package com.looksee.services;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.ElementState;
import com.looksee.models.ImageElementState;
import com.looksee.models.PageState;
import com.looksee.models.PageStateAudits;
import com.looksee.models.SimpleElement;
import com.looksee.models.SimplePage;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.ElementStateIssueMessage;
import com.looksee.models.audit.UXIssueMessage;
import com.looksee.models.enums.AuditName;
import com.looksee.models.enums.AuditSubcategory;
import com.looksee.models.enums.ObservationType;
import com.looksee.models.repository.AuditRepository;

import io.github.resilience4j.retry.annotation.Retry;

/**
 * Contains business logic for interacting with and managing audits
 *
 */
@Service
@Retry(name = "neoforj")
public class AuditService {
	private static Logger log = LoggerFactory.getLogger(AuditService.class);

	@Autowired
	private AuditRepository audit_repo;
	
	@Autowired
	private UXIssueMessageService ux_issue_service;
	
	@Autowired
	private PageStateService page_state_service;

	public Audit save(Audit audit) {
		assert audit != null;
		
		return audit_repo.save(audit);
	}

	public Optional<Audit> findById(long id) {
		return audit_repo.findById(id);
	}
	
	public Audit findByKey(String key) {
		return audit_repo.findByKey(key);
	}

	public List<Audit> saveAll(List<Audit> audits) {
		assert audits != null;
		
		List<Audit> audits_saved = new ArrayList<Audit>();
		
		for(Audit audit : audits) {
			if(audit == null) {
				continue;
			}
			
			Audit audit_record = audit_repo.findByKey(audit.getKey());
			if(audit_record != null) {
				log.warn("audit already exists!!!");
				audits_saved.add(audit_record);
				continue;
			}

			Audit saved_audit = audit_repo.save(audit);
			audits_saved.add(saved_audit);
		}
		
		return audits_saved;
	}

	public List<Audit> findAll() {
		// TODO Auto-generated method stub
		return IterableUtils.toList(audit_repo.findAll());
	}

	public Set<UXIssueMessage> getIssues(long audit_id) {
		Set<UXIssueMessage> raw_issue_set = audit_repo.findIssueMessages(audit_id);
		
		return raw_issue_set.parallelStream()
							.filter(issue -> issue.getPoints() != issue.getMaxPoints())
							.distinct()
							.collect(Collectors.toSet());
	}
	
	/**
	 * using a list of audits, sorts the list by page and packages results into list 
	 * 	of {@linkplain PageStateAudits}
	 * 
	 * @param audits
	 * @return
	 */
	public List<PageStateAudits> groupAuditsByPage(Set<Audit> audits) {
		Map<String, Set<Audit>> audit_url_map = new HashMap<>();
		
		for(Audit audit : audits) {
			//if url of pagestate already exists 
			if(audit_url_map.containsKey(audit.getUrl())) {
				audit_url_map.get(audit.getUrl()).add(audit);
			}
			else {
				Set<Audit> page_audits = new HashSet<>();
				page_audits.add(audit);
				
				audit_url_map.put(audit.getUrl(), page_audits);
			}
		}
		
		List<PageStateAudits> page_audits = new ArrayList<>();
		for(String url : audit_url_map.keySet()) {
			//load page state by url
			PageState page_state = page_state_service.findByUrl(url);
			SimplePage simple_page = new SimplePage(
											page_state.getUrl(), 
											page_state.getViewportScreenshotUrl(), 
											page_state.getFullPageScreenshotUrlOnload(), 
											page_state.getFullPageScreenshotUrlComposite(), 
											page_state.getFullPageWidth(),
											page_state.getFullPageHeight(),
											page_state.getSrc(), 
											page_state.getKey(), page_state.getId());
			PageStateAudits page_state_audits = new PageStateAudits(simple_page, audit_url_map.get(url));
			page_audits.add( page_state_audits ) ;
		}
		
		return page_audits;
	}
	
	
	/**
	 * Generates a {@linkplain Map} with element keys for it's keys and a set of issue keys associated 
	 * 	with each element as the values
	 * 
	 * @param audits
	 * @param page_url
	 * @return
	 * @throws MalformedURLException
	 */
	public Map<String, Set<String>> generateElementIssuesMap(Set<Audit> audits)  {		
		Map<String, Set<String>> element_issues = new HashMap<>();
				
		for(Audit audit : audits) {	
			Set<UXIssueMessage> issues = getIssues(audit.getId());

			for(UXIssueMessage issue_msg : issues ) {
				
				ElementState element = ux_issue_service.getElement(issue_msg.getId());
				if(element == null) {
					continue;
				}
				
				//associate issue with element
				if(!element_issues.containsKey(element.getKey())) {	
					Set<String> issue_keys = new HashSet<>();
					issue_keys.add(issue_msg.getKey());
					
					element_issues.put(element.getKey(), issue_keys);
				}
				else {
					element_issues.get(element.getKey()).add(issue_msg.getKey());
				}

			}
		}

		return element_issues;
	}
	
