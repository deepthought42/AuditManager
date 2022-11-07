package com.looksee.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.looksee.models.Account;
import com.looksee.models.PageState;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.ImageAudit;
import com.looksee.models.audit.ImagePolicyAudit;
import com.looksee.models.audit.ParagraphingAudit;
import com.looksee.models.audit.content.ImageAltTextAudit;
import com.looksee.models.audit.content.ReadabilityAudit;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.message.AuditError;
import com.looksee.models.message.AuditProgressUpdate;
import com.looksee.models.message.PageAuditRecordMessage;
import com.looksee.services.AuditRecordService;
import com.looksee.services.PageStateService;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

/**
 * Responsible for performing audits for {@link PageVersion}s and
 * {@link Domain}s
 * 
 */
@Component
@Scope("prototype")
public class ContentAuditor extends AbstractActor {
	private static Logger log = LoggerFactory.getLogger(ContentAuditor.class.getName());

	private Cluster cluster = Cluster.get(getContext().getSystem());

	@Autowired
	private ImageAltTextAudit image_alt_text_auditor;

	@Autowired
	private ParagraphingAudit paragraph_auditor;

	@Autowired
	private ReadabilityAudit readability_auditor;

	@Autowired
	private PageStateService page_state_service;

	@Autowired
	private AuditRecordService audit_record_service;

	@Autowired
	private ImageAudit image_audit;

	@Autowired
	private ImagePolicyAudit image_policy_audit;
	
	private Account account;

