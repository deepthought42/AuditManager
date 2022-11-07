package com.looksee.models.audit;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.looksee.models.ElementState;
import com.looksee.models.ImageElementState;
import com.looksee.models.PageState;
import com.looksee.models.designsystem.DesignSystem;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.enums.AuditName;
import com.looksee.models.enums.AuditSubcategory;
import com.looksee.models.enums.Priority;
import com.looksee.services.AuditService;
import com.looksee.services.PageStateService;
import com.looksee.services.UXIssueMessageService;
import com.looksee.utils.BrowserUtils;

/**
 * Responsible for executing an audit on the hyperlinks on a page for the information architecture audit category
 */
@Component
public class ImagePolicyAudit implements IExecutablePageStateAudit {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImagePolicyAudit.class);
	
	@Autowired
	private AuditService audit_service;
	
	@Autowired
	private	PageStateService page_state_service;
	
	@Autowired
	private UXIssueMessageService issue_message_service;
	
	public ImagePolicyAudit() {
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * Scores links on a page based on if the link has an href value present, the url format is valid and the 
	 *   url goes to a location that doesn't produce a 4xx error 
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	@Override
	public Audit execute(PageState page_state, AuditRecord audit_record, DesignSystem design_system) {
		assert page_state != null;
		
		//get all elements that are text containers
		List<ElementState> elements = page_state_service.getElementStates(page_state.getKey());
		//filter elements that aren't text elements
		List<ElementState> element_list = BrowserUtils.getTextElements(elements);
		
		String why_it_matters = "";
		String description = "";

		Score image_policy_score = calculateImagePolicyViolationScore(element_list, design_system);
		
		Audit audit = new Audit(AuditCategory.CONTENT,
								 AuditSubcategory.IMAGERY, 
								 AuditName.IMAGE_POLICY, 
								 image_policy_score.getPointsAchieved(), 
								 new HashSet<>(), 
								 AuditLevel.PAGE, 
								 image_policy_score.getMaxPossiblePoints(), 
								 page_state.getUrl(),
								 why_it_matters, 
								 description,
								 false); 
		
		audit_service.save(audit);
		audit_service.addAllIssues(audit.getId(), image_policy_score.getIssueMessages());
		return audit;
	}


	/**
	 * Calculates score of images based on the image policies in place
	 * 
	 * @param element_list
	 * @param design_system
	 * @return
	 */
	private Score calculateImagePolicyViolationScore(List<ElementState> element_list, DesignSystem design_system) {
		int points_earned = 0;
		int max_points = 0;
		Set<UXIssueMessage> issue_messages = new HashSet<>();
		Set<String> labels = new HashSet<>();
		labels.add("imagery");
		labels.add("policy");
		
		String ada_compliance = "There are no ADA compliance requirements for this category";
		
		for(ElementState element: element_list) {
			if(element instanceof ImageElementState) {
				ImageElementState img_element = (ImageElementState)element;
				if(img_element.isAdultContent() && !design_system.getAllowedImageCharacteristics().contains("ADULT")) {
					log.warn("Creating issue for image with adult content");
		
					//return new Score(1, 1, new HashSet<>());
					String recommendation = "Use an image without nudity";
					String title = "Nudity detected";
					String description = "Image contains nudity and/or adult content";

					ElementStateIssueMessage issue_message = new ElementStateIssueMessage(
																	Priority.MEDIUM, 
																	description, 
																	recommendation, 
																	null,
																	AuditCategory.CONTENT,
																	labels,
																	ada_compliance,
																	title,
																	0,
																	1);
					
					issue_message = (ReadingComplexityIssueMessage) issue_message_service.save(issue_message);
					issue_message_service.addElement(issue_message.getId(), element.getId());
					issue_messages.add(issue_message);
					
					points_earned += 0;
					max_points += 1;
				}
				else if(img_element.isViolentContent() && !design_system.getAllowedImageCharacteristics().contains("VIOLENCE")) {
					log.warn("Creating issue for image with violent content");
		
					//return new Score(1, 1, new HashSet<>());
					String recommendation = "Use an image without voilence";
					String title = "Violent imagery";
					String description = "Image contains violence";
					
					ElementStateIssueMessage issue_message = new ElementStateIssueMessage(
																	Priority.MEDIUM, 
																	description, 
																	recommendation, 
																	null,
																	AuditCategory.CONTENT,
																	labels,
																	ada_compliance,
																	title,
																	0,
																	1);
					
					issue_message = (ReadingComplexityIssueMessage) issue_message_service.save(issue_message);
					issue_message_service.addElement(issue_message.getId(), element.getId());
					issue_messages.add(issue_message);
					
					points_earned += 0;
					max_points += 1;
				}
				else {
					points_earned += 1;
					max_points += 1;
					String recommendation = "";
					String title = "Image complies with policy";
					String description = "This image complies with the domain policy. Well done!";
					
					ElementStateIssueMessage issue_message = new ElementStateIssueMessage(
																	Priority.NONE, 
																	description, 
																	recommendation, 
																	null,
																	AuditCategory.CONTENT,
																	labels,
																	ada_compliance,
																	title,
																	1,
																	1);

					issue_message = (ReadingComplexityIssueMessage) issue_message_service.save(issue_message);
					issue_message_service.addElement(issue_message.getId(), element.getId());
					issue_messages.add(issue_message);
				}
			}
		}
		return new Score(points_earned, max_points, issue_messages);	
	}
}
