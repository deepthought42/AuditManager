package com.looksee.models.audit.content;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.looksee.models.ElementState;
import com.looksee.models.PageState;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.IExecutablePageStateAudit;
import com.looksee.models.audit.ReadingComplexityIssueMessage;
import com.looksee.models.audit.Score;
import com.looksee.models.audit.UXIssueMessage;
import com.looksee.models.audit.recommend.Recommendation;
import com.looksee.models.designsystem.DesignSystem;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.enums.AuditName;
import com.looksee.models.enums.AuditSubcategory;
import com.looksee.models.enums.Priority;
import com.looksee.services.AuditService;
import com.looksee.services.UXIssueMessageService;
import com.looksee.utils.ContentUtils;

import io.whelk.flesch.kincaid.ReadabilityCalculator;

/**
 * Responsible for executing an audit on the hyperlinks on a page for the information architecture audit category
 *
 * WCAG Level - AAA
 * WCAG Success Criterion - https://www.w3.org/TR/UNDERSTANDING-WCAG20/meaning-supplements.html
 */
@Component
public class ReadabilityAudit implements IExecutablePageStateAudit {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ReadabilityAudit.class);
	
	@Autowired
	private AuditService audit_service;
	
	@Autowired
	private UXIssueMessageService issue_message_service;
	
	public ReadabilityAudit() {} 

	
	/**
	 * {@inheritDoc}
	 * 
	 * Scores readability and relevance of content on a page based on the reading level of the content and the keywords used
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	@Override
	public Audit execute(PageState page_state, AuditRecord audit_record, DesignSystem design_system) {
		assert page_state != null;
		
		Set<UXIssueMessage> issue_messages = new HashSet<>();
		
		//filter elements that aren't text elements
		//get all element states
		//filter any element state whose text exists within another element
		List<ElementState> og_text_elements = new ArrayList<>();
		
		String ada_compliance = "Text content shouldn't require a reading ability more advanced than the lower"
				+ " secondary education level (grades 5 through 8 ) after removal of proper names and titles.";
		
		Set<String> labels = new HashSet<>();
		labels.add("written content");
		labels.add("readability");
		labels.add("wcag");
		
		//List<ElementState> elements = page_state_service.getElementStates(page_state.getId());
		for(ElementState element: page_state.getElements()) {
			if(element.getName().contentEquals("button") 
					|| element.getName().contentEquals("a") 
					|| element.getOwnedText().isEmpty() 
					|| element.getAllText().split(" ").length <= 3
			) {
				continue;
			}
			boolean is_child_text = false;
			for(ElementState element2: page_state.getElements()) {
				if(element2.getKey().contentEquals(element.getKey())) {
					continue;
				}
				if(!element2.getOwnedText().isEmpty() 
						&& element2.getAllText().contains(element.getAllText()) 
						&& !element2.getAllText().contentEquals(element.getAllText())
				) {
					is_child_text = true;
					break;
				}
				else if(element2.getAllText().contentEquals(element.getAllText())
						&& !element2.getXpath().contains(element.getXpath())
				) {
					is_child_text = true;
					break;
				}

			}
			
			if(!is_child_text) {
				og_text_elements.add(element);
			}
		}
		
		for(ElementState element : og_text_elements) {
			//List<Sentence> sentences = CloudNLPUtils.extractSentences(all_page_text);
			//Score paragraph_score = calculateParagraphScore(sentences.size());
			try {
				double ease_of_reading_score = ReadabilityCalculator.calculateReadingEase(element.getAllText());
				String difficulty_string = ContentUtils.getReadingDifficultyRatingByEducationLevel(ease_of_reading_score, audit_record.getTargetUserEducation());
				String grade_level = ContentUtils.getReadingGradeLevel(ease_of_reading_score);
				
				if("unknown".contentEquals(difficulty_string)) {
					continue;
				}
	
				int element_points = getPointsForEducationLevel(ease_of_reading_score, audit_record.getTargetUserEducation());
	
				if(element.getAllText().split(" ").length < 10) {
					element_points = 4;
				}
				
				if(element_points < 4) {
					String title = "Content is written at " + grade_level + " reading level";
					String description = generateIssueDescription(element, difficulty_string, ease_of_reading_score, audit_record.getTargetUserEducation());
					String recommendation = "Reduce the length of your sentences by breaking longer sentences into 2 or more shorter sentences. You can also use simpler words. Words that contain many syllables can also be difficult to understand.";
					
					ReadingComplexityIssueMessage issue_message = new ReadingComplexityIssueMessage(Priority.LOW, 
																								  description,
																								  recommendation,
																								  null,
																								  AuditCategory.CONTENT,
																								  labels,
																								  ada_compliance,
																								  title,
																								  element_points,
																								  4,
																								  ease_of_reading_score);
					
					issue_message = (ReadingComplexityIssueMessage) issue_message_service.save(issue_message);
					issue_message_service.addElement(issue_message.getId(), element.getId());
					issue_messages.add(issue_message);
				}
				else {
					String recommendation = "";
					String description = "";
					if(element.getAllText().split(" ").length < 10) {
						element_points = 4;
						description = "Content is short enough to be easily understood by all users";
					}
					else {					
						description = generateIssueDescription(element, difficulty_string, ease_of_reading_score, audit_record.getTargetUserEducation());
					}
					String title = "Content is easy to read";
					
					ReadingComplexityIssueMessage issue_message = new ReadingComplexityIssueMessage(Priority.NONE, 
																								  description,
																								  recommendation,
																								  null,
																								  AuditCategory.CONTENT,
																								  labels,
																								  ada_compliance,
																								  title,
																								  element_points,
																								  4,
																								  ease_of_reading_score);
					
					issue_message = (ReadingComplexityIssueMessage) issue_message_service.save(issue_message);
					issue_message_service.addElement(issue_message.getId(), element.getId());
					issue_messages.add(issue_message);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}		

		String why_it_matters = "For people with reading disabilities(including the most highly educated), it is important"
				+ "to accomodate these users by providing text that is simpler to read."
				+ "Beyond accessibility, the way users experience content online has changed." + 
				" Attention spans are shorter, and users skim through most information." + 
				" Presenting information in small, easy to digest chunks makes their" + 
				" experience easy and convenient.";
		
		int points_earned = 0;
		int max_points = 0;
		for(UXIssueMessage issue_msg : issue_messages) {
			points_earned += issue_msg.getPoints();
			max_points += issue_msg.getMaxPoints();
			/*
			if(issue_msg.getScore() < 90 && issue_msg instanceof ElementStateIssueMessage) {
				ElementStateIssueMessage element_issue_msg = (ElementStateIssueMessage)issue_msg;
				List<ElementState> good_examples = audit_service.findGoodExample(AuditName.READING_COMPLEXITY, 100);
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

		String description = "";		
		Audit audit = new Audit(AuditCategory.CONTENT,
								 AuditSubcategory.WRITTEN_CONTENT,
								 AuditName.READING_COMPLEXITY,
								 points_earned,
								 new HashSet<>(),
								 AuditLevel.PAGE,
								 max_points, 
								 page_state.getUrl(),
								 why_it_matters, 
								 description,
								 false); 
		
		audit_service.save(audit);
		audit_service.addAllIssues(audit.getId(), issue_messages);
		return audit;
	}


	private String generateIssueDescription(ElementState element, 
											String difficulty_string,
											double ease_of_reading_score, 
											String targetUserEducation) {
		String description = "The text \"" + element.getAllText() + "\" is " + difficulty_string + " to read for "+getConsumerType(targetUserEducation);
		
		return description;
	}


	private String getConsumerType(String targetUserEducation) {
		String consumer_label = "the average consumer";
		
		if(targetUserEducation != null) {
			consumer_label = "users with a "+targetUserEducation + " education";
		}
		
		return consumer_label;
	}


	private int getPointsForEducationLevel(double ease_of_reading_score, String target_user_education) {
		int element_points = 0;
				
		if(ease_of_reading_score >= 90 ) {
			if(target_user_education == null) {
				element_points = 4;
			}
			else if("HS".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else if("College".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else if("Advanced".contentEquals(target_user_education)) {				
				element_points = 3;
			}
			else {
				element_points = 4;
			}
		}
		else if(ease_of_reading_score < 90 && ease_of_reading_score >= 80 ) {
			if(target_user_education == null) {
				element_points = 4;
			}
			else if("HS".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else if("College".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else if("Advanced".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else {
				element_points = 4;
			}
		}
		else if(ease_of_reading_score < 80 && ease_of_reading_score >= 70) {
			if(target_user_education == null) {
				element_points = 4;
			}
			else if("HS".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else if("College".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else if("Advanced".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else {
				element_points = 3;
			}
		}
		else if(ease_of_reading_score < 70 && ease_of_reading_score >= 60) {
			if(target_user_education == null) {
				element_points = 3;
			}
			else if("HS".contentEquals(target_user_education)) {				
				element_points = 3;
			}
			else if("College".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else if("Advanced".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else {
				element_points = 2;
			}
		}
		else if(ease_of_reading_score < 60 && ease_of_reading_score >= 50) {
			if(target_user_education == null) {
				element_points = 2;
			}
			else if("HS".contentEquals(target_user_education)) {				
				element_points = 2;
			}
			else if("College".contentEquals(target_user_education)) {				
				element_points = 3;
			}
			else if("Advanced".contentEquals(target_user_education)) {				
				element_points = 4;
			}
			else {
				element_points = 1;
			}
		}
		else if(ease_of_reading_score < 50 && ease_of_reading_score >= 30) {
			if(target_user_education == null) {
				element_points = 1;
			}
			else if("HS".contentEquals(target_user_education)) {				
				element_points = 1;
			}
			else if("College".contentEquals(target_user_education)) {				
				element_points = 2;
			}
			else if("Advanced".contentEquals(target_user_education)) {				
				element_points = 3;
			}
			else {
				element_points = 0;
			}		
		}
		else if(ease_of_reading_score < 30) {
			if(target_user_education == null) {
				element_points = 0;
			}
			else if("College".contentEquals(target_user_education)) {				
				element_points = 1;
			}
			else if("Advanced".contentEquals(target_user_education)) {				
				element_points = 2;
			}
			else {
				element_points = 0;
			}	
			element_points = 0;
		}
		
		return element_points;
	}


	public static Score calculateSentenceScore(String sentence) {
		//    		for each sentence check that sentence is no longer than 20 words
		String[] words = sentence.split(" ");
		
		if(words.length <= 10) {
			return new Score(2, 2, new HashSet<>());
		}
		else if(words.length <= 20) {
			return new Score(1, 2, new HashSet<>());
		}

		return new Score(0, 2, new HashSet<>());
	}


	public static Score calculateParagraphScore(int sentence_count) {
		if(sentence_count <= 5) {
			return new Score(1, 1, new HashSet<>());
		}

		return new Score(0, 1, new HashSet<>());
		//	  		Verify that there are no more than 5 sentences
		// validate that spacing between paragraphs is at least 2x the font size within the paragraphs
	}
}
