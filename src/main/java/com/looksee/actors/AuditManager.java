package com.looksee.actors;

import static com.looksee.config.SpringExtension.SpringExtProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.looksee.models.Account;
import com.looksee.models.Domain;
import com.looksee.models.PageState;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.PageAuditRecord;
import com.looksee.models.enums.ExecutionStatus;
import com.looksee.models.enums.SubscriptionPlan;
import com.looksee.models.journeys.Step;
import com.looksee.models.message.ExceededSubscriptionMessage;
import com.looksee.models.message.Message;
import com.looksee.models.message.PageAuditRecordMessage;
import com.looksee.models.message.PageDataExtractionError;
import com.looksee.models.message.PageDataExtractionMessage;
import com.looksee.services.AccountService;
import com.looksee.services.AuditRecordService;
import com.looksee.services.DomainService;
import com.looksee.services.SubscriptionService;
import com.looksee.utils.BrowserUtils;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

/**
 * 
 * 
 */
@Component
@Scope("prototype")
public class AuditManager extends AbstractActor{
	private static Logger log = LoggerFactory.getLogger(AuditManager.class.getName());

	private Cluster cluster = Cluster.get(getContext().getSystem());

	private Account account = null;
	
	@Autowired
	private ActorSystem actor_system;
	
	@Autowired
	private DomainService domain_service;
	
	@Autowired
	private AccountService account_service;
	
	@Autowired
	private AuditRecordService audit_record_service;
	
	@Autowired
	private SubscriptionService subscription_service;
	
	private boolean is_auditing_complete = false;

	private Domain domain = null;

	//subscription tracking
	
	//PROGRESS TRACKING VARIABLES
	double journey_mapping_progress = 0.0;
	
