package com.looksee.services;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.grid.common.exception.GridException;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.looksee.api.exception.PagesAreNotMatchingException;
import com.looksee.browsing.Browser;
import com.looksee.helpers.BrowserConnectionHelper;
import com.looksee.models.ActionOLD;
import com.looksee.models.Animation;
import com.looksee.models.Domain;
import com.looksee.models.Element;
import com.looksee.models.ElementState;
import com.looksee.models.Group;
import com.looksee.models.LookseeObject;
import com.looksee.models.PageLoadAnimation;
import com.looksee.models.PageState;
import com.looksee.models.Test;
import com.looksee.models.TestRecord;
import com.looksee.models.enums.BrowserEnvironment;
import com.looksee.models.enums.BrowserType;
import com.looksee.models.enums.TestStatus;
import com.looksee.models.journeys.Redirect;
import com.looksee.models.repository.TestRepository;
import com.looksee.utils.PathUtils;

@Component
public class TestService {
	private static Logger log = LoggerFactory.getLogger(TestService.class);


	@Autowired
	private TestRepository test_repo;

	@Autowired
	private ActionService action_service;

	@Autowired
	private GroupService group_service;

	@Autowired
	private PageStateService page_state_service;

	@Autowired
	private ElementStateService element_state_service;

	@Autowired
	private RedirectService redirect_service;

	@Autowired
	private AnimationService animation_service;

	@Autowired
	private PageLoadAnimationService page_load_animation_service;


	/**
	 * Runs an {@code Test}
	 *
	 * @param test test to be ran
	 *
	 * @pre test != null
	 * @return	{@link TestRecord} indicating passing status and {@link PageVersion} if not passing
	 * @throws NoSuchAlgorithmException
	 * @throws WebDriverException
	 * @throws GridException
	 */
	 public TestRecord runTest(Test test, String browser_name, TestStatus last_test_status, Domain domain, String user_id) {
		 assert test != null;

		 TestStatus passing = null;
		 PageState page = null;
		 TestRecord test_record = null;
		 final long pathCrawlStartTime = System.currentTimeMillis();

		 int cnt = 0;
		 Browser browser = null;
		 
		 do{
			 try {
				 browser = BrowserConnectionHelper.getConnection(BrowserType.create(browser_name), BrowserEnvironment.TEST);
				 //page = crawler.crawlPath(user_id, domain, test.getPathKeys(), test.getPathObjects(), browser, new URL(PathUtils.getFirstPage(test.getPathObjects()).getUrl()).getHost(), visible_element_map, visible_elements);
			 } catch(PagesAreNotMatchingException e){
				 log.warn(e.getMessage());
			 }
			 catch (Exception e) {
				 e.printStackTrace();
				 log.error("RUN TEST ERROR ::  " + e.getMessage());
			 }
			 finally{
				 if(browser != null){
					 browser.close();
				 }
			 }

			 cnt++;
		 }while(cnt < 1000 && page == null);

		 final long pathCrawlEndTime = System.currentTimeMillis();
		 long pathCrawlRunTime = pathCrawlEndTime - pathCrawlStartTime;

		 passing = Test.isTestPassing(getResult(test.getKey(), domain.getUrl(), user_id), page, last_test_status );
 		 test_record = new TestRecord(new Date(), passing, browser_name.trim(), page, pathCrawlRunTime, test.getPathKeys());

		 return test_record;
	 }

	 public Test save(Test test, String url, long account_id) throws Exception {
		 assert test != null;
		 Test record = test_repo.findByKey(test.getKey(), url, account_id);

		if(record == null){
			log.warn("test record is null while saving");
			List<LookseeObject> path_objects = new ArrayList<LookseeObject>();
			for(LookseeObject path_obj : test.getPathObjects()){
				if(path_obj instanceof PageState){
					path_objects.add(page_state_service.save((PageState)path_obj));
					
				}
				else if(path_obj instanceof Element){						path_objects.add(element_state_service.save((ElementState)path_obj));
				}
				else if(path_obj instanceof ActionOLD){
					path_objects.add(action_service.save((ActionOLD)path_obj));
				}
				else if(path_obj instanceof Redirect){
					path_objects.add(redirect_service.save((Redirect)path_obj));
				}
				else if(path_obj instanceof Animation){
					path_objects.add(animation_service.save((Animation)path_obj));
				}
				else if(path_obj instanceof PageLoadAnimation){
					path_objects.add(page_load_animation_service.save((PageLoadAnimation)path_obj));
				}
			}
			test.setPathObjects(path_objects);
			if(test.getResult() != null){
				test.setResult(page_state_service.save(test.getResult()));			}
			
			Set<Group> groups = new HashSet<>();
			for(Group group : test.getGroups()){
				groups.add(group_service.save(group));
			}
			test.setGroups(groups);
	  		return test_repo.save(test);
		}
		else{
			log.warn("test record already exists");
			List<LookseeObject> path_objects = test_repo.getPathObjects(test.getKey(), url, account_id );
			path_objects = PathUtils.orderPathObjects(test.getPathKeys(), path_objects);
			record.setPathObjects(path_objects);
			
			if(test.getResult() == null){
				PageState result = page_state_service.save(test.getResult());
				log.warn("result of saving result :: " + result);
				record.setResult(result);
			}
	
			Set<Group> groups = new HashSet<>();
			for(Group group : test.getGroups()){
				groups.add(group_service.save(group));
			}
			record.setGroups(groups);
			
			if(record.getName() != null && record.getName().contains("Test #")){
				record.setName(test.generateTestName());
			}
			
			return test_repo.save(record);
		}
	}

