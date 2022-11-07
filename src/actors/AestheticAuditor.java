package com.looksee.actors;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.looksee.models.Account;
import com.looksee.models.PageState;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.aesthetics.ColorPaletteAudit;
import com.looksee.models.audit.aesthetics.NonTextColorContrastAudit;
import com.looksee.models.audit.aesthetics.TextColorContrastAudit;
import com.looksee.models.designsystem.DesignSystem;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.message.AuditError;
import com.looksee.models.message.AuditProgressUpdate;
import com.looksee.models.message.PageAuditRecordMessage;
import com.looksee.services.AuditRecordService;
import com.looksee.services.DesignSystemService;
import com.looksee.services.DomainService;

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
public class AestheticAuditor extends AbstractActor{
	private static Logger log = LoggerFactory.getLogger(AestheticAuditor.class.getName());

	private Cluster cluster = Cluster.get(getContext().getSystem());
	
	@Autowired
	private DesignSystemService design_system_service;
	
	@Autowired
	private TextColorContrastAudit text_contrast_auditor;

	@Autowired
	private NonTextColorContrastAudit non_text_contrast_auditor;
	
	@Autowired
	private AuditRecordService audit_record_service;
	
	@Autowired
	private ColorPaletteAudit color_palette_auditor;
	
	@Autowired
	private DomainService domain_service;
	
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
						//retrieve compliance level
						DesignSystem design_system = null;

						if(page_audit_record_msg.getDomainId() >= 0 ) {
							
							Optional<DesignSystem> design_system_opt = domain_service.getDesignSystem(page_audit_record_msg.getDomainId());
							if(!design_system_opt.isPresent()) {
								log.warn("design system couldn't be found for domain :: "+page_audit_record_msg.getDomainId());
								design_system = design_system_service.save( new DesignSystem() );
								domain_service.addDesignSystem(page_audit_record_msg.getDomainId(), design_system.getId());
							}
							else {
								design_system = design_system_opt.get();
							}
						}
						AuditRecord audit_record = audit_record_service.findById(page_audit_record_msg.getPageAuditId()).get();
						PageState page = audit_record_service.getPageStateForAuditRecord(audit_record.getId());
					   	//PageState page = page_audit_record_msg.getPageState();
					   	//check if page state already
			   			//perform audit and return audit result
					   
					   	//Audit color_palette_audit = color_palette_auditor.execute(page);
						//audits.add(color_palette_audit);
					   	AuditProgressUpdate audit_update = new AuditProgressUpdate(
																	page_audit_record_msg.getAccountId(),
																	audit_record.getId(),
																	(1.0/3.0),
																	"Reviewing text contrast",
																	AuditCategory.AESTHETICS,
																	AuditLevel.PAGE, 
																	null, 
																	page_audit_record_msg.getDomainId());

					   	getSender().tell(audit_update, getSelf());

						/*
						Audit padding_audits = padding_auditor.execute(page);
						audits.add(padding_audits);
	
						Audit margin_audits = margin_auditor.execute(page);
						audits.add(margin_audits);
						 */
						
						try {
						   	Audit text_contrast_audit = text_contrast_auditor.execute(page, audit_record, design_system);
						   	if(text_contrast_audit != null) {
								AuditProgressUpdate audit_update2 = new AuditProgressUpdate(
																			page_audit_record_msg.getAccountId(),
																			audit_record.getId(),
																			(2.0/3.0),
																			"Reviewing non-text contrast for WCAG compliance",
																			AuditCategory.AESTHETICS,
																			AuditLevel.PAGE, 
																			text_contrast_audit, 
																			page_audit_record_msg.getDomainId());
								getSender().tell(audit_update2, getSelf());
						   	}
						}
						catch(Exception e) {
							AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(), 
																  page_audit_record_msg.getAccountId(), 
																  page_audit_record_msg.getAuditRecordId(), 
																  "An error occurred while performing non-text audit", 
																  AuditCategory.AESTHETICS, 
																  (2.0/3.0));
							getSender().tell(audit_err, getSelf());
							e.printStackTrace();
						}
						
						
						try {
							Audit non_text_contrast_audit = non_text_contrast_auditor.execute(page, audit_record, design_system);
							if( non_text_contrast_audit != null ) {
							
								AuditProgressUpdate audit_update3 = new AuditProgressUpdate(
																			page_audit_record_msg.getAccountId(),
																			audit_record.getId(),
																			1.0,
																			"Completed review of non-text contrast",
																			AuditCategory.AESTHETICS,
																			AuditLevel.PAGE, 
																			non_text_contrast_audit, 
																			page_audit_record_msg.getDomainId());
	
								getSender().tell(audit_update3, getSelf());
							}
						}
						catch(Exception e) {
							AuditError audit_err = new AuditError(page_audit_record_msg.getDomainId(), 
																  page_audit_record_msg.getAccountId(), 
																  page_audit_record_msg.getAuditRecordId(), 
																  "An error occurred while performing non-text audit", 
																  AuditCategory.AESTHETICS, 
																  1.0);
							getSender().tell(audit_err, getSelf());
							e.printStackTrace();
						}
						
					}catch(Exception e) {
						log.warn("exception caught during aesthetic audit");
						e.printStackTrace();
						log.warn("-------------------------------------------------------------");
						log.warn("-------------------------------------------------------------");
						log.warn("THERE WAS AN ISSUE DURING AESTHETICS AUDIT");
						log.warn("-------------------------------------------------------------");
						log.warn("-------------------------------------------------------------");

						
						
						AuditProgressUpdate audit_update3 = new AuditProgressUpdate(
								page_audit_record_msg.getAccountId(),
								page_audit_record_msg.getPageAuditId(),
								1.0,
								"Completed review of non-text contrast",
								AuditCategory.AESTHETICS,
								AuditLevel.PAGE, 
								null,
								page_audit_record_msg.getDomainId());

						getContext().getParent().tell(audit_update3, getSelf());
					}
				})
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	
}
