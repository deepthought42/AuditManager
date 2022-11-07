package com.looksee.browsing;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.openqa.grid.common.exception.GridException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import com.looksee.api.exception.PagesAreNotMatchingException;
import com.looksee.helpers.BrowserConnectionHelper;
import com.looksee.models.ActionOLD;
import com.looksee.models.Domain;
import com.looksee.models.ElementState;
import com.looksee.models.ExploratoryPath;
import com.looksee.models.LookseeObject;
import com.looksee.models.PageAlert;
import com.looksee.models.PageLoadAnimation;
import com.looksee.models.PageState;
import com.looksee.models.enums.Action;
import com.looksee.models.enums.AlertChoice;
import com.looksee.models.enums.BrowserEnvironment;
import com.looksee.models.enums.BrowserType;
import com.looksee.models.journeys.Redirect;
import com.looksee.models.message.JourneyMessage;
import com.looksee.models.repository.ActionRepository;
import com.looksee.services.BrowserService;
import com.looksee.utils.BrowserUtils;
import com.looksee.utils.PathUtils;
import com.looksee.utils.TimingUtils;


/**
 * Provides methods for crawling web pages using Selenium
 */
@Component
public class Crawler {
	private static Logger log = LoggerFactory.getLogger(Crawler.class);

	@Autowired
	private BrowserService browser_service;

	@Autowired
	private ActionRepository action_repo;