	// subscribe to cluster changes
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
	}

	// re-subscribe when restart
	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Receive createReceive() {
		return receiveBuilder().match(PageAuditRecordMessage.class, page_audit_record_msg -> {
			try {
				AuditRecord audit_record = audit_record_service.findById(page_audit_record_msg.getPageAuditId())
																.get();
				//PageState page = audit_record_service.getPageStateForAuditRecord(audit_record.getId());
				// generate audit report
				// Set<Audit> audits = new HashSet<>();
				//long page_id = page.getId();
				PageState page = page_audit_record_msg.getPageState();
				page.setElements(page_state_service.getElementStates(page.getId()));
				log.warn("evaluating "+page.getElements().size()+" element state for content audit with page ID :: "+page.getId());
				AuditProgressUpdate audit_update = new AuditProgressUpdate(page_audit_record_msg.getAccountId(),
																			audit_record.getId(), 
																			(1.0 / 6.0), 
																			"checking images for alt text", 
																			AuditCategory.CONTENT,
																			AuditLevel.PAGE, 
																			null, 
																			page_audit_record_msg.getDomainId());

				getContext().getParent().tell(audit_update, getSelf());

				try {
					Audit alt_text_audit = image_alt_text_auditor.execute(page, audit_record, null);
					AuditProgressUpdate audit_update2 = new AuditProgressUpdate(page_audit_record_msg.getAccountId(),
																				audit_record.getId(), 
																				(2.0 / 6.0), 
																				"Reviewing content for readability",
																				AuditCategory.CONTENT, 
																				AuditLevel.PAGE, 
																				alt_text_audit,
																				page_audit_record_msg.getDomainId());

					getContext().getParent().tell(audit_update2, getSelf());
				} catch (Exception e) {
					AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(),
														  page_audit_record_msg.getAccountId(), 
														  page_audit_record_msg.getAuditRecordId(),
													  	  "An error occurred while reviewing image alt-text", 
													  	  AuditCategory.CONTENT, 
													  	  (2.0 / 6.0));
					getContext().getParent().tell(audit_err, getSelf());
					e.printStackTrace();
				}

				try {
					Audit readability_audit = readability_auditor.execute(page, audit_record, null);
					AuditProgressUpdate audit_update3 = new AuditProgressUpdate(page_audit_record_msg.getAccountId(),
																				audit_record.getId(), 
																				(3.0 / 6.0), 
																				"Reviewing paragraph length", 
																				AuditCategory.CONTENT,
																				AuditLevel.PAGE, 
																				readability_audit, 
																				page_audit_record_msg.getDomainId());

					getSender().tell(audit_update3, getSelf());
				} catch (Exception e) {
					AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(),
														  page_audit_record_msg.getAccountId(), 
														  page_audit_record_msg.getAuditRecordId(),
														  "An error occurred while reviewing readability", 
														  AuditCategory.CONTENT, 
														  (3.0 / 6.0));
					getContext().getParent().tell(audit_err, getSelf());
					e.printStackTrace();
				}

				try {
					Audit paragraph_audit = paragraph_auditor.execute(page, audit_record, null);
					AuditProgressUpdate audit_update4 = new AuditProgressUpdate(page_audit_record_msg.getAccountId(),
																				audit_record.getId(), 
																				(4.0/6.0), 
																				"Content Audit Compelete!", 
																				AuditCategory.CONTENT,
																				AuditLevel.PAGE, 
																				paragraph_audit, 
																				page_audit_record_msg.getDomainId());

					getContext().getParent().tell(audit_update4, getSelf());
				} catch (Exception e) {
					AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(),
														  page_audit_record_msg.getAccountId(),
														  page_audit_record_msg.getAuditRecordId(),
														  "An error occurred while reviewing paragraph structure", 
														  AuditCategory.CONTENT, 
														  (4.0/6.0));
					getContext().getParent().tell(audit_err, getSelf());
					e.printStackTrace();
				}

				try {
					Audit image_copyright_audit = image_audit.execute(page, audit_record, null);
					AuditProgressUpdate audit_update5 = new AuditProgressUpdate(page_audit_record_msg.getAccountId(),
																				audit_record.getId(), 
																				(5.0 / 6.0), 
																				"Reviewing images for uniqueness", 
																				AuditCategory.CONTENT,
																				AuditLevel.PAGE, 
																				image_copyright_audit, 
																				page_audit_record_msg.getDomainId());

					getContext().getParent().tell(audit_update5, getSelf());
				} catch (Exception e) {
					AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(),
							page_audit_record_msg.getAccountId(), page_audit_record_msg.getAuditRecordId(),
							"An error occurred while reviewing images for uniqueness", AuditCategory.CONTENT,
							(5.0 / 6.0));
					getContext().getParent().tell(audit_err, getSelf());
					e.printStackTrace();
				}
				
				try {
					Audit image_policy_result = image_policy_audit.execute(page, audit_record, null);
					AuditProgressUpdate audit_update6 = new AuditProgressUpdate(page_audit_record_msg.getAccountId(),
																				audit_record.getId(), 
																				1, 
																				"Reviewing images for compliance with domain policy", 
																				AuditCategory.CONTENT,
																				AuditLevel.PAGE, 
																				image_policy_result, 
																				page_audit_record_msg.getDomainId());

					getContext().getParent().tell(audit_update6, getSelf());
				} catch (Exception e) {
					AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(),
							page_audit_record_msg.getAccountId(), page_audit_record_msg.getAuditRecordId(),
							"An error occurred while reviewing images for uniqueness", AuditCategory.CONTENT,
							1.0);
					getContext().getParent().tell(audit_err, getSelf());
					e.printStackTrace();
				}
				
			} catch (Exception e) {
				log.error("exception caught during content audit");
				e.printStackTrace();
				log.error("-------------------------------------------------------------");
				log.error("-------------------------------------------------------------");
				log.error("THERE WAS AN ISSUE DURING CONTENT AUDIT");
				log.error("-------------------------------------------------------------");
				log.error("-------------------------------------------------------------");
			} finally {
				AuditProgressUpdate audit_update = new AuditProgressUpdate(page_audit_record_msg.getAccountId(),
						page_audit_record_msg.getAuditRecordId(), (1.0), "Content Audit Compelete!",
						AuditCategory.CONTENT, AuditLevel.PAGE, null, page_audit_record_msg.getDomainId());

				getSender().tell(audit_update, getSelf());
			}
		}).match(MemberUp.class, mUp -> {
			log.debug("Member is Up: {}", mUp.member());
		}).match(UnreachableMember.class, mUnreachable -> {
			log.debug("Member detected as unreachable: {}", mUnreachable.member());
		}).match(MemberRemoved.class, mRemoved -> {
			log.debug("Member is Removed: {}", mRemoved.member());
		}).matchAny(o -> {
			log.debug("received unknown message of type :: " + o.getClass().getName());
		}).build();
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
