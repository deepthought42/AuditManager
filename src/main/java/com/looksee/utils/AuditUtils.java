package com.looksee.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.looksee.auditManager.models.Audit;
import com.looksee.auditManager.models.AuditRecord;
import com.looksee.auditManager.models.AuditScore;
import com.looksee.auditManager.models.PageAuditRecord;
import com.looksee.auditManager.models.ReadingComplexityIssueMessage;
import com.looksee.auditManager.models.SentenceIssueMessage;
import com.looksee.auditManager.models.StockImageIssueMessage;
import com.looksee.auditManager.models.UXIssueMessage;
import com.looksee.auditManager.models.enums.AuditCategory;
import com.looksee.auditManager.models.enums.AuditName;
import com.looksee.auditManager.models.enums.AuditSubcategory;


public class AuditUtils {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AuditUtils.class.getName());


	public static double calculateScore(Set<Audit> audits) {
		assert audits != null;
		
		List<Audit> filtered_audits = audits.parallelStream()
										    .filter((s) -> (s.getTotalPossiblePoints() > 0))
										    .collect(Collectors.toList());
		
		double scores_total = filtered_audits.parallelStream()
										   .mapToDouble(x -> x.getPoints() / (double)x.getTotalPossiblePoints())
										   .sum();

		if(filtered_audits.isEmpty()) {
			return -1.0;
		}
		double final_score = (scores_total / (double)filtered_audits.size())*100;
		return final_score;
	}
	
	/**
	 * Reviews set of {@link Audit} and generates audits scores for content,
	 *   information architecture, aesthetics, interactivity and accessibility
	 *   
	 * @param audits
	 * @return
	 */
	public static AuditScore extractAuditScore(Set<Audit> audits) {
		double content_score = 0;
		int content_count = 0;
		
		double info_architecture_score = 0;
		int info_architecture_count = 0;
		
		double aesthetic_score = 0;
		int aesthetic_count = 0;
		
		double interactivity_score = 0;
		int interactivity_count = 0;
		
    	for(Audit audit: audits) {
    		if(audit.getTotalPossiblePoints() == 0) {
    			continue;
    		}
    		
    		if(AuditCategory.CONTENT.equals(audit.getCategory())) {
    			content_score += (audit.getPoints()/(double)audit.getTotalPossiblePoints());
    			content_count++;
    		}
    		else if(AuditCategory.INFORMATION_ARCHITECTURE.equals(audit.getCategory())) {
    			info_architecture_score += (audit.getPoints()/(double)audit.getTotalPossiblePoints());
    			info_architecture_count++;
    		}
    		else if(AuditCategory.AESTHETICS.equals(audit.getCategory())) {
    			aesthetic_score += (audit.getPoints()/(double)audit.getTotalPossiblePoints());
    			aesthetic_count++;
    		}
    	}
    	
    	if(content_count > 0) {
    		content_score = ( content_score / (double)content_count ) * 100;
    	}
    	else {
    		content_score = -1;
    	}
    	
    	if(info_architecture_count > 0) {
    		info_architecture_score = ( info_architecture_score / (double)info_architecture_count ) * 100;
    	}
    	else {
    		info_architecture_score = -1;
    	}
    	
    	if(aesthetic_count > 0) {
    		aesthetic_score = ( aesthetic_score / (double)aesthetic_count ) * 100;
    	}
    	else {
    		aesthetic_score = -1;
    	}
    	
    	double readability = extractLabelScore(audits, "readability");
    	double spelling_grammar = extractLabelScore(audits, "spelling");
    	double image_quality = extractLabelScore(audits, "images");
    	double alt_text = extractLabelScore(audits, "alt_text");
    	double links = extractLabelScore(audits, "links");
    	double metadata = extractLabelScore(audits, "metadata");
    	double seo = extractLabelScore(audits, "seo");
    	double security = extractLabelScore(audits, "security");
    	double color_contrast = extractLabelScore(audits, "color contrast");
    	double text_contrast = AuditUtils.calculateScoreByName(audits, AuditName.TEXT_BACKGROUND_CONTRAST);
    	double non_text_contrast = AuditUtils.calculateScoreByName(audits, AuditName.NON_TEXT_BACKGROUND_CONTRAST);
    	double whitespace = extractLabelScore(audits, "whitespace");
    	double accessibility = extractLabelScore(audits, "accessibility");
    	
    	return new AuditScore(content_score,
    							readability,
    							spelling_grammar,
    							image_quality,
    							alt_text,
    							info_architecture_score,
    							links,
    							metadata,
    							seo,
    							security,
    							aesthetic_score,
    							color_contrast, 
    							whitespace, 
    							interactivity_score, 
    							accessibility,
    							text_contrast,
    							non_text_contrast);
    	
	}

	private static double extractLabelScore(Set<Audit> audits, String label) {
		double score = 0.0;
		int count = 0;
    	for(Audit audit: audits) {
    		for(UXIssueMessage msg: audit.getMessages()) {
    			if(msg.getLabels().contains(label)) {
    				count++;
    				score += (msg.getPoints() / (double)msg.getMaxPoints());
       			}
    		}
    	}
    	
    	if(count <= 0) {
    		return 0.0;
    	}
    	
    	return score / (double)count;
	}

	public static boolean isPageAuditComplete(AuditRecord audit_record) {
		return audit_record.getAestheticAuditProgress() >= 1 
			&& audit_record.getContentAuditProgress() >= 1
			&& audit_record.getInfoArchitechtureAuditProgress() >= 1
			&& audit_record.getDataExtractionProgress() >= 1;
	}

	public static String getExperienceRating(PageAuditRecord audit_record) {
		double score = audit_record.getAestheticAuditProgress();
		score += audit_record.getContentAuditProgress();
		score += audit_record.getInfoArchitechtureAuditProgress();
		
		double final_score = score / 3;
		if(final_score >= 80) {
			return "delightful";
		}
		else if(final_score <80.0 && final_score >= 60) {
			return "almost there";
		}
		else {
			return "needs work";
		}
	}

	/**
	 * Calculate the score for all audits that have the given subcategory
	 * 
	 * @param audits
	 * @param subcategory
	 * 
	 * @return
	 * 
	 * @pre audits != null
	 * @pre subcategory != null
	 */
	public static double calculateSubcategoryScore(Set<Audit> audits, AuditSubcategory subcategory) {
		assert audits != null;
		assert subcategory != null;
		
		
		List<Audit> filtered_audits = audits.parallelStream()
				  .filter((s) -> (s.getTotalPossiblePoints() > 0 && s.getSubcategory().equals(subcategory)))
			      .collect(Collectors.toList());

		double scores_total = filtered_audits.parallelStream()
				   .mapToDouble(x -> (x.getPoints() / (double)x.getTotalPossiblePoints()))
				   .sum();

		if(filtered_audits.isEmpty()) {
			return -1.0;
		}
		
		double category_score = (scores_total / (double)filtered_audits.size())*100;
		
		return category_score;
	}

	public static double calculateScoreByCategory(Set<Audit> audits, AuditCategory category) {
		assert audits != null;
		assert category != null;
			
		List<Audit> filtered_audits = audits.parallelStream()
							  .filter((s) -> (s.getTotalPossiblePoints() > 0 && s.getCategory().equals(category)))
						      .collect(Collectors.toList());
		
		
		double scores_total = filtered_audits.parallelStream()
				   .mapToDouble(x -> (x.getPoints() / (double)x.getTotalPossiblePoints()))
				   .sum();
		
		if(filtered_audits.isEmpty()) {
			return -1.0;
		}
		
		double category_score = (scores_total / (double)filtered_audits.size())*100;
		return category_score;
	}

	/**
	 * Calculates percentage score based on audits with the given name
	 * 
	 * @param audits
	 * @param name
	 * @return
	 */
	public static double calculateScoreByName(Set<Audit> audits, AuditName name) {
		assert audits != null;
		assert name != null;
		
		//int audit_cnt = 0;
	
		List<Audit> filtered_audits = audits.parallelStream()
				  .filter((s) -> (s.getTotalPossiblePoints() > 0 && name.equals(s.getName())))
			      .collect(Collectors.toList());
		
		double scores_total = filtered_audits.parallelStream()
				   .mapToDouble(x -> { return x.getPoints() / (double)x.getTotalPossiblePoints(); })
				   .sum();

		if(filtered_audits.isEmpty()) {
			return -1.0;
		}
		
		double category_score = (scores_total / (double)filtered_audits.size())*100;
		
		return category_score;
	}

	/**
	 * Calculates percentage of failing large text items
	 * 
	 * @param audits
	 * @return
	 */
	public static double getPercentPassingLargeTextItems(Set<Audit> audits) {
		int count_large_text_items = 0;
		int failing_large_text_items = 0;
		
		for(Audit audit: audits) {
			//get audit issue messages
			for(UXIssueMessage msg : audit.getMessages()){
				if(msg.getTitle().contains("Large text")) {
					count_large_text_items++;
					if(msg.getPoints() == msg.getMaxPoints()) {
						failing_large_text_items++;
					}
				}
			}
		}
		
		return count_large_text_items / (double)failing_large_text_items;
	}

	public static double getPercentFailingSmallTextItems(Set<Audit> audits) {
		int count_text_items = 0;
		int failing_text_items = 0;
		
		for(Audit audit: audits) {
			//get audit issue messages
			for(UXIssueMessage msg : audit.getMessages()){
				if(msg.getDescription().contains("Text has")) {
					count_text_items++;
					if(msg.getPoints() == msg.getMaxPoints()) {
						failing_text_items++;
					}
				}
			}
		}
		
		return count_text_items / (double)failing_text_items;
	}

	/**
	 * Retrieves count of pages that have non text contrast issue
	 * 
	 * @param page_audits
	 * @param subcategory TODO
	 * @return
	 */
	public static int getCountPagesWithSubcategoryIssues(Set<PageAuditRecord> page_audits,
														 AuditSubcategory subcategory) {
		int count_failing_pages = 0;
		for(PageAuditRecord page_audit : page_audits) {
			for(Audit audit: page_audit.getAudits()) {
				if(subcategory.equals( audit.getSubcategory() ) 
						&& audit.getPoints() < audit.getTotalPossiblePoints()) {
					count_failing_pages++;
					break;
				}
			}
		}
		
		return count_failing_pages;
	}

	/**
	 * Retrieves count of pages that have non text contrast issue
	 * 
	 * @param page_audits
	 * @return
	 */
	public static int getCountPagesWithIssuesByAuditName(Set<PageAuditRecord> page_audits, AuditName audit_name) {
		int count_failing_pages = 0;
		
		for(PageAuditRecord page_audit : page_audits) {
			for(Audit audit: page_audit.getAudits()) {
				if(audit_name.equals( audit.getName() ) 
						&& audit.getPoints() < audit.getTotalPossiblePoints()) {
					count_failing_pages++;
					break;
				}
			}
		}
		
		return count_failing_pages;
	}

	/**
	 * 
	 * 
	 * @param page_audits
	 * @return
	 */
	public static int getCountOfPagesWithWcagComplianceIssues(Set<PageAuditRecord> page_audits) {
		int pages_with_issues = 0;
		
		for(PageAuditRecord audit_record : page_audits) {
			boolean has_issue = false;
			for(Audit audit: audit_record.getAudits()) {
				for(UXIssueMessage issue : audit.getMessages()) {
					if(issue.getLabels().contains("wcag") && issue.getPoints() < issue.getMaxPoints()) {
						pages_with_issues++;
						has_issue = true;
						break;
					}
				}
				if(has_issue) {
					break;
				}
			}
		}
		return pages_with_issues;
	}

	public static double calculateAverageWordsPerSentence(Set<Audit> audits) {
		int issue_count = 0;
		int word_count = 0;
		
		for(Audit audit: audits) {
			for(UXIssueMessage issue_msg : audit.getMessages()) {
				if(issue_msg instanceof SentenceIssueMessage) {
					issue_count++;
					word_count += ((SentenceIssueMessage) issue_msg).getWordCount();
				}
			}
		}
		
		return word_count / (double)issue_count;
	}

	public static double calculatePercentStockImages(Set<Audit> audits) {
		int image_count = 0;
		int stock_image_count = 0;
		
		for(Audit audit: audits) {
			for(UXIssueMessage issue_msg : audit.getMessages()) {
				if(issue_msg instanceof StockImageIssueMessage) {
					image_count++;
					if(((StockImageIssueMessage) issue_msg).isStockImage()) {
						stock_image_count++;
					}
				}
			}
		}
		
		return image_count / (double)stock_image_count;
	}

	public static double calculateAverageReadingComplexity(Set<Audit> audits) {
		int reading_complexity_issues = 0;
		double reading_complexity_total = 0;
		
		for(Audit audit: audits) {
			for(UXIssueMessage issue_msg : audit.getMessages()) {
				if(issue_msg instanceof ReadingComplexityIssueMessage) {
					reading_complexity_issues++;
					reading_complexity_total += ((ReadingComplexityIssueMessage) issue_msg).getEaseOfReadingScore();
				}
			}
		}
		
		return reading_complexity_issues / reading_complexity_total;
	}

	/**
	 * Calculates the {@link Audit} progress based on audits completed and the total pages
	 * @param category
	 * @param page_count
	 * @param audit_list
	 * @param audit_labels
	 * @return
	 */
	public static double calculateProgress(AuditCategory category, 
											 int page_count, 
											 Set<Audit> audit_list, 
											 Set<AuditName> audit_labels) {
				
		Map<AuditName, Integer> audit_count_map = new HashMap<>();
		Set<AuditName> category_audit_labels = getAuditLabels(category, audit_labels);
		List<Audit> filtered_audits = audit_list.stream()
												.filter(audit -> category.equals(audit.getCategory()))
												.filter(audit -> category_audit_labels.contains(audit.getName()))
												.collect(Collectors.toList());
		
		
		for(Audit audit : filtered_audits) {
			if(audit_count_map.containsKey(audit.getName())) {
				audit_count_map.put(audit.getName(), audit_count_map.get(audit.getName())+1);
			}
			else {
				audit_count_map.put(audit.getName(), 1);
			}
		}
		
		log.warn("audit count map = " +audit_count_map);
		double total_audit_count = 0;
		for(int count : audit_count_map.values()) {
			total_audit_count += (double)count;
		}
		
		log.warn("total audit count = "+total_audit_count);
		total_audit_count = total_audit_count / (double)category_audit_labels.size();
		log.warn("new total ="+total_audit_count);
		
		return total_audit_count / (double)page_count;
		
	}

	/**
	 * Retrieves the intersection between the full set of Audit Labels for a category compared with the desired set of audit labels.
	 * If the category is unknown, then the desired set of audit labels is returned without an intersection operation performed
	 * 
	 * @param category
	 * @param audit_labels
	 * @return
	 */
	public static Set<AuditName> getAuditLabels(AuditCategory category, Set<AuditName> audit_labels) {
		Set<AuditName> aesthetic_audit_labels = new HashSet<>();
		aesthetic_audit_labels.add(AuditName.TEXT_BACKGROUND_CONTRAST);
		aesthetic_audit_labels.add(AuditName.NON_TEXT_BACKGROUND_CONTRAST);
		
		Set<AuditName> content_audit_labels = new HashSet<>();
		content_audit_labels.add(AuditName.ALT_TEXT);
		content_audit_labels.add(AuditName.READING_COMPLEXITY);
		content_audit_labels.add(AuditName.PARAGRAPHING);
		content_audit_labels.add(AuditName.IMAGE_COPYRIGHT);
		content_audit_labels.add(AuditName.IMAGE_POLICY);

		Set<AuditName> info_architecture_audit_labels = new HashSet<>();
		info_architecture_audit_labels.add(AuditName.LINKS);
		info_architecture_audit_labels.add(AuditName.TITLES);
		info_architecture_audit_labels.add(AuditName.ENCRYPTED);
		info_architecture_audit_labels.add(AuditName.METADATA);
		
		if(AuditCategory.AESTHETICS.equals(category)) {
			//retrieve labels that are in both the aesthetic audit labels set and the list of audit labels passed in
			return SetUtils.intersection(aesthetic_audit_labels, audit_labels);
		}
		else if(AuditCategory.CONTENT.equals(category)) {
			//retrieve labels that are in both the content audit labels set and the list of audit labels passed in
			return SetUtils.intersection(content_audit_labels, audit_labels);

		}
		else if(AuditCategory.INFORMATION_ARCHITECTURE.equals(category)) {
			//retrieve labels that are in both the aesthetic audit labels set and the list of audit labels passed in
			return SetUtils.intersection(info_architecture_audit_labels, audit_labels);
		}
		
		return audit_labels;
	}
}