	/**
	 * Crawls the path using the provided {@link Browser browser}
	 * @param browser
	 * @param user_id TODO
	 *
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws WebDriverException
	 * @throws GridException
	 * @throws URISyntaxException 
	 *
	 * @pre path != null
	 * @pre path != null
	 */
	 /*
	public void crawlPathWithoutBuildingResult(List<String> path_keys, List<LookseeObject> path_objects, Browser browser, String host_channel, String user_id) 
			throws IOException, GridException, WebDriverException, NoSuchAlgorithmException, PagesAreNotMatchingException, URISyntaxException{
		assert browser != null;
		assert path_keys != null;

		Element last_element = null;
		LookseeObject last_obj = null;
		PageState expected_page = null;
		
		List<LookseeObject> ordered_path_objects = PathUtils.orderPathObjects(path_keys, path_objects);
		
		for(LookseeObject current_obj: ordered_path_objects){
			if(current_obj instanceof PageState){
				expected_page = (PageState)current_obj;
				BrowserUtils.detectShortAnimation(browser, expected_page.getUrl(), user_id);
			}
			else if(current_obj instanceof Element){
				last_element = (Element) current_obj;
				browser.scrollToElement(last_element);
			}
			//String is action in this context
			else if(current_obj instanceof Action){
				Action action = (Action)current_obj;
				Action action_record = action_repo.findByKey(action.getKey());
				if(action_record==null){
					action_repo.save(action);
				}
				else{
					action = action_record;
				}

				performAction(action, last_element, browser.getDriver());
				Point p = browser.getViewportScrollOffset();
				browser.setXScrollOffset(p.getX());
				browser.setYScrollOffset(p.getY());
			}
			else if(current_obj instanceof Redirect){
				Redirect redirect = (Redirect)current_obj;

				//if redirect is preceded by a page state or nothing then initiate navigation
				if(last_obj == null || last_obj instanceof PageState){
					browser.navigateTo(redirect.getStartUrl());
				}
				//if redirect follows an action then watch page transition
				BrowserUtils.getPageTransition(redirect.getStartUrl(), browser, host_channel, user_id);
			}
			else if(current_obj instanceof PageLoadAnimation){
				BrowserUtils.getLoadingAnimation(browser, host_channel, user_id);
			}
			else if(current_obj instanceof PageAlert){
				log.debug("Current path node is a PageAlert");
				PageAlert alert = (PageAlert)current_obj;
				alert.performChoice(browser.getDriver(), AlertChoice.DISMISS);
			}

			last_obj = current_obj;
		}
	}
*/

	
	/**
	 * Crawls the path using the provided {@link Browser browser}
	 * @param browser
	 * @param path list of vertex keys
	 * @param user_id TODO
	 *
	 * @return {@link PageVersion result_page} state that resulted from crawling path
	 *
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws WebDriverException
	 * @throws GridException
	 * @throws URISyntaxException 
	 *
	 * @pre path != null
	 * @pre path != null
	 */
	@Deprecated
	public void crawlPathExplorer(List<String> keys, 
								  List<LookseeObject> path_object_list, 
								  Browser browser, 
								  String host_channel, 
								  ExploratoryPath path
	) throws IOException, GridException, NoSuchElementException, WebDriverException, NoSuchAlgorithmException, PagesAreNotMatchingException, URISyntaxException{
		assert browser != null;
		assert keys != null;

		com.looksee.models.Element last_element = null;
		LookseeObject last_obj = null;
		PageState expected_page = null;
		List<String> path_keys = new ArrayList<String>(keys);
		List<LookseeObject> ordered_path_objects = PathUtils.orderPathObjects(keys, path_object_list);
		List<LookseeObject> path_objects_explored = new ArrayList<>(ordered_path_objects);

		String last_url = null;
		int current_idx = 0;
		for(LookseeObject current_obj: ordered_path_objects){
			if(current_obj instanceof PageState){
				expected_page = (PageState)current_obj;
				last_url = expected_page.getUrl();
				
			}
			else if(current_obj instanceof Redirect){
				Redirect redirect = (Redirect)current_obj;
				//if redirect is preceded by a page state or nothing then initiate navigation
				if(last_obj == null || last_obj instanceof PageState){
					browser.navigateTo(redirect.getStartUrl());
				}

				//if redirect follows an action then watch page transition
				last_url = redirect.getUrls().get(redirect.getUrls().size()-1);
			}
			else if(current_obj instanceof PageLoadAnimation){
			}
			else if(current_obj instanceof com.looksee.models.Element){
				last_element = (com.looksee.models.Element) current_obj;
				browser.scrollToElement(last_element);
				//BrowserUtils.detectShortAnimation(browser, expected_page.getUrl());
			}
			//String is action in this context
			else if(current_obj instanceof ActionOLD){
				ActionOLD action = (ActionOLD)current_obj;
				ActionOLD action_record = action_repo.findByKey(action.getKey());
				if(action_record==null){
					action_repo.save(action);
				}
				else{
					action = action_record;
				}

				//performAction(action, last_element, browser.getDriver());

				//check for page alert presence
				Alert alert = browser.isAlertPresent();
				if(alert != null){
					log.warn("Alert was encountered!!!");
					PageAlert page_alert = new PageAlert(alert.getText());
					path_keys.add(page_alert.getKey());
					path_objects_explored.add(page_alert);
					current_idx++;
				}
				else{
					if(current_idx == ordered_path_objects.size()-1){
						
					}

					Point p = browser.getViewportScrollOffset();
					browser.setXScrollOffset(p.getX());
					browser.setYScrollOffset(p.getY());
				}
			}
			else if(current_obj instanceof PageAlert){
				log.debug("Current path node is a PageAlert");
				PageAlert alert = (PageAlert)current_obj;
				alert.performChoice(browser.getDriver(), AlertChoice.DISMISS);
			}
			last_obj = current_obj;
			current_idx++;
		}

		if(path.getPathKeys().size() != path_keys.size()){
			path.setPathKeys(path_keys);
			path.setPathObjects(path_objects_explored);
		}
	}
	
