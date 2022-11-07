package com.looksee.actors;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.looksee.models.Account;
import com.looksee.models.PageState;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.informationarchitecture.LinksAudit;
import com.looksee.models.audit.informationarchitecture.MetadataAudit;
import com.looksee.models.audit.informationarchitecture.SecurityAudit;
import com.looksee.models.audit.informationarchitecture.TitleAndHeaderAudit;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.message.AuditError;
import com.looksee.models.message.AuditProgressUpdate;
import com.looksee.models.message.PageAuditRecordMessage;
import com.looksee.services.AuditRecordService;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

/**
 * Responsible for performing audits for {@link PageVersion}s and {@link Domain}s
 * 
 */
@Component
@Scope("prototype")
public class InformationArchitectureAuditor extends AbstractActor{
	private static Logger log = LoggerFactory.getLogger(InformationArchitectureAuditor.class.getName());

	private Cluster cluster = Cluster.get(getContext().getSystem());
	
	@Autowired
	private LinksAudit links_auditor;
	
	@Autowired
	private MetadataAudit metadata_auditor;
	
	@Autowired
	private AuditRecordService audit_record_service;
	
	@Autowired
	private TitleAndHeaderAudit title_and_header_auditor;

	@Autowired
	private SecurityAudit security_audit;
	
	private Account account;

	//subscribe to cluster changes
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
				MemberEvent.class, UnreachableMember.class);
	}

	//re-subscribe when restart
	@Override
    public void postStop() {
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
				.match(PageAuditRecordMessage.class, page_audit_record_msg -> {
					try {
						AuditRecord audit_record = audit_record_service.findById(page_audit_record_msg.getPageAuditId()).get();
						PageState page = page_audit_record_msg.getPageState(); //audit_record_service.getPageStateForAuditRecord(audit_record.getId());
						//generate audit report
						
						AuditProgressUpdate audit_update = new AuditProgressUpdate(
																	page_audit_record_msg.getAccountId(),
																	audit_record.getId(),
																	(1.0/5.0),
																	"Reviewing links",
																	AuditCategory.INFORMATION_ARCHITECTURE,
																	AuditLevel.PAGE, 
																	null,  
																	page_audit_record_msg.getDomainId());

						getContext().getParent().tell(audit_update, getSelf());

						try {
						   	Audit link_audit = links_auditor.execute(page, audit_record, null);
						   		
							AuditProgressUpdate audit_update2 = new AuditProgressUpdate(
																		page_audit_record_msg.getAccountId(),
																		audit_record.getId(),
																		(2.0/5.0),
																		"Reviewing title and header page title and header",
																		AuditCategory.INFORMATION_ARCHITECTURE,
																		AuditLevel.PAGE, 
																		link_audit,  
																		page_audit_record_msg.getDomainId());
	
							getContext().getParent().tell(audit_update2, getSelf());
						} 
						catch(Exception e) {
							AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(), 
																  page_audit_record_msg.getAccountId(), 
																  page_audit_record_msg.getAuditRecordId(), 
																  "An error occurred while reviewing title and header", 
																  AuditCategory.INFORMATION_ARCHITECTURE, 
																  (2.0/5.0));
							getContext().getParent().tell(audit_err, getSelf());
							e.printStackTrace();
						}
						
						try {
							Audit title_and_headers = title_and_header_auditor.execute(page, audit_record, null);
							
							AuditProgressUpdate audit_update3 = new AuditProgressUpdate(
																		page_audit_record_msg.getAccountId(),
																		audit_record.getId(),
																		(3.0/5.0),
																		"Checking that page is secure",
																		AuditCategory.INFORMATION_ARCHITECTURE,
																		AuditLevel.PAGE, 
																		title_and_headers,  
																		page_audit_record_msg.getDomainId());
	
							getContext().getParent().tell(audit_update3, getSelf());
						} 
						catch(Exception e) {
							AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(), 
																  page_audit_record_msg.getAccountId(), 
																  page_audit_record_msg.getAuditRecordId(), 
																  "An error occurred while reviewing page security", 
																  AuditCategory.INFORMATION_ARCHITECTURE, 
																  (3.0/5.0));
							getContext().getParent().tell(audit_err, getSelf());
							e.printStackTrace();
						}
						
						try {
							Audit security = security_audit.execute(page, audit_record, null);
							
							AuditProgressUpdate audit_update4 = new AuditProgressUpdate(
																		page_audit_record_msg.getAccountId(),
																		audit_record.getId(),
																		(4.0/5.0),
																		"Reviewing SEO",
																		AuditCategory.INFORMATION_ARCHITECTURE,
																		AuditLevel.PAGE, 
																		security,  
																		page_audit_record_msg.getDomainId());
							
							getSender().tell(audit_update4, getSelf());
						} 
						catch(Exception e) {
							AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(), 
																  page_audit_record_msg.getAccountId(), 
																  page_audit_record_msg.getAuditRecordId(), 
																  "An error occurred while reviewing SEO", 
																  AuditCategory.INFORMATION_ARCHITECTURE, 
																  (4.0/5.0));
							getContext().getParent().tell(audit_err, getSelf());
							e.printStackTrace();
						}
						
						try {
							Audit metadata = metadata_auditor.execute(page, audit_record, null);
							
							AuditProgressUpdate audit_update5 = new AuditProgressUpdate(
																		page_audit_record_msg.getAccountId(),
																		audit_record.getId(),
																		1.0,
																		"Completed information architecture audit",
																		AuditCategory.INFORMATION_ARCHITECTURE,
																		AuditLevel.PAGE, 
																		metadata,  
																		page_audit_record_msg.getDomainId());
							
							getSender().tell(audit_update5, getSelf());
						}
						catch(Exception e) {
							AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(), 
																  page_audit_record_msg.getAccountId(), 
																  page_audit_record_msg.getAuditRecordId(), 
																  "An error occurred while reviewing metadata", 
																  AuditCategory.INFORMATION_ARCHITECTURE, 
																  1.0);
							getContext().getParent().tell(audit_err, getSelf());
							e.printStackTrace();
						}
						
					} catch(Exception e) {
						log.warn("exception caught during Information Architecture audit");
						e.printStackTrace();
						log.warn("-------------------------------------------------------------");
						log.warn("-------------------------------------------------------------");
						log.warn("THERE WAS AN ISSUE DURING INFO ARCHITECTURE AUDIT");
						log.warn("-------------------------------------------------------------");
						log.warn("-------------------------------------------------------------");
						
					}
					finally {

						AuditProgressUpdate audit_update5 = new AuditProgressUpdate(
								page_audit_record_msg.getAccountId(),
								page_audit_record_msg.getAuditRecordId(),
								1.0,
								"Completed information architecture audit",
								AuditCategory.INFORMATION_ARCHITECTURE,
								AuditLevel.PAGE,
								null,  
								page_audit_record_msg.getDomainId());

						getSender().tell(audit_update5, getSelf());
					}
				})
				.match(MemberUp.class, mUp -> {
					log.debug("Member is Up: {}", mUp.member());
				})
				.match(UnreachableMember.class, mUnreachable -> {
					log.warn("Member detected as unreachable: {}", mUnreachable.member());
				})
				.match(MemberRemoved.class, mRemoved -> {
					log.debug("Member is Removed: {}", mRemoved.member());
				})
				.matchAny(o -> {
					log.warn("received unknown message of type :: "+o.getClass().getName());
				})
				.build();
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	
}