   public List<Test> findTestsWithElementState(String page_state_key, String element_state_key){
     return test_repo.findTestWithElementState(page_state_key, element_state_key);
   }

   public Test findByKey(String key, String url, long account_id){
     return test_repo.findByKey(key, url, account_id);
   }

   public List<Test> findTestsWithPageState(String page_state_key, String url, long account_id) {
     return test_repo.findTestWithPageState(page_state_key, url, account_id);
   }

   /**
    * Retrieves list of path objects from database and puts them in the correct order
    * 
    * @param test_key key of {@link Test} that we want path objects for
    * 
    * @return List of ordered {@link LookseeObject}s
    */
   public List<LookseeObject> getPathObjects(String test_key, String url, long account_id) {
	   Test test = test_repo.findByKey(test_key, url, account_id);
	   List<LookseeObject> path_obj_list = test_repo.getPathObjects(test_key, url, account_id);
	   //order path objects
	   List<LookseeObject> ordered_list = new ArrayList<LookseeObject>();
	   for(String key : test.getPathKeys()) {
		   for(LookseeObject path_obj : path_obj_list) {
			   if(path_obj.getKey().equals(key)) {
				   ordered_list.add(path_obj);
				   break;
			   }
		   }
	   }
	   
	   return ordered_list;
   }

   public Set<Group> getGroups(String key) {
     return test_repo.getGroups(key);
   }
   
   public PageState getResult(String key, String url, String user_id) {
	   return test_repo.getResult(key, url, user_id);
   }

   /**
    * Checks if url, xpath and remaining keys in path_keys list are present in any of the test paths provided in test_path_object_lists
    * 
    * @param path_keys
    * @param tests_path_object_lists
    * 
    * @pre path_keys != null
    * @pre !path_keys.isEmpty()
    * @pre test_path_object_lists != null
    * @pre !test_path_object_lists.isEmpty()
    * 
    * @return
    */
   public boolean checkIfEndOfPathAlreadyExistsInAnotherTest(List<String> path_keys, List<List<LookseeObject>> test_path_object_lists, String user_id, String url) {
	   assert path_keys != null;
	   assert !path_keys.isEmpty();
	   assert test_path_object_lists != null;
	   assert !test_path_object_lists.isEmpty();
   
	   //load path objects using path keys
	   List<LookseeObject> path_objects = loadPathObjects(user_id, path_keys);
	   
	   //find all tests with page state at index
	   for(List<LookseeObject> test_path_objects : test_path_object_lists) {
		   //check if any subpath of test matches path_objects based on url, xpath and action
		   int current_idx = 0;
		   
		   log.warn("path object list size when checking if end of path is unique :: "+test_path_objects.size());
		   for(LookseeObject path_object : test_path_objects) {
			   if(path_object != null && path_object.getKey().contains("pagestate") && ((PageState)path_object).getUrl().equalsIgnoreCase(((PageState)path_objects.get(0)).getUrl())){
				   current_idx++;
				   break;
			   }
			   current_idx++;
		   }

		   log.warn("------------------------------------------------------------------------------");
		   log.warn("path objects size :: "+path_objects.size());
		   log.warn("test path objects size :: "+test_path_objects.size());
		   log.warn("------------------------------------------------------------------------------");
		   
		   //check if next element has the same xpath as the next element in path objects
		   if(test_path_objects.size() > 1) {
			   boolean matching_test_found = true;
			   if(((Element)test_path_objects.get(current_idx)).getXpath().equalsIgnoreCase(((Element)path_objects.get(1)).getXpath())) {
				   current_idx++;
				   //check if remaining keys in path_objects match following keys in test_path_objects
				   for(LookseeObject obj : path_objects.subList(2, path_objects.size())) {
					   if(!obj.getKey().equalsIgnoreCase(test_path_objects.get(current_idx).getKey())) {
						   matching_test_found = false;
						   break;
					   }
					   current_idx++;
				   }	
			   }

			   if(matching_test_found) {
				   return true;
			   }
		   }
	   }
	   
	   return false;
   }

	public List<LookseeObject> loadPathObjects(String user_id, List<String> path_keys) {
		//load path objects using path keys
		List<LookseeObject> path_objects = new ArrayList<LookseeObject>();
		for(String key : path_keys) {
			if(key.contains("pagestate")) {
				path_objects.add(page_state_service.findByKey(key));
			}
			else if(key.contains("elementstate")) {
				path_objects.add(element_state_service.findByKey(key));
			}
			else if(key.contains("action")) {
				path_objects.add(action_service.findByKey(key));
			}
	    }
		
		return path_objects;
	}

	public Set<Test> findAllTestRecordsContainingKey(String path_object_key, String url, String user_id) {
		return test_repo.findAllTestRecordsContainingKey(path_object_key, url, user_id);
	}

	public boolean checkIfEndOfPathAlreadyExistsInPath(PageState resultPage, List<String> path_keys) {
		return path_keys.contains(resultPage.getKey());
	}

	public void addGroup(String test_key, Group group, String url, String user_id) {
		Group group_record = group_service.save(group);
		test_repo.addGroup(test_key, group_record.getKey(), url, user_id);
	}
}