	/**
	 * Crawls the path using the provided {@link Browser browser}
	 * @param browser
	 * @param path list of vertex keys
	 * @param user_id TODO
	 *
	 * @return {@link PageVersion result_page} state that resulted from crawling path
	 *
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws WebDriverException
	 * @throws GridException
	 * @throws URISyntaxException 
	 *
	 * @pre path != null
	 * @pre path != null
	 */
	@Deprecated
	/*
	public JourneyMessage crawlPathExplorer(List<String> keys, 
										 List<LookseeObject> path_object_list, 
										 Browser browser, 
										 String host_channel, 
										 JourneyMessage path
	) throws IOException, GridException, NoSuchElementException, WebDriverException, NoSuchAlgorithmException, PagesAreNotMatchingException, URISyntaxException{
		assert browser != null;
		assert keys != null;

		com.looksee.models.Element last_element = null;
		LookseeObject last_obj = null;
		PageState expected_page = null;

		List<String> path_keys = new ArrayList<String>(keys);
		List<LookseeObject> ordered_path_objects = PathUtils.orderPathObjects(keys, path_object_list);

		List<LookseeObject> path_objects_explored = new ArrayList<>();

		String last_url = null;
		int current_idx = 0;
		for(LookseeObject current_obj: ordered_path_objects){
			path_objects_explored.add(current_obj);
			if(current_obj instanceof PageState){
				expected_page = (PageState)current_obj;
				last_url = expected_page.getUrl();
			}
			else if(current_obj instanceof Redirect){
				Redirect redirect = (Redirect)current_obj;
				//if redirect is preceded by a page state or nothing then initiate navigation
				if(last_obj == null || last_obj instanceof PageState){
					browser.navigateTo(redirect.getStartUrl());
				}

				//if redirect follows an action then watch page transition
				last_url = redirect.getUrls().get(redirect.getUrls().size()-1);
			}
			else if(current_obj instanceof PageLoadAnimation){
			}
			else if(current_obj instanceof com.looksee.models.Element){
				last_element = (com.looksee.models.Element) current_obj;
				browser.scrollToElement(last_element);
				//BrowserUtils.detectShortAnimation(browser, expected_page.getUrl());
			}
			//String is action in this context
			else if(current_obj instanceof ActionOLD){
				ActionOLD action = (ActionOLD)current_obj;
				ActionOLD action_record = action_repo.findByKey(action.getKey());
				if(action_record==null){
					action_repo.save(action);
				}
				else{
					action = action_record;
				}

				performAction(action, last_element, browser.getDriver());

				//check for page alert presence
				Alert alert = browser.isAlertPresent();
				if(alert != null){
					log.warn("Alert was encountered!!!");
					PageAlert page_alert = new PageAlert(alert.getText());
					path_keys.add(page_alert.getKey());
					path_objects_explored.add(page_alert);
					current_idx++;
				}
				else{
					if((current_idx < ordered_path_objects.size()-1
							&& !ordered_path_objects.get(current_idx+1).getKey().contains("redirect")
							&& !ordered_path_objects.get(current_idx+1).getKey().contains("elementstate"))
							|| (current_idx == ordered_path_objects.size()-1 && !last_url.equals(BrowserUtils.sanitizeUrl(browser.getDriver().getCurrentUrl(), false)))){
						
					}

					Point p = browser.getViewportScrollOffset();
					browser.setXScrollOffset(p.getX());
					browser.setYScrollOffset(p.getY());
				}
			}
			else if(current_obj instanceof PageAlert){
				log.debug("Current path node is a PageAlert");
				PageAlert alert = (PageAlert)current_obj;
				alert.performChoice(browser.getDriver(), AlertChoice.ACCEPT);
			}
			last_obj = current_obj;
			current_idx++;
		}
		
		if(path.getSteps().size() != path_keys.size()){
			return new JourneyMessage(path.getSteps(), 
								   path.getStatus(), 
								   path.getBrowser(), 
								   path.getDomainId(), 
								   path.getAccountId());
		}
		
		return path;
	}
	*/
	
	/**
	 * Executes the given {@link ElementAction element action} pair such that
	 * the action is executed against the element
	 *
	 * @return whether action was able to be performed on element or not
	 */
	public static void performAction(Action action, com.looksee.models.Element elem, WebDriver driver) throws NoSuchElementException{
		ActionFactory actionFactory = new ActionFactory(driver);
		WebElement element = driver.findElement(By.xpath(elem.getXpath()));
		actionFactory.execAction(element, "", action);
		TimingUtils.pauseThread(500L);
	}

