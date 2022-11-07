package com.looksee.models.audit.content;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.looksee.models.ElementState;
import com.looksee.models.PageState;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.ElementStateIssueMessage;
import com.looksee.models.audit.IExecutablePageStateAudit;
import com.looksee.models.audit.UXIssueMessage;
import com.looksee.models.designsystem.DesignSystem;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.enums.AuditName;
import com.looksee.models.enums.AuditSubcategory;
import com.looksee.models.enums.Priority;
import com.looksee.services.AuditService;
import com.looksee.services.PageStateService;
import com.looksee.services.UXIssueMessageService;

/**
 * Responsible for executing an audit on the images on a page to determine adherence to alternate text best practices 
 *  for the visual audit category
 */
@Component
public class ImageAltTextAudit implements IExecutablePageStateAudit {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImageAltTextAudit.class);

	@Autowired
	private PageStateService page_state_service;
	
	@Autowired
	private AuditService audit_service;
	
	@Autowired
	private UXIssueMessageService issue_message_service;
	
	public ImageAltTextAudit() {
		//super(buildBestPractices(), getAdaDescription(), getAuditDescription(), AuditSubcategory.LINKS);
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * Scores images on a page based on if the image has an "alt" value present, format is valid and the 
	 *   url goes to a location that doesn't produce a 4xx error 
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	@Override
	public Audit execute(PageState page_state, AuditRecord audit_record, DesignSystem design_system) { 
		assert page_state != null;
		
		Set<UXIssueMessage> issue_messages = new HashSet<>();

		Set<String> labels = new HashSet<>();
		labels.add("accessibility");
		labels.add("alt_text");
		labels.add("wcag");
		
		String tag_name = "img";
		List<ElementState> elements = page_state_service.getElementStates(page_state.getId());
		List<ElementState> image_elements = new ArrayList<>();
		for(ElementState element : elements) {
			if(element.getName().equalsIgnoreCase(tag_name)) {
				image_elements.add(element);
			}
		}
		
		String why_it_matters = "Alt-text helps with both SEO and accessibility. Search engines use alt-text"
				+ " to help determine how usable and your site is as a way of ranking your site.";
		
		String ada_compliance = "Your website does not meet the level A ADA compliance requirement for" + 
				" ‘Alt’ text for images present on the website.";

		//score each link element
		for(ElementState image_element : image_elements) {
	
			Document jsoup_doc = Jsoup.parseBodyFragment(image_element.getOuterHtml(), page_state.getUrl());
			Element element = jsoup_doc.getElementsByTag(tag_name).first();
			
			//Check if element has "alt" attribute present
			if(element.hasAttr("alt")) {				

				if(element.attr("alt").isEmpty()) {
					String title = "Image alternative text value is empty";
					String description = "Image alternative text value is empty";
					
					ElementStateIssueMessage issue_message = new ElementStateIssueMessage(
																	Priority.HIGH, 
																	description, 
																	"Images without alternative text defined as a non empty string value", 
																	null,
																	AuditCategory.CONTENT,
																	labels,
																	ada_compliance,
																	title,
																	0,
																	1);
					
					issue_message = (ElementStateIssueMessage) issue_message_service.save(issue_message);
					issue_message_service.addElement(issue_message.getId(), image_element.getId());
					issue_messages.add(issue_message);
				}
				else {
					String title = "Image has alt text value set!";
					String description = "Well done! By providing an alternative text value, you are providing a more inclusive experience";
					
					ElementStateIssueMessage issue_message = new ElementStateIssueMessage(
																	Priority.NONE, 
																	description, 
																	"Images without alternative text defined as a non empty string value", 
																	null,
																	AuditCategory.CONTENT,
																	labels,
																	ada_compliance,
																	title,
																	1,
																	1);

					issue_message = (ElementStateIssueMessage) issue_message_service.save(issue_message);
					issue_message_service.addElement(issue_message.getId(), image_element.getId());
					issue_messages.add(issue_message);
				}
			}
			else {
				String title= "Images without alternative text attribute";
				String description = "Images without alternative text attribute";
				
				ElementStateIssueMessage issue_message = new ElementStateIssueMessage(
																Priority.HIGH, 
																description, 
																"Images without alternative text attribute", 
																null,
																AuditCategory.CONTENT, 
																labels,
																ada_compliance,
																title,
																0,
																1);
				
				issue_message = (ElementStateIssueMessage) issue_message_service.save(issue_message);
				issue_message_service.addElement(issue_message.getId(), image_element.getId());
				issue_messages.add(issue_message);
			}
		}
		
		int points_earned = 0;
		int max_points = 0;
		for(UXIssueMessage issue_msg : issue_messages) {
			points_earned += issue_msg.getPoints();
			max_points += issue_msg.getMaxPoints();
			/*
			if(issue_msg.getScore() < 90 && issue_msg instanceof ElementStateIssueMessage) {
				ElementStateIssueMessage element_issue_msg = (ElementStateIssueMessage)issue_msg;
				
				List<ElementState> good_examples = audit_service.findGoodExample(AuditName.ALT_TEXT, 100);
				if(good_examples.isEmpty()) {
					log.warn("Could not find element for good example...");
					continue;
				}
				Random random = new Random();
				ElementState good_example = good_examples.get(random.nextInt(good_examples.size()-1));
				element_issue_msg.setGoodExample(good_example);
				issue_message_service.save(element_issue_msg);
			}
			*/
		}
		
		//log.warn("ALT TEXT AUDIT SCORE ::  "+ points_earned + " / " + max_points);
		String description = "Images without alternative text defined as a non empty string value";

		Audit audit = new Audit(AuditCategory.CONTENT,
								 AuditSubcategory.IMAGERY,
								 AuditName.ALT_TEXT,
								 points_earned,
								 new HashSet<>(),
								 AuditLevel.PAGE,
								 max_points,
								 page_state.getUrl(), 
								 why_it_matters, 
								 description,
								 true);
		audit_service.save(audit);
		audit_service.addAllIssues(audit.getId(), issue_messages);
		return audit;
		//the contstant 2 in this equation is the exact number of boolean checks for this audit
	}
}
