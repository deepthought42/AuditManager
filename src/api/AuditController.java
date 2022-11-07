package com.looksee.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.looksee.api.exception.MissingSubscriptionException;
import com.looksee.browsing.Crawler;
import com.looksee.models.Account;
import com.looksee.models.PageState;
import com.looksee.models.UXIssueReportDto;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.PageAuditRecord;
import com.looksee.models.audit.UXIssueMessage;
import com.looksee.models.audit.performance.PerformanceInsight;
import com.looksee.models.dto.exceptions.UnknownAccountException;
import com.looksee.models.enums.ObservationType;
import com.looksee.security.SecurityConfig;
import com.looksee.services.AccountService;
import com.looksee.services.AuditRecordService;
import com.looksee.services.AuditService;
import com.looksee.services.ElementStateService;
import com.looksee.services.PageStateService;
import com.looksee.services.ReportService;
import com.looksee.services.UXIssueMessageService;
import com.looksee.utils.BrowserUtils;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *	API for interacting with {@link User} data
 */
@Controller
@RequestMapping(path = "audits", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuditController {
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(this.getClass());

   	public final static long SECS_PER_HOUR = 60 * 60;
	
	@Autowired
	private AccountService account_service;
    
    @Autowired
    protected SecurityConfig appConfig;
    
    @Autowired
    protected AuditService audit_service;
    
    @Autowired
    protected UXIssueMessageService issue_message_service;
    
    @Autowired
    protected PageStateService page_state_service;
    
    @Autowired
    protected ElementStateService element_state_service;
    
    @Autowired
    protected AuditRecordService audit_record_service;
    
    @Autowired
    protected Crawler crawler;
    
	@Autowired
	private UXIssueMessageService ux_issue_service;
    
    /**
     * Retrieves list of audits {@link Audit audits} from last 30 days
     * 
     * @param key account key
     * @return {@link PerformanceInsight insight}
     * @throws MalformedURLException 
     * @throws UnknownAccountException 
     */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Set<PageAuditRecord> getAudits(HttpServletRequest request) 
    		throws MalformedURLException, UnknownAccountException 
    {
    	Principal principal = request.getUserPrincipal();
    	String id = principal.getName();
    	Account acct = account_service.findByUserId(id);
    	
    	if(acct == null){
    		throw new UnknownAccountException();
    	}
    	else if(acct.getSubscriptionToken() == null){
    		throw new MissingSubscriptionException();
    	}    	
    	
    	return account_service.findMostRecentPageAudits(acct.getId()); 
    }

    
    /**
     * Retrieves {@link Audit audit} with given ID
     * 
     * @param id
     * @return {@link Audit audit} with given ID
     */
    @RequestMapping(method= RequestMethod.GET, path="/{id}")
    public @ResponseBody Set<Audit> getAudit(HttpServletRequest request,
									@PathVariable("id") long id)
    {
    	Set<Audit> audit_set = new HashSet<Audit>();
    	
    	Audit audit = audit_service.findById(id).get();
    	audit.setMessages( audit_service.getIssues(audit.getId()) );
        
    	audit_set.add(audit);
    	
        return audit_set;
    }
    

	/**
     * Creates a new {@link Observation observation} 
     * 
     * @return {@link PerformanceInsight insight}
     * @throws UnknownAccountException 
     */
    @RequestMapping(method = RequestMethod.POST, value="/$key/issues")
    public @ResponseBody UXIssueMessage addIssue(
							    		HttpServletRequest request,
										@PathVariable("key") String key,
							    		@RequestBody UXIssueMessage issue_message
	) throws UnknownAccountException {
    	
    	Principal principal = request.getUserPrincipal();
    	String id = principal.getName().replace("auth0|", "");
    	Account acct = account_service.findByUserId(id);

    	if(acct == null){
    		throw new UnknownAccountException();
    	}
    	

    	//find audit by key
    	//find audit by key and add recommendation
    
    	//add observation to page

    	issue_message.setKey(issue_message.generateKey());
		issue_message = issue_message_service.save( issue_message );
		audit_service.addIssue(key, issue_message.getKey());

		return issue_message;
    }

	
	@RequestMapping("/stop")
	public @ResponseBody void stopAudit(HttpServletRequest request, 
				@RequestParam(value="url", required=true) String url)
			throws MalformedURLException, UnknownAccountException 
	{
	   	Principal principal = request.getUserPrincipal();
	   	String id = principal.getName().replace("auth0|", "");
	   	Account acct = account_service.findByUserId(id);
	
	   	if(acct == null){
	   		throw new UnknownAccountException();
	   	}
	   	else if(acct.getSubscriptionToken() == null){
	   		throw new MissingSubscriptionException();
	   	}
	}
	
	/**
	 * Get Excel file for {@link AuditRecord} with the given id
	 * @param request
	 * @param domain_id
	 * @param username
	 * @param password
	 * @param role
	 * 
	 * 
	 * @throws UnknownUserException
	 * @throws UnknownAccountException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
    @RequestMapping(path="/{audit_id}/excel", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Resource> exportExcelReport(HttpServletRequest request,
    									@PathVariable(value="audit_id", required=true) long audit_id) 
    											throws UnknownAccountException, 
														FileNotFoundException, IOException {
    	Optional<AuditRecord> audit_opt = audit_record_service.findById(audit_id);
    	if(!audit_opt.isPresent()) {
    		throw new AuditRecordNotFoundException();
    	}
    	
    	List<UXIssueReportDto> ux_issues = new ArrayList<>();
		Set<Audit> audits = audit_record_service.getAllAuditsForPageAuditRecord(audit_opt.get().getId());
		PageState page = audit_record_service.getPageStateForAuditRecord(audit_opt.get().getId());	
    	for(Audit audit : audits) {
    		log.warn("audit key :: "+audit.getKey());
    		Set<UXIssueMessage> messages = audit_service.getIssues(audit.getId());
    		
    		for(UXIssueMessage message : messages) {
    			String element_selector = "";
    			if(ObservationType.ELEMENT.equals(message.getType()) 
					|| ObservationType.COLOR_CONTRAST.equals(message.getType())) {
    				element_selector = ux_issue_service.getElement(message.getId()).getCssSelector();
    			}
    			else {
    				element_selector = "No specific element is associated with this issue";
    			}
    			
    			UXIssueReportDto issue_dto = new UXIssueReportDto(message.getRecommendation(),
    															  message.getPriority(),
    															  message.getDescription(),
    															  message.getType(),
    															  message.getCategory(),
    															  message.getWcagCompliance(),
    															  message.getLabels(),
    															  audit.getWhyItMatters(),
    															  message.getTitle(),
    															  element_selector,
    															  page.getUrl());
    			ux_issues.add(issue_dto);
    		}
    		
    	}

    	URL sanitized_domain_url = new URL(BrowserUtils.sanitizeUrl(page.getUrl(), page.isSecure()));
    	XSSFWorkbook workbook = ReportService.generateExcelSpreadsheet(ux_issues, sanitized_domain_url);
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);

    		HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; " + sanitized_domain_url.getHost()+".xlsx");
            
            return ResponseEntity.ok()
            		.contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            		.cacheControl(CacheControl.noCache())
            		.headers(headers)
            		.body(new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray())));
        }
    }
}

@ResponseStatus(HttpStatus.SEE_OTHER)
class PageNotFoundError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 794045239226319408L;

	public PageNotFoundError() {
		super("Oh no! We couldn't find the page you want to audit.");
	}
}