	/**
	 * Executes the given {@link ElementAction element action} pair such that
	 * the action is executed against the element
	 *
	 * @return whether action was able to be performed on element or not
	 */
	public static void performAction(Action action, com.looksee.models.Element elem, WebDriver driver, Point location) throws NoSuchElementException{
		ActionFactory actionFactory = new ActionFactory(driver);
		WebElement element = driver.findElement(By.xpath(elem.getXpath()));
		actionFactory.execAction(element, "", action);
		TimingUtils.pauseThread(500L);
	}
	
	public static void scrollDown(WebDriver driver, int distance)
    {
        ((JavascriptExecutor)driver).executeScript("scroll(0,"+ distance +");");
    }

	/**
	 * Handles setting up browser for path crawl and in the event of an error, the method retries until successful
	 * @param browser
	 * @param path
	 * @param host
	 * @return
	 */
	/*
	public PageState performPathExploratoryCrawl(String user_id, Domain domain, String browser_name, ExploratoryPath path, String host) {
		PageState result_page = null;
		int tries = 0;
		Browser browser = null;
		do{
			try{
				browser = BrowserConnectionHelper.getConnection(BrowserType.create(browser_name), BrowserEnvironment.DISCOVERY);
				String url = PathUtils.getFirstUrl(path.getSteps());
				log.warn("expected path url : "+url);
				browser.navigateTo(url);
				browser.moveMouseToNonInteractive(new Point(300,300));

				crawlPathExplorer(path.getPathKeys(), path.getPathObjects(), browser, host, path);

				String browser_url = browser.getDriver().getCurrentUrl();
				browser_url = BrowserUtils.sanitizeUrl(browser_url, false);
				//get last page state
				PageState last_page_state = PathUtils.getLastPageStateOLD(path.getPathObjects());
						
				//verify that screenshot does not match previous page
				result_page = browser_service.buildPageState(new URL(domain.getUrl()), browser, new URL(browser_url));
				
				PageState last_page = PathUtils.getLastPageStateOLD(path.getPathObjects());
				//result_page.setLoginRequired(last_page.isLoginRequired());
			}
			catch(NullPointerException e){
				log.warn("Null Pointer Exception happened during exploratory crawl ::  "+e.getMessage());
			}
			catch (GridException e) {
				log.warn("Grid exception encountered while trying to crawl exporatory path"+e.getMessage());
			}
			catch (NoSuchElementException e){
				log.warn("Unable to locate element while performing path crawl   ::    "+ e.getMessage());
			}
			catch (WebDriverException e) {
				log.warn("(Exploratory Crawl) web driver exception occurred : " + e.getMessage());
				//TODO: HANDLE EXCEPTION THAT OCCURS BECAUSE THE PAGE ELEMENT IS NOT ON THE PAGE
				//log.warn("WebDriver exception encountered while trying to perform crawl of exploratory path"+e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				log.error("No Such Algorithm exception encountered while trying to crawl exporatory path"+e.getMessage());
				//e.printStackTrace();
			} catch(Exception e) {
				log.warn("Exception occurred in performPathExploratoryCrawl actor. \n"+e.getMessage());
				//e.printStackTrace();
			}
			finally{
				if(browser != null){
					browser.close();
				}
			}
			tries++;
		}while(result_page == null && tries < 1000);
		
		log.warn("done crawling exploratory path");
		return result_page;
	}
	*/

