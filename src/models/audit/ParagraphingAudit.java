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

import com.google.cloud.language.v1.Sentence;
import com.looksee.gcp.CloudNLPUtils;
import com.looksee.models.ElementState;
import com.looksee.models.PageState;
import com.looksee.models.audit.recommend.Recommendation;
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
public class ParagraphingAudit implements IExecutablePageStateAudit {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ParagraphingAudit.class);
	
	@Autowired
	private AuditService audit_service;
	
	@Autowired
	private PageStateService page_state_service;
	
	@Autowired
	private UXIssueMessageService issue_message_service;
	
	
	public ParagraphingAudit() {
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * Scores links on a page based on if the link has an href value present, the url format is valid and the 
	 *   url goes to a location that doesn't produce a 4xx error 
	 *   
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	@Override
	public Audit execute(PageState page_state, AuditRecord audit_record, DesignSystem design_system) {
		assert page_state != null;

		Set<UXIssueMessage> issue_messages = new HashSet<>();
		
		//get all elements that are text containers
		List<ElementState> elements = page_state_service.getElementStates(page_state.getId());
		//filter elements that aren't text elements
		List<ElementState> element_list = BrowserUtils.getTextElements(elements);
		
		for(ElementState element : element_list) {
			String text_block = element.getOwnedText();
			
			//    parse text block into paragraph chunks(multiple paragraphs can exist in a text block)
			String[] paragraphs = text_block.split("\n");
			for(String paragraph : paragraphs) {
				if(paragraph.split(" ").length < 3) {
					continue;
				}
				else if(!paragraph.contains(".")) {
					paragraph = paragraph + ".";
				}
				try {
					List<Sentence> sentences = CloudNLPUtils.extractSentences(paragraph);
					Score score = calculateSentenceScore(sentences, element);

					issue_messages.addAll(score.getIssueMessages());	
				} catch (Exception e) {
					log.warn("error getting sentences from text :: "+paragraph);
					//e.printStackTrace();
				}

			}
			// validate that spacing between paragraphs is at least 2x the font size within the paragraphs
		}
		
		String why_it_matters = "The way users experience content has changed in the mobile phone era." + 
				" Attention spans are shorter, and users skim through most information." + 
				" Presenting information in small, easy to digest chunks makes their" + 
				" experience easy and convenient. ";


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
		
		String description = "";

		Audit audit = new Audit(AuditCategory.CONTENT,
						 AuditSubcategory.WRITTEN_CONTENT, 
						 AuditName.PARAGRAPHING, 
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


	/**
	 * Reviews list of sentences and gives a score based on how many of those sentences have 
	 * 		25 words or less. This is considered the maximum sentence length allowed in EU government documentation
	 * @param sentences
	 * @param element
	 * @return
	 */
	public Score calculateSentenceScore(List<Sentence> sentences, ElementState element) {
		//    		for each sentence check that sentence is no longer than 25 words
		int points_earned = 0;
		int max_points = 0;
		Set<UXIssueMessage> issue_messages = new HashSet<>();
		Set<String> labels = new HashSet<>();
		labels.add("written content");
		labels.add("paragraphs");
		labels.add("readability");
		
		String ada_compliance = "There are no ADA compliance requirements for this category.";
		
		for(Sentence sentence : sentences) {
			String[] words = sentence.getText().getContent().split(" ");
			
			if(words.length > 25) {

				//return new Score(1, 1, new HashSet<>());
				String recommendation = "Try reducing the size of the sentence or breaking it up into multiple sentences";
				String title = "Sentence is too long";
				String description = "The sentence  \"" + sentence.getText().getContent() + "\" has more than 25 words which can make it difficult for users to understand";

				SentenceIssueMessage issue_message = new SentenceIssueMessage(
																Priority.MEDIUM, 
																description, 
																recommendation, 
																null,
																AuditCategory.CONTENT,
																labels,
																ada_compliance,
																title,
																0,
																1,
																words.length);
				
				issue_message = (SentenceIssueMessage) issue_message_service.save(issue_message);
				issue_message_service.addElement(issue_message.getId(), element.getId());
				issue_messages.add(issue_message);
				
				points_earned += 0;
				max_points += 1;
			}
			else {
				points_earned += 1;
				max_points += 1;
				String recommendation = "";
				String title = "Sentence meets EU and US governmental standards for sentence length";
				String description = "The sentence  \"" + sentence.getText().getContent() + "\" has less than 25 words which is the standard for governmental documentation in the European Union(EU) and the United States(US)";
				
				SentenceIssueMessage issue_message = new SentenceIssueMessage(
																Priority.NONE, 
																description, 
																recommendation, 
																null,
																AuditCategory.CONTENT,
																labels,
																ada_compliance,
																title,
																1,
																1,
																words.length);
				
				issue_message = (SentenceIssueMessage) issue_message_service.save(issue_message);
				issue_message_service.addElement(issue_message.getId(), element.getId());
				issue_messages.add(issue_message);
			}
		}
		return new Score(points_earned, max_points, issue_messages);					
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
