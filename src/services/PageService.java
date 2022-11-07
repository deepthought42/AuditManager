package com.looksee.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.Element;
import com.looksee.models.Page;
import com.looksee.models.PageState;
import com.looksee.models.audit.performance.PerformanceInsight;
import com.looksee.models.repository.PageRepository;
import com.looksee.models.repository.PageStateRepository;
import com.looksee.models.repository.PerformanceInsightRepository;

/**
 * Methods for interacting with page object
 */
@Service
public class PageService {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PageService.class);
	
	@Autowired
	private PageRepository page_repo;
	
	@Autowired
	private PageStateRepository page_state_service;
	
	@Autowired
	private PerformanceInsightRepository performance_insight_repo;
	
	
	/**
	 * Saves {@link Page} to database
	 * 
	 * @param page
	 * 
	 * @return {@link Page} object reference to database object
	 * 
	 * @pre page != null;
	 */
	public Page saveForUser(String user_id, Page page){
		assert page != null;
		assert user_id != null;
		
		Page page_record = findByKey(page.getKey());
		if(page_record != null){
			page_record.setPageStates(page.getPageStates());
			return page_repo.save(page_record);
		}
		
		System.out.println("page repo ::  "+page_repo);
		System.out.println("Page   ::   "+page);
		return page_repo.save(page);
	}
	
	/**
	 * Saves {@link Page} to database
	 * 
	 * @param page
	 * 
	 * @return {@link Page} object reference to database object
	 * 
	 * @pre page != null;
	 */
	public Page save(Page page){
		assert page != null;
		
		Page page_record = findByKey(page.getKey());
		if(page_record != null){
			page_record.setPageStates(page.getPageStates());
			return page_repo.save(page_record);
		}
		
		return page_repo.save(page);
	}
	
	/**
	 * Retrieve page from database using key and user ID
	 * 
	 * @param user_id
	 * @param key
	 * 
	 * @return {@link Page} record
	 * 
	 * @pre key != null;
	 * @pre !key.isEmpty();
	 * @pre user_id != null
	 * @pre !user_id.isEmpty()
	 */
	@Deprecated
	public Page findByKeyAndUser(String user_id, String key){
		assert key != null;
		assert !key.isEmpty();
		assert user_id != null;
		assert !user_id.isEmpty();
		
		return page_repo.findByKeyAndUser(user_id, key);
	}
	
	/**
	 * Retrieve page from database using key
	 * 
	 * @param key
	 * 
	 * @return {@link Page} record
	 * 
	 * @pre key != null;
	 * @pre !key.isEmpty();
	 */
	public Page findByKey( String key ){
		assert key != null;
		assert !key.isEmpty();
		
		return page_repo.findByKey(key);
	}

	/**
	 * Retrieve page from database using key
	 * 
	 * @param url
	 * 
	 * @return {@link Page} record
	 * 
	 * @pre key != null;
	 * @pre !key.isEmpty();
	 */
	public Page findByUrl( String url ){
		assert url != null;
		assert !url.isEmpty();
		
		return page_repo.findByUrl(url);
	}
	
	/**
	 * 
	 * 
	 * @param page
	 * @param performance_insight
	 * 
	 * @pre user_id != null
	 * @pre !user_id.isEmpty()
	 * @pre domain_url != null;
	 * @pre !domain_url.isEmpty();
	 * @pre page_key != null
	 * @pre !page_key.isEmpty()
	 * @pre performance_insight_key != null
	 * @pre !performance_insight_key.isEmpty();
	 */
	public boolean addPerformanceInsight(String user_id, String domain_url, String page_key, String performance_insight_key) {
		assert user_id != null;
		assert !user_id.isEmpty();
		assert domain_url != null;
		assert !domain_url.isEmpty();
		assert page_key != null;
		assert !page_key.isEmpty();
		assert performance_insight_key != null;
		assert !performance_insight_key.isEmpty();
		
		//check if performance insight already exists for page
		PerformanceInsight performance_insight = page_repo.getPerformanceInsight(user_id, domain_url, page_key, performance_insight_key);
		if(performance_insight == null) {
			page_repo.addPerformanceInsight(user_id, domain_url, page_key, performance_insight_key);
			return true;
		}
		return false;
	}

	/**
	 * Retrieves a List of all {@link PerformanceInsight}s associated with a {@link Page} that has a given key
	 * 
	 * @param page_key
	 * @return
	 * 
	 * @pre page_key != null
	 * @pre !page_key.isEmpty()
	 */
	public List<PerformanceInsight> findAllInsights(String page_key) {
		assert page_key != null;
		assert !page_key.isEmpty();
		
		return page_repo.getAllPerformanceInsights(page_key);
	}
	
	/**
	 * Retrieves the latest {@link PerformanceInsight} for a {@link Page} with a given key
	 * 
	 * @param page_key
	 * @return
	 * 
	 * @pre page_key != null
	 * @pre !page_key.isEmpty()
	 */
	public PerformanceInsight findLatestInsight(String page_key) {
		assert page_key != null;
		assert !page_key.isEmpty();
		
		PerformanceInsight insight = page_repo.getLatestPerformanceInsight(page_key);
		insight.setAudits(performance_insight_repo.getAllAudits(page_key, insight.getKey()));
		return insight;
	}
	
	/**
	 * 
	 * @param page_key
	 * @return
	 * 
	 * @pre page_key != null
	 * @pre !page_key.isEmpty()
	 */
	@Deprecated
	public void addPageState(String user_id, String page_key, PageState page_state) {
		assert user_id != null;
		assert !user_id.isEmpty();
		assert page_key != null;
		assert !page_key.isEmpty();
		assert page_state != null;
		
		PageState page_state_record = page_state_service.findByKeyAndUsername(user_id, page_state.getKey());
		if(page_state_record == null) {
			page_state_record = page_state_service.save(page_state);
		}
		Page page = page_repo.findByKey(page_key);
		page.addPageState(page_state_record);
		page_repo.save(page);
	}
	
	/**
	 * 
	 * @param page_key
	 * @return
	 * 
	 * @pre page_key != null
	 * @pre !page_key.isEmpty()
	 * @pre page_state_key != null
	 * @pre !page_state_key.isEmpty()
	 */
	public void addPageState(String page_key, long page_state_id) {
		assert page_key != null;
		assert !page_key.isEmpty();
		
		Optional<PageState> page_state_record = page_repo.findPageStateForPage(page_key, page_state_id);
		if(!page_state_record.isPresent()) {
			page_repo.addPageState(page_key, page_state_id);
		}
	}

	public PageState getMostRecentPageState(String key) {
		return page_repo.findMostRecentPageState(key);
	}

	public List<Element> getElements(String key) {
		return page_repo.getElements(key);
	}
}