	/**
	 * WIP
	 * 
	 * @param audits
	 * @param page_url
	 * @return
	 * @throws MalformedURLException
	 */
	public Map<String, String> generateIssueElementMap(Set<Audit> audits)  {		
		Map<String, String> issue_element_map = new HashMap<>();
				
		for(Audit audit : audits) {	
			Set<UXIssueMessage> issues = getIssues(audit.getId());

			for(UXIssueMessage issue_msg : issues ) {
				if(issue_msg.getType().equals(ObservationType.COLOR_CONTRAST) || 
						issue_msg.getType().equals(ObservationType.ELEMENT) ) {
					ElementState element = ux_issue_service.getElement(issue_msg.getId());
					if(element == null) {
						log.warn("element issue map:: element is null for issue msg ... "+issue_msg.getId());
						continue;
					}
					
					//associate issue with element
					issue_element_map.put(issue_msg.getKey(), element.getKey());
				}
				else {
					// DO NOTHING FOR NOW
				}
			
			}
		}

		return issue_element_map;
	}

	public UXIssueMessage addIssue(
			String key, 
			String issue_key) {
		assert key != null;
		assert !key.isEmpty();
		assert issue_key != null;
		assert !issue_key.isEmpty();
		
		return audit_repo.addIssueMessage(key, issue_key);
	}

	/**
	 * 
	 * @param audits
	 * @return
	 */
	public Collection<UXIssueMessage> retrieveUXIssues(Set<Audit> audits) {
		Map<String, UXIssueMessage> issues = new HashMap<>();
		
		for(Audit audit : audits) {	
			Set<UXIssueMessage> issue_set = getIssues(audit.getId());
			
			for(UXIssueMessage ux_issue: issue_set) {
				if(ObservationType.ELEMENT.equals(ux_issue.getType())) {
					ElementStateIssueMessage element_issue = (ElementStateIssueMessage)ux_issue;
					/*
					ElementState good_example = ux_issue_service.getGoodExample(ux_issue.getId());
					element_issue.setGoodExample(good_example);
					*/
					issues.put(ux_issue.getKey(), element_issue);
				}
				else {
					issues.put(ux_issue.getKey(), ux_issue);
				}
			}
		}
		return issues.values();
	}
	

	/**
	 * Returns a {@linkplain Set} of {@linkplain ElementState} objects that are associated 
	 * 	with the {@linkplain UXIssueMessage} provided
	 * @param issue_set
	 * @return
	 */
	public Collection<SimpleElement> retrieveElementSet(Collection<? extends UXIssueMessage> issue_set) {
		Map<String, SimpleElement> element_map = new HashMap<>();
		
		for(UXIssueMessage ux_issue: issue_set) {
			if(ux_issue.getType().equals(ObservationType.COLOR_CONTRAST) || 
					ux_issue.getType().equals(ObservationType.ELEMENT) ) {

				ElementState element = ux_issue_service.getElement(ux_issue.getId());
				if(element == null) {
					return element_map.values();
				}
				if(element instanceof ImageElementState) {
					ImageElementState img_element = (ImageElementState)element;
					
					SimpleElement simple_element = 	new SimpleElement(img_element.getKey(),
																		img_element.getScreenshotUrl(), 
																		img_element.getXLocation(), 
																		img_element.getYLocation(), 
																		img_element.getWidth(), 
																		img_element.getHeight(),
																		img_element.getCssSelector(),
																		img_element.getAllText(),
																		img_element.isImageFlagged(),
																		img_element.isAdultContent());

					element_map.put(img_element.getKey(), simple_element);
				}
				else {					
					SimpleElement simple_element = 	new SimpleElement(element.getKey(),
																	  element.getScreenshotUrl(), 
																	  element.getXLocation(), 
																	  element.getYLocation(), 
																	  element.getWidth(), 
																	  element.getHeight(),
																	  element.getCssSelector(),
																	  element.getAllText(),
																	  element.isImageFlagged(),
																	  false);
					
					element_map.put(element.getKey(), simple_element);
				}
			}
			else {
				//DO NOTHING FOR NOW
			}
				
		}
		return element_map.values();
	}

	public void addAllIssues(long id, List<Long> issue_ids) {
		audit_repo.addAllIssues(id, issue_ids);
	}

	public List<ElementState> getIssuesByNameAndScore(AuditName audit_name, int score) {
		return audit_repo.getIssuesByNameAndScore(audit_name.toString(), score);
	}
	
	public List<ElementState> findGoodExample(AuditName audit_name, int score) {
		return getIssuesByNameAndScore(audit_name, score);
	}
	
	public int countAuditBySubcategory(Set<Audit> audits, AuditSubcategory category) {
		assert audits != null;
		assert category != null;
	
		int issue_count = audits.parallelStream()
				  .filter((s) -> (s.getTotalPossiblePoints() > 0 && category.equals(s.getSubcategory())))
				  .mapToInt(s -> audit_repo.getMessageCount(s.getId()))
				  .sum();
		return issue_count;
	}

	public int countIssuesByAuditName(Set<Audit> audits, AuditName name) {
		assert audits != null;
		assert name != null;
	
		int issue_count = audits.parallelStream()
				  .filter((s) -> (s.getTotalPossiblePoints() > 0 && name.equals(s.getName())))
				  .mapToInt(s -> audit_repo.getMessageCount(s.getId()))
				  .sum();
		
		return issue_count;	
	}

	public void addAllIssues(long audit_id, Set<UXIssueMessage> issue_messages) {
		List<Long> issue_ids = issue_messages.stream().map(x -> x.getId()).collect(Collectors.toList());
		addAllIssues(audit_id, issue_ids);
	}
}
