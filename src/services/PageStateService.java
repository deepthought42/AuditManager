package com.looksee.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.ElementState;
import com.looksee.models.PageState;
import com.looksee.models.Screenshot;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.PageAuditRecord;
import com.looksee.models.enums.AuditName;
import com.looksee.models.repository.PageStateRepository;

import io.github.resilience4j.retry.annotation.Retry;



/**
 * Service layer object for interacting with {@link PageState} database layer
 *
 */
@Service
@Retry(name = "neoforj")
public class PageStateService {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PageStateService.class.getName());
	
	@Autowired
	private PageStateRepository page_state_repo;

	/**
	 * Save a {@link PageState} object and its associated objects
	 * @param page_state
	 * @return
	 * @throws Exception 
	 * 
	 * @pre page_state != null
	 */
	public PageState save(PageState page_state) throws Exception {
		assert page_state != null;
		
		PageState page_state_record = page_state_repo.findByKey(page_state.getKey());
		
		if(page_state_record == null) {
			log.warn("page state wasn't found in database. Saving new page state to neo4j");

			return page_state_repo.save(page_state);
		}

		return page_state_record;
	}
	
	public PageState findByKey(String page_key) {
		PageState page_state = page_state_repo.findByKey(page_key);
		if(page_state != null){
			page_state.setElements(getElementStates(page_key));
		}
		return page_state;
	}
	
	public List<PageState> findByScreenshotChecksumAndPageUrl(String user_id, String url, String screenshot_checksum){
		return page_state_repo.findByScreenshotChecksumAndPageUrl(url, screenshot_checksum);		
	}
	
	public List<PageState> findByFullPageScreenshotChecksum(String screenshot_checksum){
		return page_state_repo.findByFullPageScreenshotChecksum(screenshot_checksum);		
	}
	
	public PageState findByAnimationImageChecksum(String user_id, String screenshot_checksum){
		return page_state_repo.findByAnimationImageChecksum(user_id, screenshot_checksum);		
	}
	
	public List<ElementState> getElementStates(String page_key){
		assert page_key != null;
		assert !page_key.isEmpty();
		
		return page_state_repo.getElementStates(page_key);
	}
	
	public List<ElementState> getElementStates(long page_state_id){
		return page_state_repo.getElementStates(page_state_id);
	}
	
	public List<ElementState> getLinkElementStates(long page_state_id){
		return page_state_repo.getLinkElementStates(page_state_id);
	}
	
	public List<Screenshot> getScreenshots(String user_id, String page_key){
		List<Screenshot> screenshots = page_state_repo.getScreenshots(user_id, page_key);
		if(screenshots == null){
			return new ArrayList<Screenshot>();
		}
		return screenshots;
	}
	
	public List<PageState> findPageStatesWithForm(long account_id, String url, String page_key) {
		return page_state_repo.findPageStatesWithForm(account_id, url, page_key);
	}

	public Collection<ElementState> getExpandableElements(List<ElementState> elements) {
		List<ElementState> expandable_elements = new ArrayList<>();
		for(ElementState elem : elements) {
			if(elem.isLeaf()) {
				expandable_elements.add(elem);
			}
		}
		return expandable_elements;
	}
	
	public List<PageState> findBySourceChecksumForDomain(String url, String src_checksum) {
		return page_state_repo.findBySourceChecksumForDomain(url, src_checksum);
	}
	
	public List<Audit> getAudits(String page_state_key){
		assert page_state_key != null;
		assert !page_state_key.isEmpty();
		
		return page_state_repo.getAudits(page_state_key);
	}

	public Audit findAuditBySubCategory(AuditName subcategory, String page_state_key) {
		return page_state_repo.findAuditBySubCategory(subcategory.getShortName(), page_state_key);
	}

	public List<ElementState> getVisibleLeafElements(String page_state_key) {
		return page_state_repo.getVisibleLeafElements(page_state_key);
	}

	public PageState findByUrl(String url) {
		assert url != null;
		assert !url.isEmpty();
		
		return page_state_repo.findByUrl(url);
	}

	public boolean addElement(long page_id, long element_id) {		
		Optional<ElementState> element_state = getElementState(page_id, element_id);
		
		if(element_state.isPresent()) {
			return true;
		}
		return page_state_repo.addElement(page_id, element_id) != null;
	}

	private Optional<ElementState> getElementState(long page_id, long element_id) {
		return page_state_repo.getElementState(page_id, element_id);
	}

	/**
	 * Retrieves an {@link AuditRecord} for the page with the given id
	 * @param id
	 * @return
	 */
	public PageAuditRecord getAuditRecord(long id) {
		
		return page_state_repo.getAuditRecord(id);
	}

	public Optional<PageState> findById(long page_id) {
		return page_state_repo.findById(page_id);
	}

	public void updateCompositeImageUrl(Long id, String composite_img_url) {
		page_state_repo.updateCompositeImageUrl(id, composite_img_url);
	}

	public void addAllElements(long page_state_id, List<Long> element_ids) {
		page_state_repo.addAllElements(page_state_id, element_ids);
	}
}
