package com.looksee.api;

import static com.looksee.config.SpringExtension.SpringExtProvider;

import java.net.URL;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.looksee.api.exception.PaymentDueException;
import com.looksee.browsing.Crawler;
import com.looksee.models.Account;
import com.looksee.models.PageState;
import com.looksee.models.SimplePage;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.PageAuditRecord;
import com.looksee.models.audit.performance.PerformanceInsight;
import com.looksee.models.dto.exceptions.UnknownAccountException;
import com.looksee.models.enums.CrawlAction;
import com.looksee.models.enums.ExecutionStatus;
import com.looksee.models.enums.SubscriptionPlan;
import com.looksee.models.message.PageCrawlActionMessage;
import com.looksee.services.AccountService;
import com.looksee.services.AuditRecordService;
import com.looksee.services.AuditService;
import com.looksee.services.PageStateService;
import com.looksee.services.SubscriptionService;
import com.looksee.utils.BrowserUtils;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 *	API for interacting with {@link User} data
 */
@RestController
@RequestMapping("/auditor")
public class AuditorController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountService account_service;
	
	@Autowired
	private PageStateService page_service;
    
    @Autowired
    private ActorSystem actor_system;
    
    @Autowired
    protected AuditRecordService audit_record_service;
    
    @Autowired
    protected Crawler crawler;
    
    @Autowired
    protected AuditService audit_service;
    
	@Autowired
	private SubscriptionService subscription_service;
	
	/**
     * 
     * @param request
     * @param page
     * @return
     * @throws Exception
     */
	@RequestMapping(path="/start-individual", method = RequestMethod.POST)
	public @ResponseBody AuditRecord startSinglePageAudit(
			HttpServletRequest request,
			@RequestBody(required=true) PageState page
	) throws Exception {
		Principal principal = request.getUserPrincipal();
		Account account = null;
    	
		//is user logged in and have they exceeded the page audit limit??
		if(principal != null ) {
			account = account_service.findByUserId(principal.getName());
			SubscriptionPlan plan = SubscriptionPlan.create(account.getSubscriptionType());
			LocalDate today = LocalDate.now();
			
			int page_audit_cnt = account_service.getPageAuditCountByMonth(account.getId(), today.getMonthValue());

			if( subscription_service.hasExceededSinglePageAuditLimit(plan, page_audit_cnt) ) {				
	    		throw new PaymentDueException("Your plan has 0 page audits left. Please upgrade to perform more audits");
			}
		}
		
    	String lowercase_url = page.getUrl().toLowerCase();

    	URL sanitized_url = new URL(BrowserUtils.sanitizeUserUrl(lowercase_url ));
    	
	   	//create new audit record
	   	PageAuditRecord audit_record = new PageAuditRecord(ExecutionStatus.IN_PROGRESS, 
	   														new HashSet<>(), 
	   														null, 
	   														false);
		audit_record.setUrl(sanitized_url.toString());
	   	audit_record.setDataExtractionMsg("loading page");
	   	audit_record.setDataExtractionProgress(1.0/50.0);
	   	audit_record.setAestheticMsg("Waiting for data extraction ...");
	   	audit_record.setAestheticAuditProgress(0.0);
	   	audit_record.setContentAuditMsg("Waiting for data extraction ...");
	   	audit_record.setContentAuditProgress(0.0);
	   	audit_record.setInfoArchMsg("Waiting for data extraction ...");
	   	audit_record.setInfoArchitectureAuditProgress(0.0);
	   	audit_record = (PageAuditRecord)audit_record_service.save(audit_record, null, null);
	   	
	   	long account_id = -1;
		if(account != null) {
	    	account_service.addAuditRecord(account.getEmail(), audit_record.getId());
			account_id = account.getId();			
		}
		
		PageCrawlActionMessage start_single_page_audit = new PageCrawlActionMessage(CrawlAction.START, 
																					 -1L, 
																					 account_id,
																					 audit_record,
																					 sanitized_url);
		
		
		log.warn("Initiating single page audit = "+audit_record.getId());
		ActorRef audit_manager = actor_system.actorOf(SpringExtProvider.get(actor_system)
	   												.props("singlePageAuditManager"), "singlePageAuditManager"+UUID.randomUUID());
		audit_manager.tell(start_single_page_audit, ActorRef.noSender());
		
   		return audit_record;
	}
	
	
    
    /**
     * Retrieves list of {@link PerformanceInsight insights} with a given key
     * 
     * @param key account key
     * @return {@link PerformanceInsight insight}
     * @throws UnknownAccountException 
     */
    @RequestMapping(method = RequestMethod.GET)
    public SimplePage getPage(HttpServletRequest request,
			@RequestParam(value="url", required=true) String url
	)  {
    	PageState page = page_service.findByUrl(url);
    	
        log.info("finding page :: "+page.getKey());
        
        SimplePage simple_page = new SimplePage(
        								page.getUrl(), 
        								page.getViewportScreenshotUrl(), 
        								page.getFullPageScreenshotUrlOnload(), 
        								page.getFullPageScreenshotUrlComposite(), 
        								page.getFullPageWidth(),
        								page.getFullPageHeight(),
        								page.getSrc(), 
        								page.getKey(), page.getId());	
        return simple_page;
    }
    
    /**
     * Retrieves list of {@link PerformanceInsight insights} with a given key
     * 
     * @param key account key
     * @return {@link PerformanceInsight insight}
     * @throws UnknownAccountException 
     */
    @PreAuthorize("hasAuthority('read:actions')")
    @RequestMapping(method = RequestMethod.GET, path="/$page_key/insights")
    public PerformanceInsight getInsights(HttpServletRequest request,
			@PathVariable(value="page_key", required=true) String page_key
	) throws UnknownAccountException {
    	Principal principal = request.getUserPrincipal();
    	String id = principal.getName();
    	Account acct = account_service.findByUserId(id);

    	if(acct == null){
    		throw new UnknownAccountException();
    	}
    	
        log.info("finding all page insights :: "+page_key);
        return null; //page_service.getAuditInsights(page_state_key);
    }
    
}


@ResponseStatus(HttpStatus.SEE_OTHER)
class AccountLimitExceededException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8549092797919036363L;

	public AccountLimitExceededException() {
		super("You have exceeded the number of single page audits that are available for your plan. Upgrade to get access to more audits.");
	}
}