	/**
	 * Handles setting up browser for path crawl and in the event of an error, the method retries until successful
	 * @param browser
	 * @param path
	 * @param host
	 * @return
	 * @throws Exception 
	 */
	@Deprecated
	/*
	public PageState performPathExploratoryCrawl(long account_id, Domain domain, String browser_name, JourneyMessage path) throws Exception {
		PageState result_page = null;
		int tries = 0;
		Browser browser = null;
		JourneyMessage new_path = path.clone();
		boolean no_such_element_exception = false;
		
		do{
			
			try{
				if(!no_such_element_exception){
					no_such_element_exception = false;
					browser = BrowserConnectionHelper.getConnection(BrowserType.create(browser_name), BrowserEnvironment.DISCOVERY);
					PageState expected_page = PathUtils.getFirstPage(path.getSteps());
					//browser.navigateTo(expected_page.getUrl());
					browser.moveMouseToNonInteractive(new Point(300,300));
					
					ExploratoryPath exploratory_path = new ExploratoryPath(path.getSteps(), path.getPathObjects());
					
					crawlPathExplorer(new_path.getSteps(), new_path.getPathObjects(), browser, domain.getUrl(), exploratory_path);
				}
				String browser_url = browser.getDriver().getCurrentUrl();
				browser_url = BrowserUtils.sanitizeUrl(browser_url, false);
				//get last page state
				PageState last_page_state = PathUtils.getLastPageStateOLD(new_path.getPathObjects());
								
				//verify that screenshot does not match previous page
				result_page = browser_service.buildPageState(new URL(domain.getUrl()), browser, new URL(browser_url));
				//result_page.setLoginRequired(last_page_state.isLoginRequired());
				return result_page;
			}
			catch(NullPointerException e){
				//e.printStackTrace();
				log.error("NPE occurred while exploratory crawl  ::   "+e.getMessage());
			} 
			catch (GridException e) {
				log.debug("Grid exception encountered while trying to crawl exporatory path"+e.getMessage());
			}
			catch (NoSuchElementException e){
				log.warn("Unable to locate element while performing path crawl   ::    "+ e.getMessage());
			}
			catch (WebDriverException e) {
				log.debug("(Exploratory Crawl) web driver exception occurred : " + e.getMessage());
				//TODO: HANDLE EXCEPTION THAT OCCURS BECAUSE THE PAGE ELEMENT IS NOT ON THE PAGE
				//log.warn("WebDriver exception encountered while trying to perform crawl of exploratory path"+e.getMessage());
			} 
			catch (NoSuchAlgorithmException e) {
				log.error("No Such Algorithm exception encountered while trying to crawl exporatory path"+e.getMessage());
				//e.printStackTrace();
			} 
			catch(Exception e) {
				log.warn("Exception occurred in performPathExploratoryCrawl using PathMessage actor. \n"+e.getMessage());
				e.printStackTrace();
			}
			finally{
				if(browser != null && !no_such_element_exception){
					browser.close();
				}
			}
			tries++;
		}while(result_page == null && tries < 100);
		return result_page;
	}
	*/
	
	/**
	 * 
	 * @param web_element
	 * @return
	 * 
	 * @pre web_element != null
	 * @pre child_element != null
	 * @pre offset != null
	 */
	public static Point generateRandomLocationWithinElementButNotWithinChildElements(WebElement web_element, ElementState child_element) {
		assert web_element != null;
		assert child_element != null;
		
		Point elem_location = web_element.getLocation();

		int left_lower_x = 0;
		int left_upper_x = child_element.getXLocation()- elem_location.getX();
		int right_lower_x = (child_element.getXLocation() - elem_location.getX()) + child_element.getWidth();
		int right_upper_x = web_element.getSize().getWidth();
		
		int top_lower_y = 0;
		int top_upper_y = child_element.getYLocation() - elem_location.getY();
		int bottom_lower_y = child_element.getYLocation() - elem_location.getY() + child_element.getHeight();
		int bottom_upper_y = web_element.getSize().getHeight();
		
		int x_coord = 0;
		int y_coord = 0;
		
		if(left_lower_x != left_upper_x && left_upper_x > 0){
			x_coord = new Random().nextInt(left_upper_x);
		}
		else {
			int difference = right_upper_x - right_lower_x;
			int x_offset = 0;
			if(difference == 0){
				x_offset = new Random().nextInt(right_upper_x);
			}
			else{
				x_offset = new Random().nextInt(difference);
			}
			x_coord = right_lower_x + x_offset;
		}
		
		if(top_lower_y != top_upper_y && top_upper_y > 0){
			y_coord = new Random().nextInt(top_upper_y);
		}
		else {
			int difference = bottom_upper_y - bottom_lower_y;
			int y_offset = 0;
			if(difference == 0){
				y_offset = new Random().nextInt(bottom_upper_y);
			}
			else{
				y_offset = new Random().nextInt(bottom_upper_y - bottom_lower_y);
			}
			y_coord = bottom_lower_y + y_offset;
		}

		return new Point(x_coord, y_coord);
	}
}