	//subscribe to cluster changes
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
				MemberEvent.class, UnreachableMember.class);
	}

	//re-subscribe when restart
	@Override
    public void postStop() {
		log.error("Something happened that caused AuditManager to stop");
		cluster.unsubscribe(getSelf());
    }

	/**
	 * {@inheritDoc}
	 *
	 * NOTE: Do not change the order of the checks for instance of below. These are in this order because ExploratoryPath
	 * 		 is also a Test and thus if the order is reversed, then the ExploratoryPath code never runs when it should
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws NoSuchElementException
	 */
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				/*
				.match(PageCrawlActionMessage.class, message-> {
					//HANDLE SINGLE PAGE AUDIT ACTION
					if(message.getAction().equals(CrawlAction.START)){
						this.is_domain_audit = false;
						log.warn("starting single page audit for  :: "+message.getUrl());
						PageAuditRecord page_audit_record = (PageAuditRecord)audit_record_service.findById(message.getAuditRecordId()).get();
						
						PageCrawlActionMessage crawl_action_msg = new PageCrawlActionMessage(CrawlAction.START, 
																							 message.getDomainId(),
																							 message.getAccountId(), 
																							 page_audit_record, 
																							 message.getUrl()); 
						
						
						ActorRef page_state_builder = getContext().actorOf(SpringExtProvider.get(actor_system)
					   			.props("pageStateBuilder"), "pageStateBuilder"+UUID.randomUUID());
						page_state_builder.tell(crawl_action_msg, getSelf());					
					}
					else if(message.getAction().equals(CrawlAction.STOP)){
						stopAudit(message);
					}
				})
				*/
				/*
				.match(CrawlActionMessage.class, message-> {
					if(message.getAction().equals(CrawlAction.START)){
						log.warn("starting domain audit");
						this.is_domain_audit = true;
						this.domain_audit_id = message.getAuditRecordId();
						//send message to webCrawlerActor to get pages
						
						ActorRef crawler_actor = getContext().actorOf(SpringExtProvider.get(actor_system)
								  .props("crawlerActor"), "crawlerActor"+UUID.randomUUID());
						crawler_actor.tell(message, getSelf());

					}
					else if(message.getAction().equals(CrawlAction.STOP)){
						stopAudit();
					}
					
				})
				*/
				/*
				.match(JourneyCrawlActionMessage.class, message-> {
					if(message.getAction().equals(CrawlAction.START)){
						this.is_domain_audit = true;
						this.domain_audit_id = message.getAuditRecordId();
						//send message to webCrawlerActor to get pages
						
						ActorRef crawler_actor = getContext().actorOf(SpringExtProvider.get(actor_system)
								  .props("crawlerActor"), "crawlerActor"+UUID.randomUUID());
						crawler_actor.tell(message, getSelf());

					}
					else if(message.getAction().equals(CrawlAction.STOP)){
						stopAudit();
					}
					
				})
				*/
				/*
				.match(PageCandidateFound.class, message -> {
					log.warn("Page candidate found message recieved by AUDIT MANAGER");
					try {
						String url_without_protocol = BrowserUtils.getPageUrl(message.getUrl());
						if(!this.page_urls.containsKey(url_without_protocol)) {
	
							this.total_pages++;
							DomainAuditRecord domain_audit_record = (DomainAuditRecord)audit_record_service.findById(message.getAuditRecordId()).get();						
							domain_audit_record.setTotalPages( this.page_urls.keySet().size());
							audit_record_service.save(domain_audit_record, message.getAccountId(), message.getDomainId());
							
							if(this.account == null) {
								this.account = account_service.findById(message.getAccountId()).get();
							}
					    	//int page_audit_cnt = audit_record_service.getPageAuditCount(message.getAuditRecordId());
							
					    	SubscriptionPlan plan = SubscriptionPlan.create(account.getSubscriptionType());

							if(!subscription_service.hasExceededDomainPageAuditLimit(plan, page_urls.size())) {
								//Account is still within page limit. continue with mapping page 
								PageAuditRecord audit_record = new PageAuditRecord(ExecutionStatus.BUILDING_PAGE, 
																					new HashSet<>(), 
																					null, 
																					true);
								audit_record.setUrl(url_without_protocol);
								audit_record.setDataExtractionProgress(1/50.0);
								audit_record.setDataExtractionMsg("Creating page record for "+url_without_protocol);
								audit_record.setAestheticMsg("Waiting for data extraction ...");
								audit_record.setAestheticAuditProgress(0.0);
							   	audit_record.setContentAuditMsg("Waiting for data extraction ...");
							   	audit_record.setContentAuditProgress(0.0);
							   	audit_record.setInfoArchMsg("Waiting for data extraction ...");
							   	audit_record.setInfoArchitectureAuditProgress(0.0);
							   	
							   	audit_record = (PageAuditRecord)audit_record_service.save(audit_record, 
							   														   	  message.getAccountId(), 
							   														   	  message.getDomainId());
							   	
							   	audit_record_service.addPageAuditToDomainAudit(message.getAuditRecordId(), 
							   												   audit_record.getKey());
								
								PageCrawlActionMessage crawl_action_msg = new PageCrawlActionMessage(CrawlAction.START,
																									 message.getDomainId(),
																									 message.getAccountId(), 
																									 audit_record, 
																									 message.getUrl());
								
								ActorRef page_state_builder = getContext().actorOf(SpringExtProvider.get(actor_system)
							   											  .props("pageStateBuilder"), "pageStateBuilder"+UUID.randomUUID());
								page_state_builder.tell(crawl_action_msg, getSelf());
							}
							page_urls.put(url_without_protocol, Boolean.TRUE);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				})
				*/
				.match(PageDataExtractionError.class, message -> {
					log.warn("Error occurred while extracting page state for url "+message.getUrl()+";    error = "+message.getErrorMessage());
				})
				/*
				.match(ElementsSaved.class, message -> {
					PageState page_state = page_state_service.findById(message.getPageStateId()).get();

					PageAuditRecordMessage audit_record_msg = new PageAuditRecordMessage(
																	message.getAuditRecordId(), 
																	message.getDomainId(), 
																	message.getAccountId(), 
																	message.getAuditRecordId(),
																	page_state);
					
					ActorRef content_auditor = getContext().actorOf(SpringExtProvider.get(actor_system)
		   											.props("contentAuditor"), "contentAuditor"+UUID.randomUUID());
					content_auditor.tell(audit_record_msg, getSelf());							

					ActorRef info_architecture_auditor = getContext().actorOf(SpringExtProvider.get(actor_system)
					   			.props("informationArchitectureAuditor"), "informationArchitectureAuditor"+UUID.randomUUID());
					info_architecture_auditor.tell(audit_record_msg, getSelf());

					ActorRef aesthetic_auditor = getContext().actorOf(SpringExtProvider.get(actor_system)
								.props("aestheticAuditor"), "aestheticAuditor"+UUID.randomUUID());		
					aesthetic_auditor.tell(audit_record_msg, getSelf());
				})
				*/
				.match(PageDataExtractionMessage.class, message -> {
					
					if(message.getDomainId() == -1 && message.getAccountId() == -1) {
						AuditRecord audit_record = audit_record_service.findById(message.getAuditRecordId()).get();
						markDomainAuditComplete(audit_record, message);
					}
					else {
					
						if(this.account == null) {
							this.account = account_service.findById(message.getAccountId()).get();
						}
						if(this.domain == null) {
							domain = domain_service.findById(message.getDomainId()).get();
						}
						
						PageState page_state = message.getPageState();
						
						//AuditRecord audit_record = audit_record_service.findById(message.getAuditRecordId()).get();
						Set<PageAuditRecord> page_audit_records = audit_record_service.getAllPageAudits(message.getAuditRecordId());
						HashMap<String, Boolean> audited_urls = new HashMap<>();
						for(PageAuditRecord record : page_audit_records) {
							audited_urls.put(record.getUrl(), Boolean.TRUE);
						}
						
						if(!audited_urls.containsKey(page_state.getUrl())
								&& !BrowserUtils.isExternalLink(domain.getUrl(), page_state.getUrl())) 
						{
							log.warn("Starting audit process for "+page_state.getUrl());
					    	SubscriptionPlan plan = SubscriptionPlan.create(this.account.getSubscriptionType());
	
							if(!subscription_service.hasExceededDomainPageAuditLimit(plan, audited_urls.size())) {
								//is_auditing_complete = false;
								//TODO: SEND PUBSUB MESSAGE THAT AUDITS WERE INITIATED
								initiatePageAudits(page_state, message);
							}
							else {
								//is_data_extraction_complete = true;
								log.warn("+++++++++++++++++++++++++++++++++++++++");
								log.warn("User has exceeded domain page audit limit");
								log.warn("+++++++++++++++++++++++++++++++++++++++");
								AuditRecord audit_record = audit_record_service.findById(message.getAuditRecordId()).get();
								
								if(is_auditing_complete) {
									markDomainAuditComplete(audit_record, message);
								}
							}
							audited_urls.put(page_state.getUrl(), Boolean.TRUE);
						}
					}
				})
				/*
				.match(ConfirmedJourneyMessage.class, message -> {
					try {
						//save journey steps
						List<Step> saved_steps = new ArrayList<>();
						for(Step step : message.getSteps()) {
							saved_steps.add(step_service.save(step));
						}
						
						//build create and save journey
						Journey journey = new Journey(saved_steps);
						journey = journey_service.save(journey);
						
						//add journey to domain audit
						audit_record_service.addJourney(this.domain_audit_id, journey.getId());
						
						//retrieve all unique page states for journey steps
						List<PageState> page_states = getAllUniquePageStates(saved_steps);
						//remove all unique pageStates that have already been analyzed
						
						//create PageAuditRecord for each page that hasn't been analyzed.
						if(this.account == null) {
							this.account = account_service.findById(message.getAccountId()).get();
						}
						if(this.domain == null) {
							domain = domain_service.findById(message.getDomainId()).get();
						}
						
				    	//For each page audit record perform audits
						for(PageState page_state : page_states) {
							String url_without_protocol = page_state.getUrl();
	
							if(!page_urls.containsKey(page_state.getUrl())
									&& !BrowserUtils.isExternalLink(domain.getUrl(), page_state.getUrl())) 
							{
								log.warn("Starting audit process for "+url_without_protocol);
						    	SubscriptionPlan plan = SubscriptionPlan.create(this.account.getSubscriptionType());

								if(!subscription_service.hasExceededDomainPageAuditLimit(plan, page_urls.size())) {
									page_urls.put(url_without_protocol, Boolean.TRUE);
									total_pages_audited++;
									is_auditing_complete = false;
									initiatePageAudits(page_state, message);
								}
								else {
									page_urls.put(url_without_protocol, Boolean.TRUE);
									log.warn("+++++++++++++++++++++++++++++++++++++++");
									log.warn("User has exceeded domain page audit limit");
									log.warn("+++++++++++++++++++++++++++++++++++++++");
									is_data_extraction_complete = true;
									getSender().tell(PoisonPill.getInstance(), getSelf());
									DomainAuditRecord domain_audit = (DomainAuditRecord)audit_record_service.findById(message.getAuditRecordId()).get();
									if(is_auditing_complete) {
										markDomainAuditComplete(domain_audit, message);
										mail_service.sendDomainAuditCompleteEmail(account.getEmail(), domain.getUrl(), domain.getId());
									}
									break;
								}
							}
							else {
								log.warn("page state has already been audited ::  "+url_without_protocol);
							}
						}

					}catch(Exception e) {
						log.warn("an exception occurred while AuditManager processing ConfirmedJourneyMessage");
						e.printStackTrace();
					}
				})
				*/
				/*
				.match(JourneyExaminationProgressMessage.class, message -> {
					double journey_mapping_progress = message.getExaminedJourneys()/(double)message.getGeneratedJourneys();

					if(this.account == null) {
						this.account = account_service.findById(message.getAccountId()).get();
					}
					SubscriptionPlan plan = SubscriptionPlan.create(account.getSubscriptionType());

					AuditRecord audit_record = audit_record_service.findById(message.getAuditRecordId()).get();
					
					if(subscription_service.hasExceededDomainPageAuditLimit(plan, page_urls.size()) 
							|| message.getExaminedJourneys()==message.getGeneratedJourneys()) 
					{
						is_data_extraction_complete = true;
						if(is_auditing_complete) {
							markDomainAuditComplete(audit_record, message);
							mail_service.sendDomainAuditCompleteEmail(account.getEmail(), domain.getUrl(), domain.getId());
						}
						getSender().tell(PoisonPill.class, getSelf());
						audit_record.setDataExtractionProgress(1.0);
					}
					else {
						is_data_extraction_complete = false;
						//set progress for audit record journey mapping
						audit_record.setDataExtractionProgress(journey_mapping_progress);
					}
					
					audit_record_service.save(audit_record, message.getAccountId(), message.getDomainId());
				})
				.match(AuditProgressUpdate.class, message -> {
					try {
						AuditRecord audit_record = audit_record_service.updateAuditProgress(message.getAuditRecordId(), 
																							message.getCategory(), 
																							message.getAccountId(), 
																							message.getDomainId(), 
																							message.getProgress(), 
																							message.getMessage());	
	
						if(message.getAudit() != null) {
							audit_record_service.addAudit( audit_record.getId(), message.getAudit().getId() );							
						}
						
						if(this.is_domain_audit) {
							//extract color from page audits for color palette
							
							try {
								if(audit_record instanceof PageAuditRecord) {
									audit_record = audit_record_service.getDomainAuditRecordForPageRecord(audit_record.getId()).get();
									//audit_record = audit_record_service.findById(audit_record.getId()).get();
								}
								if(this.account == null) {
									this.account = account_service.findById(message.getAccountId()).get();
								}
								//SubscriptionPlan plan = SubscriptionPlan.create(account.getSubscriptionType());

								//if(subscription_service.hasExceededDomainPageAuditLimit(plan, page_urls.size())) {
								if( audit_record_service.isDomainAuditComplete( audit_record)) {
									is_auditing_complete = true;
									if(this.domain == null) {
										domain = domain_service.findById(message.getDomainId()).get();
									}
									if(is_data_extraction_complete) {
										markDomainAuditComplete(audit_record, message);
										log.warn("Domain audit is complete(part 2) :: "+audit_record.getId());
										log.warn("Domain id :: "+message.getDomainId());
										mail_service.sendDomainAuditCompleteEmail(account.getEmail(), domain.getUrl(), domain.getId());
									}
								}
							}
							catch(Exception e) {
								e.printStackTrace();
							}
						}
						else if(audit_record instanceof PageAuditRecord){
							boolean is_page_audit_complete = AuditUtils.isPageAuditComplete(audit_record);						
							if(is_page_audit_complete) {
								audit_record.setEndTime(LocalDateTime.now());
								audit_record.setStatus(ExecutionStatus.COMPLETE);
								audit_record = audit_record_service.save(audit_record, message.getAccountId(), message.getDomainId());	
							
								PageState page = audit_record_service.getPageStateForAuditRecord(audit_record.getId());								
								if(this.account == null) {
									this.account = account_service.findById(message.getAccountId()).get();
								}
								log.warn("sending email to account :: "+account.getEmail());
								mail_service.sendPageAuditCompleteEmail(account.getEmail(), page.getUrl(), audit_record.getId());
							}
						}
					} catch(Exception e) {
						log.warn("failed to retrieve audit record with id : "+message.getAuditRecordId());
						e.printStackTrace();
					}
				})
				*/
				.match(ExceededSubscriptionMessage.class, message -> {
					log.warn("subscription limits exceeded.");
					//TODO STOP AUDIT
				})
				/*
				.match(AuditError.class, message -> {
					AuditRecord audit_record = audit_record_service.findById(message.getAuditRecordId()).get();
					audit_record.setStatus(ExecutionStatus.ERROR);

					if(AuditCategory.CONTENT.equals(message.getAuditCategory())) {
						audit_record.setContentAuditProgress( message.getProgress() );
						audit_record.setContentAuditMsg( message.getErrorMessage());
					}
					else if(AuditCategory.AESTHETICS.equals(message.getAuditCategory())) {
						audit_record.setAestheticAuditProgress( message.getProgress() );
						audit_record.setAestheticMsg(message.getErrorMessage());
					}
					else if(AuditCategory.INFORMATION_ARCHITECTURE.equals(message.getAuditCategory())) {
						audit_record.setInfoArchitectureAuditProgress( message.getProgress() );
						audit_record.setInfoArchMsg(message.getErrorMessage());
					}
					
					audit_record_service.save(audit_record, message.getAccountId(), message.getDomainId());
				})
				*/
				.match(MemberUp.class, mUp -> {
					log.debug("Member is Up: {}", mUp.member());
				})
				.match(UnreachableMember.class, mUnreachable -> {
					log.debug("Member detected as unreachable: {}", mUnreachable.member());
				})
				.match(MemberRemoved.class, mRemoved -> {
					log.debug("Member is Removed: {}", mRemoved.member());
				})
				.matchAny(o -> {
					log.debug("received unknown message of type :: "+o.getClass().getName());
				})
				.build();
	}
	
	/**
	 * Marks domain {@link AuditRecord} as complete and sends email to user
	 * @param audit_record
	 * @param domain2
	 * @param account2
	 */
	private AuditRecord markDomainAuditComplete(AuditRecord audit_record, 
												Message message) 
	{
		audit_record.setContentAuditProgress(1.0);
		audit_record.setAestheticAuditProgress(1.0);
		audit_record.setDataExtractionProgress(1.0);
		audit_record.setInfoArchitectureAuditProgress(1.0);
		audit_record.setEndTime(LocalDateTime.now());
		audit_record.setStatus(ExecutionStatus.COMPLETE);
		return audit_record_service.save(audit_record, 
										  message.getAccountId(), 
										  message.getDomainId());	
	}

	/**
	 * Initiates Visual Design, Content, and Information Architecture audits
	 * 
	 * @param page_state
	 * @param message
	 */
	private void initiatePageAudits(PageState page_state, Message message) {
		//Account is still within page limit. continue with mapping page 
		PageAuditRecord page_audit = new PageAuditRecord(ExecutionStatus.BUILDING_PAGE, 
															new HashSet<>(), 
															null, 
															true);
		
		page_audit.setUrl(page_state.getUrl());
		page_audit.setDataExtractionProgress(1/50.0);
		page_audit.setDataExtractionMsg("Creating page record for "+page_state.getUrl());
		page_audit.setAestheticMsg("Waiting for data extraction ...");
		page_audit.setAestheticAuditProgress(0.0);
		page_audit.setContentAuditMsg("Waiting for data extraction ...");
	   	page_audit.setContentAuditProgress(0.0);
	   	page_audit.setInfoArchMsg("Waiting for data extraction ...");
	   	page_audit.setInfoArchitectureAuditProgress(0.0);
	   	
	   	page_audit = (PageAuditRecord)audit_record_service.save(page_audit, 
	   														   	  message.getAccountId(), 
	   														   	  message.getDomainId());
	   	
	   	audit_record_service.addPageToAuditRecord(page_audit.getId(), page_state.getId());
	   	audit_record_service.addPageAuditToDomainAudit(message.getAuditRecordId(), 
	   													page_audit.getKey());
	   	
		PageAuditRecordMessage audit_record_msg = new PageAuditRecordMessage(
														page_audit.getId(), 
														message.getDomainId(), 
														message.getAccountId(), 
														message.getAuditRecordId(),
														page_state);
		
		ActorRef content_auditor = getContext().actorOf(SpringExtProvider.get(actor_system)
											.props("contentAuditor"), "contentAuditor"+UUID.randomUUID());

		content_auditor.tell(audit_record_msg, getSelf());							

		ActorRef info_architecture_auditor = getContext().actorOf(SpringExtProvider.get(actor_system)
		   			.props("informationArchitectureAuditor"), "informationArchitectureAuditor"+UUID.randomUUID());
		info_architecture_auditor.tell(audit_record_msg, getSelf());

		ActorRef aesthetic_auditor = getContext().actorOf(SpringExtProvider.get(actor_system)
					.props("aestheticAuditor"), "aestheticAuditor"+UUID.randomUUID());		
		aesthetic_auditor.tell(audit_record_msg, getSelf());
	}

	/**
	 * Retrieves all {@link PageState PageStates} from {@link Step steps} provided and returns a unique list of PageStates
	 * 
	 * @param steps
	 * 
	 * @return List of PageState objects
	 */
	private List<PageState> getAllUniquePageStates(List<Step> steps) {
		List<PageState> page_states = new ArrayList<>();
		Map<String, Boolean> key_map = new HashMap<>();
		
		for(Step step : steps) {
			if(!key_map.containsKey( step.getStartPage().getKey() )){
				key_map.put(step.getStartPage().getKey(), Boolean.TRUE);				
				page_states.add(step.getStartPage());
			}
			
			if(!key_map.containsKey( step.getEndPage().getKey() )){
				key_map.put(step.getEndPage().getKey(), Boolean.TRUE);				
				page_states.add(step.getEndPage());
			}
		}
		
		return page_states;
	}
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
