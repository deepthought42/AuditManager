package com.looksee.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.openqa.grid.common.exception.GridException;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.cloud.storage.StorageException;
import com.looksee.browsing.Browser;
import com.looksee.browsing.form.ElementRuleExtractor;
import com.looksee.gcp.CloudVisionUtils;
import com.looksee.gcp.GoogleCloudStorage;
import com.looksee.gcp.ImageSafeSearchAnnotation;
import com.looksee.helpers.BrowserConnectionHelper;
import com.looksee.models.Domain;
import com.looksee.models.ElementState;
import com.looksee.models.Form;
import com.looksee.models.ImageElementState;
import com.looksee.models.ImageFaceAnnotation;
import com.looksee.models.ImageLandmarkInfo;
import com.looksee.models.ImageSearchAnnotation;
import com.looksee.models.Label;
import com.looksee.models.Logo;
import com.looksee.models.Page;
import com.looksee.models.PageState;
import com.looksee.models.Template;
import com.looksee.models.enums.BrowserEnvironment;
import com.looksee.models.enums.BrowserType;
import com.looksee.models.enums.ElementClassification;
import com.looksee.models.enums.TemplateType;
import com.looksee.utils.BrowserUtils;
import com.looksee.utils.ImageUtils;

import cz.vutbr.web.css.RuleSet;
import io.github.resilience4j.retry.annotation.Retry;
import us.codecraft.xsoup.Xsoup;

/**
 * A collection of methods for interacting with the {@link Browser} session object
 *
 */
@Component
public class BrowserService {
	private static Logger log = LoggerFactory.getLogger(BrowserService.class);

	@Autowired
	private ElementRuleExtractor extractor;
	
	@Autowired
	private ElementService element_service;
	
	@Autowired
	private PageService page_service;
	
	private static String[] valid_xpath_attributes = {"class", "id", "name", "title"};

	/**
	 * retrieves a new browser connection
	 *
	 * @param browser_name name of the browser (ie. firefox, chrome)
	 *
	 * @return new {@link Browser} instance
	 * @throws MalformedURLException
	 *
	 * @pre browser_name != null;
	 * @pre !browser_name.isEmpty();
	 */
	public Browser getConnection(BrowserType browser, BrowserEnvironment browser_env) throws MalformedURLException {
		assert browser != null;

		return BrowserConnectionHelper.getConnection(browser, browser_env);
	}

	/**
 	 * Constructs an {@link Element} from a JSOUP {@link Element element}
 	 * 
	 * @param xpath
	 * @param attributes
	 * @param element
	 * @param web_elem
	 * @param classification
	 * @param rendered_css_values
	 * @param screenshot_url TODO
	 * @param css_selector TODO
	 * @pre xpath != null && !xpath.isEmpty()
	 * @pre attributes != null
	 * @pre element != null
	 * @pre classification != null
	 * @pre rendered_css_values != null
	 * @pre css_values != null
	 * @pre screenshot != null
	 * 
	 * @return {@link ElementState} based on {@link WebElement} and other params
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static ElementState buildElementState(
			String xpath, 
			Map<String, String> attributes, 
			Element element,
			WebElement web_elem,
			ElementClassification classification, 
			Map<String, String> rendered_css_values, 
			String screenshot_url,
			String css_selector
	) throws IOException{
		assert xpath != null && !xpath.isEmpty();
		assert attributes != null;
		assert element != null;
		assert classification != null;
		assert rendered_css_values != null;
		assert screenshot_url != null;
		
		Point location = web_elem.getLocation();
		Dimension dimension = web_elem.getSize();
		
		String foreground_color = rendered_css_values.get("color");
		if(foreground_color == null || foreground_color.trim().isEmpty()) {
			foreground_color = "rgb(0,0,0)";
		}
		
		ElementState element_state = new ElementState(
											element.ownText().trim(),
											element.text(),
											xpath, 
											element.tagName(), 
											attributes, 
											rendered_css_values, 
											screenshot_url, 
											location.getX(), 
											location.getY(), 
											dimension.getWidth(), 
											dimension.getHeight(), 
											classification,
											element.outerHtml(),
											web_elem.isDisplayed(),
											css_selector, 
											foreground_color,
											rendered_css_values.get("background-color"),
											false);
		
		return element_state;
	}
	
	/**
 	 * Constructs an {@link Element} from a JSOUP {@link Element element}
 	 * 
	 * @param xpath
	 * @param attributes
	 * @param element
	 * @param web_elem
	 * @param classification
	 * @param rendered_css_values
	 * @param screenshot_url TODO
	 * @param css_selector TODO
	 * @pre xpath != null && !xpath.isEmpty()
	 * @pre attributes != null
	 * @pre element != null
	 * @pre classification != null
	 * @pre rendered_css_values != null
	 * @pre css_values != null
	 * @pre screenshot != null
	 * 
	 * @return {@link ElementState} based on {@link WebElement} and other params
	 * @throws IOException 
	 */
	public static ElementState buildImageElementState(
			String xpath, 
			Map<String, String> attributes, 
			Element element,
			WebElement web_elem,
			ElementClassification classification, 
			Map<String, String> rendered_css_values, 
			String screenshot_url,
			String css_selector,
			Set<ImageLandmarkInfo> landmark_info_set,
			Set<ImageFaceAnnotation> faces,
			ImageSearchAnnotation image_search_set,
			Set<Logo> logos,
			Set<Label> labels,
			ImageSafeSearchAnnotation safe_search_annotation
	) throws IOException{
		assert xpath != null && !xpath.isEmpty();
		assert attributes != null;
		assert element != null;
		assert classification != null;
		assert rendered_css_values != null;
		assert screenshot_url != null;
		
		Point location = web_elem.getLocation();
		Dimension dimension = web_elem.getSize();
		
		String foreground_color = rendered_css_values.get("color");
		if(foreground_color == null || foreground_color.trim().isEmpty()) {
			foreground_color = "rgb(0,0,0)";
		}
		
		String background_color = rendered_css_values.get("background-color");
		if(background_color == null) {
			background_color = "rgb(255,255,255)";
		}
		
		ElementState element_state = new ImageElementState(
													element.ownText().trim(),
													element.text(),
													xpath, 
													element.tagName(), 
													attributes, 
													rendered_css_values, 
													screenshot_url, 
													location.getX(), 
													location.getY(), 
													dimension.getWidth(), 
													dimension.getHeight(), 
													classification,
													element.outerHtml(),
													web_elem.isDisplayed(),
													css_selector, 
													foreground_color,
													background_color,
													landmark_info_set,
													faces,
													image_search_set,
													logos,
													labels,
													safe_search_annotation);
		
		return element_state;
	}
	
	/**
	 *Constructs a page object that contains all child elements that are considered to be potentially expandable.
	 * @param title TODO
	 * @return page {@linkplain PageState}
	 * @throws IOException 
	 * @throws XPathExpressionException 
	 * @throws Exception 
	 * 
	 * @pre browser != null
	 */
	public Page buildPage( String page_src, 
						   String page_url, 
						   String title 
    ) throws IOException, XPathExpressionException{
		assert page_url != null;
		assert page_src != null;
		log.warn("building page....");
		String url_without_params = BrowserUtils.sanitizeUrl(page_url, true);
		
		//Element root = html_doc.getElementsByTag("body").get(0);
		List<String> raw_stylesheets = Browser.extractStylesheets(page_src); 
		List<RuleSet> rule_sets = Browser.extractRuleSetsFromStylesheets(raw_stylesheets, new URL(page_url)); 
		
		String clean_source = Browser.cleanSrc(page_src);
		URL clean_url = new URL(url_without_params);
		List<com.looksee.models.Element> elements = extractElements(clean_source, clean_url, rule_sets);
				
		Page page = new Page(
				elements,
				clean_source,
				title,
				clean_url.toString(),
				clean_url.getPath());

		
		Page record = page_service.findByKey(page.getKey());
		if(record != null) {
			return record;
		}
		log.warn("built page...now saving page state...");
		return page;
	}
	
	/**
 	 * Constructs an {@link Element} from a JSOUP {@link Element element}
 	 * 
	 * @param xpath
	 * @param attributes
	 * @param element
	 * @param classification
	 * @param rendered_css_values
	 * 
	 * @return
	 * 
	 * @pre xpath != null && !xpath.isEmpty();
	 * @pre attributes != null;
	 * @pre element != null;
	 * @pre classification != null
	 * @pre rendered_css_values != null
	 */
	public static com.looksee.models.Element buildElement(
			String xpath, 
			Map<String, String> attributes, 
			Element jsoup_element, 
			ElementClassification classification, 
			Map<String, String> pre_rendered_css_values
	) {
		assert xpath != null && !xpath.isEmpty();
		assert attributes != null;
		assert jsoup_element != null;
		assert classification != null;
		assert pre_rendered_css_values != null;
		
		com.looksee.models.Element element = new com.looksee.models.Element(jsoup_element.ownText(), xpath, jsoup_element.tagName(), attributes, pre_rendered_css_values, jsoup_element.html(), classification, jsoup_element.outerHtml());

		return element;
	}

	public static String generalizeSrc(String src) {
		assert src != null;
		Document html_doc = Jsoup.parse(src);
		html_doc.select("script").remove();
		html_doc.select("link").remove();
		html_doc.select("style").remove();
		html_doc.select("iframe").remove();
		
		//html_doc.attr("id","");
		for(Element element : html_doc.getAllElements()) {
			/*
			element.removeAttr("id")
				   .removeAttr("name")
				   .removeAttr("style")
				   .removeAttr("data-id");
			*/
		    List<String>  attToRemove = new ArrayList<>();
			for (Attribute a : element.attributes()) {
				if(element.tagName().contentEquals("img") && a.getKey().contentEquals("src")) {
					continue;
				}
		        // transfer it into a list -
		        // to be sure ALL data-attributes will be removed!!!
		        attToRemove.add(a.getKey());
		    }

		    for(String att : attToRemove) {
		        element.removeAttr(att);
		   }
		}
		
		return removeComments(html_doc.html());
	}
	
	/**
	 * Removes HTML comments from html string
	 * 
	 * @param html
	 * 
	 * @return html string without comments
	 */
	public static String removeComments(String html) {
		return Pattern.compile("<!--.*?-->").matcher(html).replaceAll("");
    }
	
	/**
	 * Process used by the web crawler to build {@link PageState} from {@link PageVersion}
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws XPathExpressionException 
	 * @throws Exception
	 */
	public PageState buildPageState(URL url) throws Exception {
		assert url != null;
		
		int http_status = BrowserUtils.getHttpStatus(url);
		//usually code 301 is returned which is a redirect, which is usually transferring to https
		if(http_status == 404 || http_status == 408) {
			return null;
		}
		
		PageState page_state = null;
		boolean complete = false;
		int cnt = 0;
		do {
			log.warn("getting browser connection... ");
			Browser browser = null;
			try {
				browser = getConnection(BrowserType.CHROME, BrowserEnvironment.DISCOVERY);
				browser.navigateTo(url.toString());

				page_state = performBuildPageProcess( browser);
				complete = true;
				cnt=Integer.MAX_VALUE;
			}
			catch(MalformedURLException e) {
				break;
			}
			catch(IOException e) {
				log.warn("IOException occurred while building page state");
				e.printStackTrace();
			}
			catch(ServiceUnavailableException e) {
				log.warn("Service unavailable exception occurred while building page state");
				//e.printStackTrace();
			}
			catch(WebDriverException | GridException e) {								
				log.warn("Selenium Exception occurred while building page state :: "+url);
			}
			catch(Exception e) {
				log.warn("Exception occurred while building page state");
				e.printStackTrace();
			}
			finally {
				if(browser != null) {
					browser.close();
				}
			}
			cnt++;
		}while(!complete && cnt < 10000);
		
		return page_state;
	}
	
	/**
	 * Navigates to a url, checks that the service is available, then removes drift 
	 * 	chat client from page if it exists. Finally it builds a {@link PageState}
	 * 
	 * @param url
	 * @param browser TODO
	 * 
	 * @pre url != null;
	 * @pre browser != null
	 * 
	 * @return {@link PageState}
	 * @throws WebDriverException 
	 * @throws  
	 * 
	 * @throws MalformedURLException
	 * @throws IOException 
	 * @throws GridException 
	 */
	public PageState performBuildPageProcess(Browser browser) throws WebDriverException, IOException 
	{
		assert browser != null;
		
		if(browser.is503Error()) {
			browser.close();
			throw new ServiceUnavailableException("503(Service Unavailable) Error encountered. Starting over..");
		}
		browser.removeDriftChat();
		
		return buildPageState(browser);
	}

	/**
	 *Constructs a page object that contains all child elements that are considered to be potentially expandable.
	 * @param url_after_loading TODO
	 * @param title TODO
	 * @return page {@linkplain PageState}
	 * @throws StorageException 
	 * @throws GridException 
	 * @throws IOException 
	 * @throws XPathExpressionException 
	 * @throws Exception 
	 * 
	 * @pre browser != null
	 */
	public PageState buildPageState( Browser browser ) throws WebDriverException, IOException {
		assert browser != null;

		URL current_url = new URL(browser.getDriver().getCurrentUrl());
		String url_without_protocol = BrowserUtils.getPageUrl(current_url.toString());
		log.warn("building page state for URL :: "+current_url);
		if(current_url.toString().contains("dashboard")) {
			log.warn("BUILDING DASHBOARD PAGE STATE...");
			log.warn("url_without_protocol = "+url_without_protocol);
		}
		else if(current_url.toString().contains("?#")) {
			log.warn("BUILDING    ?#   PAGE STATE..."+current_url);
		}

		boolean is_secure = BrowserUtils.checkIfSecure(current_url);
        int status_code = BrowserUtils.getHttpStatus(current_url);

        //remove 3rd party chat apps such as drift, and ...(NB: fill in as more identified)
        //browser.removeDriftChat();
        
        //scroll to bottom then back to top to make sure all elements that may be hidden until the page is scrolled
		String source = browser.getDriver().getPageSource();
		String title = browser.getDriver().getTitle();

		//List<ElementState> elements = extractElementStates(source, url, browser);
		BufferedImage viewport_screenshot = browser.getViewportScreenshot();
		String screenshot_checksum = ImageUtils.getChecksum(viewport_screenshot);
		String viewport_screenshot_url = GoogleCloudStorage.saveImage(viewport_screenshot, 
																	  current_url.getHost(), 
																	  screenshot_checksum, 
																	  BrowserType.create(browser.getBrowserName()));
		viewport_screenshot.flush();
		
		BufferedImage full_page_screenshot = browser.getFullPageScreenshotStitched();		
		
		//BufferedImage shutterbug_fullpage_screenshot = browser.getFullPageScreenshot();
		
		/*
		if(full_page_screenshot.getHeight() < (shutterbug_fullpage_screenshot.getHeight() - viewport_screenshot.getHeight()) ) {
			full_page_screenshot = shutterbug_fullpage_screenshot;
		}
		*/
		String full_page_screenshot_checksum = ImageUtils.getChecksum(full_page_screenshot);
		String full_page_screenshot_url = GoogleCloudStorage.saveImage(full_page_screenshot, 
																		current_url.getHost(), 
																		full_page_screenshot_checksum, 
																		BrowserType.create(browser.getBrowserName()));
		full_page_screenshot.flush();
		
		String composite_url = full_page_screenshot_url;
		long x_offset = browser.getXScrollOffset();
		long y_offset = browser.getYScrollOffset();
		Dimension size = browser.getDriver().manage().window().getSize();

		PageState page_state = new PageState(
										viewport_screenshot_url,
										new ArrayList<>(),
										source,
										false,
										x_offset,
										y_offset,
										size.getWidth(),
										size.getHeight(),
										BrowserType.CHROME,
										full_page_screenshot_url,
										full_page_screenshot.getWidth(), 
										full_page_screenshot.getHeight(), 
										url_without_protocol,
										title,
										is_secure,
										status_code, 
										composite_url,
										current_url.toString());

		return page_state;
	}
	
	/**
	 * Process used by the web crawler to build {@link PageElement} list based on the xpaths on the page
	 * @param xpaths TODO
	 * @param audit_id TODO
	 * @param url TODO
	 * @param url
	 * @param height TODO
	 * @param audit_record TODO
	 * @return
	 * @throws MalformedURLException 
	 */
	public List<ElementState> buildPageElements(PageState page_state, 
												List<String> xpaths, 
												long audit_id, 
												URL url, 
												int page_height
	) throws MalformedURLException {
		assert page_state != null;
   				
		List<ElementState> elements = new ArrayList<>();
		Map<String, ElementState> elements_mapped = new HashMap<>();
		boolean rendering_incomplete = true;
		URL sanitized_url = new URL(BrowserUtils.sanitizeUserUrl( page_state.getUrl() ));
		String page_url = sanitized_url.toString();
		
		int cnt = 0;
		do {
			Browser browser = null;
			
			try {
				browser = getConnection(BrowserType.CHROME, BrowserEnvironment.DISCOVERY);
				browser.navigateTo(page_url);
				if(browser.is503Error()) {
					throw new FiveZeroThreeException("503 Error encountered. Starting over..");
				}
				browser.removeDriftChat();
				
				//get ElementState List by asking multiple bots to build xpaths in parallel
				//for each xpath then extract element state
				elements = getDomElementStates(page_state, xpaths, browser, elements_mapped, audit_id, sanitized_url, page_height);
				break;
			}
			catch (NullPointerException e) {
				log.warn("NPE thrown during element state extraction");
				//e.printStackTrace();
			}
			catch(MalformedURLException e) {
				log.warn("Unable to get browser connection to build page elements : "+page_url);
				continue;
			}
			catch(FiveZeroThreeException e) {
				log.warn("503 exception occurred while accessing "+page_url);
			}
			catch(WebDriverException | GridException e) {
				log.warn("Webdriver exception occurred ... "+page_url);
				//e.printStackTrace();
			}	
			finally {
				if(browser != null) {
					browser.close();
				}
			}
			cnt++;
		}while(rendering_incomplete && cnt < 10000);

		return elements;
	}
	
	/**
	 * Process used by the web crawler to build {@link PageElement} list based on the xpaths on the page
	 * @param xpaths TODO
	 * @param audit_id TODO
	 * @param url TODO
	 * @param url
	 * @param height TODO
	 * @param audit_record TODO
	 * @return
	 * @throws MalformedURLException 
	 */
	public List<ElementState> buildPageElementsWithoutNavigation(PageState page_state, 
																 List<String> xpaths, 
															 	 long audit_id, 
															 	 int page_height,
															 	 Browser browser
	) throws MalformedURLException {
		assert page_state != null;
   				
		List<ElementState> elements = new ArrayList<>();
		Map<String, ElementState> elements_mapped = new HashMap<>();
		//boolean rendering_incomplete = true;
		URL sanitized_url = new URL(BrowserUtils.sanitizeUserUrl( page_state.getUrl() ));
		
		elements = getDomElementStates(page_state, xpaths, browser, elements_mapped, audit_id, sanitized_url, page_height);

		return elements;
	}
	
	
	@Retry(name="webdriver")
	private boolean openBrowserAndBuildElementStates(List<ElementState> elements,
												  Map<String, ElementState> elements_mapped, 
												  PageState page_state,
												  List<String> xpaths,
												  long audit_id,
												  int page_height
    ) throws MalformedURLException {
		URL sanitized_url = new URL(BrowserUtils.sanitizeUserUrl( page_state.getUrl() ));
		String page_url = sanitized_url.toString();
		Browser browser = null;
		
		try {
			browser = getConnection(BrowserType.CHROME, BrowserEnvironment.DISCOVERY);
			browser.navigateTo(page_url);
			if(browser.is503Error()) {
				throw new FiveZeroThreeException("503 Error encountered. Starting over..");
			}
			browser.removeDriftChat();
			
			//get ElementState List by asking multiple bots to build xpaths in parallel
			//for each xpath then extract element state
			elements = getDomElementStates(page_state, xpaths, browser, elements_mapped, audit_id, sanitized_url, page_height);
			return false;
		}
		catch (NullPointerException e) {
			log.warn("NPE thrown during element state extraction");
			//e.printStackTrace();
		}
		catch(MalformedURLException e) {
			log.warn("Unable to get browser connection to build page elements : "+page_url);
			return false;
		}
		catch(FiveZeroThreeException e) {
			log.warn("503 exception occurred while accessing "+page_url);
		}	
		finally {
			if(browser != null) {
				browser.close();
			}
		}
		return true;
	}
	
	/**
	 * identify and collect data for elements within the Document Object Model 
	 * @param audit_record_id TODO
	 * @param url TODO
	 * @param url
	 * @param page_height TODO
	 * @param page_source
	 * @param rule_sets TODO
	 * @param reviewed_xpaths
	 * @return
	 * @throws IOException
	 * @throws XPathExpressionException 
	 * 
	 * @pre xpaths != null
	 * @pre browser != null
	 * @pre element_states_map != null
	 * @pre page_state != null
	 */
	private List<ElementState> getDomElementStates(
			PageState page_state, 
			List<String> xpaths, 
			Browser browser, 
			Map<String, ElementState> element_states_map, 
			long audit_record_id, 
			URL url, 
			int page_height
	) {
		assert xpaths != null;
		assert browser != null;
		assert element_states_map != null;
		assert page_state != null;
		
		List<ElementState> visited_elements = new ArrayList<>();
		List<ElementState> filtered_elements = new ArrayList<>();
		Map<String, Boolean> overlapped_elements = new HashMap<>();

		String body_src = extractBody(page_state.getSrc());
		
		Document html_doc = Jsoup.parse(body_src);
		String host = url.getHost();
				
		for(String xpath : xpaths) {
			if(element_states_map.containsKey(xpath)) {
				continue;
			}
			
			try {
				WebElement web_element = browser.findElement(xpath);
				if(web_element == null) {
					continue;
				}
				Dimension element_size = web_element.getSize();
				Point element_location = web_element.getLocation();
				browser.scrollToElement(xpath, web_element);
				if(element_location.getY() >= page_height || element_size.getHeight() >= page_height) {
					continue;
				}
				
				//check if element is visible in pane and if not then continue to next element xpath
				if( !web_element.isDisplayed()
						|| !hasWidthAndHeight(element_size)
						|| doesElementHaveNegativePosition(element_location)) {
					continue;
				}
				
				String css_selector = generateCssSelectorFromXpath(xpath);
				String element_screenshot_url = "";

				BufferedImage element_screenshot = null;
				
				if(!BrowserUtils.isLargerThanViewport(element_size, page_state.getViewportWidth(), page_state.getViewportHeight())) {
					try {
							
						//extract element screenshot from full page screenshot
						//BufferedImage element_screenshot = page_screenshot.getSubimage(element_location.getX(), element_location.getY(), width, height);
						element_screenshot = browser.getElementScreenshot(web_element);
						String screenshot_checksum = ImageUtils.getChecksum(element_screenshot);
						
						element_screenshot_url = GoogleCloudStorage.saveImage(element_screenshot, host, screenshot_checksum, BrowserType.create(browser.getBrowserName()));
					}
					catch( Exception e) {
						//do nothing
						/*
						log.warn("element height :: "+element_size.getHeight());
						log.warn("Element Y location ::  "+ element_location.getY());
						log.warn("element width :: "+element_size.getWidth());
						log.warn("Element X location ::  "+ element_location.getX());
						*/
						try {
							BufferedImage full_page_screenshot = ImageIO.read(new URL(page_state.getFullPageScreenshotUrlComposite()));
							int width = element_size.getWidth();
							int height = element_size.getHeight();
							
							if( (element_location.getX() + element_size.getWidth()) > full_page_screenshot.getWidth() ) {
								width = full_page_screenshot.getWidth() - element_location.getX()-1;
							}
							
							if( (element_location.getY() + element_size.getHeight()) > full_page_screenshot.getHeight() ) {
								height = full_page_screenshot.getHeight() - element_location.getY()-1;
							}
							
							element_screenshot = full_page_screenshot.getSubimage(element_location.getX(), element_location.getY(), width, height);
						}
						catch(Exception e1){
							e1.printStackTrace();
						}
						//e.printStackTrace();
					}
				}
				else {
					//TODO: extract image from full page screenshot manually
				}
				
				
				Map<String, String> rendered_css_props = Browser.loadCssProperties(web_element, browser.getDriver());
				Map<String, String> attributes = browser.extractAttributes(web_element);

				ElementClassification classification = null;
				List<WebElement> children = getChildElements(web_element);
				
				if(children.isEmpty()) {
					classification = ElementClassification.LEAF;
				}
				else {
					classification = ElementClassification.ANCESTOR;
				}
				
				//load json element
				Elements elements = Xsoup.compile(xpath).evaluate(html_doc).getElements();
				if(elements.size() == 0) {
					log.warn("NO ELEMENTS WITH XPATH FOUND :: "+xpath);
				}
								
				Element element = elements.first();
				

				if(isImageElement(web_element) && element_screenshot != null) {
					//retrieve image landmark properties from google cloud vision
					Set<ImageLandmarkInfo> landmark_info_set = CloudVisionUtils.extractImageLandmarks(element_screenshot);
					
					//retrieve image faces properties from google cloud vision
					Set<ImageFaceAnnotation> faces = CloudVisionUtils.extractImageFaces(element_screenshot);
					
					//retrieve image reverse image search properties from google cloud vision
					ImageSearchAnnotation image_search_set = CloudVisionUtils.searchWebForImageUsage(element_screenshot);
					ImageSafeSearchAnnotation img_safe_search_annotation = CloudVisionUtils.detectSafeSearch(element_screenshot);
					
					//retrieve image logos from google cloud vision
					Set<Logo> logos = new HashSet<>();//CloudVisionUtils.extractImageLogos(element_screenshot);

					//retrieve image labels
					Set<Label> labels = CloudVisionUtils.extractImageLabels(element_screenshot);
					ElementState element_state = buildImageElementState(xpath, 
																	   attributes, 
																	   element, 
																	   web_element, 
																	   classification, 
																	   rendered_css_props, 
																	   element_screenshot_url,
																	   css_selector,
																	   landmark_info_set,
																	   faces,
																	   image_search_set,
																	   logos,
																	   labels,
																	   img_safe_search_annotation);
					
					element_states_map.put(xpath, element_state);
					visited_elements.add(element_state);
				}
				else {
					ElementState element_state = buildElementState(xpath, 
																   attributes, 
																   element, 
																   web_element, 
																   classification, 
																   rendered_css_props, 
																   element_screenshot_url,
																   css_selector);
					element_states_map.put(xpath, element_state);
					visited_elements.add(element_state);
				}
				
				//filter all elements that have dimensions that are within another element and have a lower z-index
				for(ElementState element1: visited_elements) {
					if(filtered_elements.contains(element1)) {
						continue;
					}
					boolean overlap_exists = false;
					for(ElementState element2: visited_elements) {
						if(element1.getKey().equals(element2.getKey()) || overlapped_elements.containsKey(element2.getKey())) {
							continue;
						}
						
						//boolean values for equality of element1 and element2 x and y value
						boolean x_overlap = element1.getXLocation() >= element2.getXLocation() && (element1.getXLocation()+element1.getWidth()) <= (element2.getXLocation()+element2.getWidth());
						boolean y_overlap = element1.getYLocation() >= element2.getYLocation() && (element1.getYLocation()+element1.getHeight()) <= (element2.getYLocation()+element2.getHeight());
						
						//log.warn("element1 z-index :: "+element1.getRenderedCssValues().get("z-index"));
						//log.warn("element2 z-index :: "+element2.getRenderedCssValues().get("z-index"));

						String element1_z_index = element1.getRenderedCssValues().get("z-index");
						if(element1_z_index.contentEquals("auto")) {
							element1_z_index = "0";
						}
						String element2_z_index = element2.getRenderedCssValues().get("z-index");
						if(element2_z_index.contentEquals("auto")) {
							element2_z_index = "0";
						}
						boolean z_index_overlap = Integer.parseInt(element1_z_index) < Integer.parseInt(element2_z_index);
						if(x_overlap && y_overlap && z_index_overlap) {
							overlap_exists = true;
							break;
						}
					}
					
					if(!overlap_exists) {
						filtered_elements.add(element1);
					}
					else {
						overlapped_elements.put(element1.getKey(), Boolean.TRUE);
					}
				}
			}
			catch(NoSuchElementException e) {
				//log.warn("No such element found :: "+xpath+"       ;;    on page : "+page_state.getUrl());
				element_states_map.put(xpath, null);
			}
			catch (StaleElementReferenceException e) {
				log.warn("Stale element exception thrown while retrieving element with xpath :: "+xpath +"; On page with url ::  "+page_state.getUrl());
				element_states_map.put(xpath, null);
			}
			catch(NullPointerException e) {
				log.warn("There was an NPE error finding element with xpath .... "+xpath + "   ;;   ON page :: "+page_state.getUrl());
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.warn("IOException occurred while building elements");
				//e.printStackTrace();
			}
		}
		return filtered_elements;
	}

	/**
	 * Checks if element tag is 'img'
	 * @param web_element
	 * @return
	 */
	private boolean isImageElement(WebElement web_element) {
		return web_element.getTagName().equalsIgnoreCase("img");
	}

	/** MESSAGE GENERATION METHODS **/
	static String[] data_extraction_messages = {
			"Locating elements",
			"Create an account to get results faster",
			"Looking for content",
			"Having a look-see",
			"Extracting colors",
			"Checking fonts",
			"Pssst. Get results faster by logging in",
			"Mapping page structure",
			"Locating links",
			"Extracting navigation",
			"Pssst. Get results faster by logging in",
			"Create an account to get results faster",
			"Mapping CSS styles",
			"Generating unique CSS selector",
			"Mapping forms",
			"Measuring whitespace",
			"Pssst. Get results faster by logging in",
			"Create an account to get results faster",
			"Mapping attributes",
			"Mapping attributes",
			"Mapping attributes",
			"Mapping attributes",
			"Mapping attributes",
			"Mapping attributes",
			"Extracting color palette",
			"Looking for headers",
			"Mapping content structure",
			"Create an account to get results faster",
			"Wow! There's a lot of elements here",
			"Wow! There's a lot of elements here",
			"Wow! There's a lot of elements here",
			"Wow! There's a lot of elements here",
			"Wow! There's a lot of elements here",
			"Wow! There's a lot of elements here",
			"Wow! There's a lot of elements here",
			"Wow! There's a lot of elements here",
			"Wow! There's a lot of elements here",
			"Crunching the numbers",
			"Pssst. Get results faster by logging in",
			"Create an account to get results faster",
			"Searching for areas of interest",
			"Evaluating purpose of webpage",
			"Just a single page audit? Login to audit a domain",
			"Labeling icons",
			"Labeling images",
			"Labeling logos",
			"Applying customizations",
			"Checking for overfancification",
			"Grouping by proximity",
			"Almost there!",
			"Create an account to get results faster",
			"Labeling text elements",
			"Labeling links",
			"Pssst. Get results faster by logging in",
			"Labeling images",
			"Mapping form fields",
			"Extracting templates",
			"Contemplating the meaning of the universe",
			"Checking template structure"
			};
	/**
	 * Select random message from list of data extraction messages. 
	 * 
	 * @return
	 */
	private String generateDataExtractionMessage() {		
		int random_idx = (int) (Math.random() * (data_extraction_messages.length-1));
		return data_extraction_messages[random_idx];
	}

	/** MESSAGE GENERATION METHODS **/
	
	/**
	 * Retrieves transparency value from rgba string
	 * @param css_value
	 * @return
	 */
	private boolean hasTransparency(String css_value) {
		assert css_value != null;
		assert !css_value.isEmpty();
		
		assert css_value.startsWith("rgba(");
		if(css_value.startsWith("rgb(")) {
			return false;
		}
		
		css_value = css_value.replace("rgba(", "");
		css_value = css_value.replace(")", "");
		String[] rgba = css_value.split(",");
		double transparency_value = Double.parseDouble(rgba[3].trim());

		return transparency_value < 1.0;
	}

	/**
	 * Checks if {@link Element element} is a part of a slideshow container
	 * 
	 * @param element
	 * @return
	 */
	private static boolean isSliderElement(Element element) {
		for(org.jsoup.nodes.Attribute attr : element.attributes()) {
			if(attr.getValue().toLowerCase().contains("slider") || attr.getKey().toLowerCase().contains("slider")) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Removes all {@link Element}s that have a negative or 0 value for the x or y coordinates
	 *
	 * @param web_elements
	 * @param is_element_state
	 *
	 * @pre web_elements != null
	 *
	 * @return filtered list of {@link Element}s
	 */
	public static List<ElementState> filterElementsWithNegativePositions(List<ElementState> web_elements, boolean is_element_state) {
		assert(web_elements != null);

		List<ElementState> elements = new ArrayList<>();

		for(ElementState element : web_elements){
			if(element.getXLocation() >= 0 && element.getYLocation() >= 0){
				elements.add(element);
			}
		}

		return elements;
	}

	public static List<WebElement> filterNonDisplayedElements(List<WebElement> web_elements) {
		List<WebElement> filtered_elems = new ArrayList<WebElement>();
		for(WebElement elem : web_elements){
			if(elem.isDisplayed()){
				filtered_elems.add(elem);
			}
		}
		
		return filtered_elems;
	}

	public static List<WebElement> filterNonChildElements(List<WebElement> web_elements) {
		List<WebElement> filtered_elems = new ArrayList<WebElement>();
		for(WebElement elem : web_elements){
			boolean is_child = getChildElements(elem).isEmpty();
			if(is_child){
				filtered_elems.add(elem);
			}
		}
		
		return filtered_elems;
	}

	public static List<WebElement> filterElementsWithNegativePositions(List<WebElement> web_elements) {
		List<WebElement> elements = new ArrayList<>();

		for(WebElement element : web_elements){
			Point location = element.getLocation();
			if(location.getX() >= 0 && location.getY() >= 0){
				elements.add(element);
			}
		}

		return elements;
	}
	
	public static boolean doesElementHaveNegativePosition(Point location) {
		return location.getX() < 0 || location.getY() < 0;
	}

	public static boolean hasWidthAndHeight(Dimension dimension) {
		return dimension.getHeight() > 1 && dimension.getWidth() > 1;
	}

	/**
	 * Filters out html, body, link, title, script, meta, head, iframe, or noscript tags
	 *
	 * @param tag_name
	 *
	 * @pre tag_name != null
	 *
	 * @return true if tag name is html, body, link, title, script, meta, head, iframe, or noscript
	 */
	public static boolean isStructureTag(String tag_name) {
		assert tag_name != null;

		return "head".contentEquals(tag_name) || "link".contentEquals(tag_name) 
				|| "script".contentEquals(tag_name) || "g".contentEquals(tag_name) 
				|| "path".contentEquals(tag_name) || "svg".contentEquals(tag_name) 
				|| "polygon".contentEquals(tag_name) || "br".contentEquals(tag_name) 
				|| "style".contentEquals(tag_name) || "polyline".contentEquals(tag_name) 
				|| "use".contentEquals(tag_name) || "template".contentEquals(tag_name) 
				|| "audio".contentEquals(tag_name)  || "iframe".contentEquals(tag_name)
				|| "noscript".contentEquals(tag_name) || "meta".contentEquals(tag_name) 
				|| "base".contentEquals(tag_name) || "em".contentEquals(tag_name);
	}

	public static List<WebElement> filterNoWidthOrHeight(List<WebElement> web_elements) {
		List<WebElement> elements = new ArrayList<WebElement>(web_elements.size());
		for(WebElement element : web_elements){
			Dimension dimension = element.getSize();
			if(dimension.getHeight() > 1 && dimension.getWidth() > 1){
				elements.add(element);
			}
		}

		return elements;
	}

	public static List<ElementState> filterNoWidthOrHeight(List<ElementState> web_elements, boolean is_element_state) {
		List<ElementState> elements = new ArrayList<>(web_elements.size());
		for(ElementState element : web_elements){
			if(element.getHeight() > 1 && element.getWidth() > 1){
				elements.add(element);
			}
		}

		return elements;
	}

	/**
	 * Checks if {@link WebElement element} is visible in the current viewport window or not
	 * 
	 * @param browser {@link Browser browser} connection to use 
	 * @param location {@link Point point} where the element top left corner is located
	 * @param size {@link Dimension size} of the element
	 * 
	 * @return true if element is rendered within viewport, otherwise false
	 */
	public static boolean isElementVisibleInPane(Browser browser, Point location, Dimension size){
		assert browser != null;
		assert location != null;
		assert size != null;

		Point offsets = browser.getViewportScrollOffset();
		browser.setXScrollOffset(offsets.getX());
		browser.setYScrollOffset(offsets.getY());
		
		long y_offset = browser.getYScrollOffset();
		long x_offset = browser.getXScrollOffset();

		int x = location.getX();
		int y = location.getY();

		int height = size.getHeight();
		int width = size.getWidth();

		return x >= x_offset 
				&& y >= y_offset 
				&& ((x-x_offset)+width) <= (browser.getViewportSize().getWidth())
				&& ((y-y_offset)+height) <= (browser.getViewportSize().getHeight());
	}

	/**
	 * Checks if {@link WebElement element} is visible in the current viewport window or not
	 * 
	 * @param browser {@link Browser browser} connection to use 
	 * @param size {@link Dimension size} of the element
	 * 
	 * @return true if element is rendered within viewport, otherwise false
	 */
	public static boolean doesElementFitInViewport(Browser browser, Point position, Dimension size){
		assert browser != null;
		assert size != null;

		int height = size.getHeight();
		int width = size.getWidth();

		return width <= (browser.getViewportSize().getWidth())
				&& height <= (browser.getViewportSize().getHeight())
				&& position.getX() < browser.getViewportSize().getWidth()
				&& position.getX() >= 0
				&& position.getY() >= 0;
	}
	
	/**
	 * Get immediate child elements for a given element
	 *
	 * @param elem	WebElement to get children for
	 * @return list of WebElements
	 */
	public static List<WebElement> getChildElements(WebElement elem) throws WebDriverException{
		return elem.findElements(By.xpath("./*"));
	}

	/**
	 * Get immediate child elements for a given element
	 *
	 * @param elem	WebElement to get children for
	 * @return list of WebElements
	 */
	public static List<WebElement> getNestedElements(WebElement elem) throws WebDriverException{
		return elem.findElements(By.xpath(".//*"));
	}

	/**
	 * Get immediate parent elements for a given element
	 *
	 * @param elem	{@linkplain WebElement) to get parent of
	 * @return parent {@linkplain WebElement)
	 */
	public WebElement getParentElement(WebElement elem) throws WebDriverException{
		return elem.findElement(By.xpath(".."));
	}

	public static String cleanAttributeValues(String attribute_values_string) {
		String escaped = attribute_values_string.replaceAll("[\\t\\n\\r]+"," ");
		escaped = escaped.trim().replaceAll("\\s+", " ");
		escaped = escaped.replace("\"", "\\\"");
		return escaped.replace("\'", "'");
	}

	/**
	 * generates a unique xpath for this element.
	 *
	 * @return an xpath that identifies this element uniquely
	 */
	public String generateXpath(WebElement element, WebDriver driver, Map<String, String> attributes){
		List<String> attributeChecks = new ArrayList<>();
		List<String> valid_attributes = Arrays.asList(valid_xpath_attributes);
		
		String xpath = "/"+element.getTagName();
		for(String attr : attributes.keySet()){
			if(valid_attributes.contains(attr)){
				String attribute_values =attributes.get(attr);
				String trimmed_values = cleanAttributeValues(attribute_values.trim());

				if(trimmed_values.length() > 0 
						&& !BrowserUtils.isJavascript(trimmed_values)) {
					attributeChecks.add("contains(@" + attr + ",\"" + trimmed_values.split(" ")[0] + "\")");
				}
			}
		}
		if(attributeChecks.size()>0){
			xpath += "["+attributeChecks.get(0).toString() + "]";
		}

	    WebElement parent = element;
	    String parent_tag_name = parent.getTagName();
	    while(!"html".equals(parent_tag_name) && !"body".equals(parent_tag_name)){
	    	try{
	    		parent = getParentElement(parent);
	    		if(driver.findElements(By.xpath("//"+parent.getTagName() + xpath)).size() == 1){
	    			return "//"+parent.getTagName() + xpath;
	    		}
	    		else{
		    		xpath = "/" + parent.getTagName() + xpath;
	    		}
	    	}catch(InvalidSelectorException e){
	    		parent = null;
	    		log.warn("Invalid selector exception occurred while generating xpath through parent nodes");
	    		break;
	    	}
	    }
	    xpath = "/"+xpath;
		return uniqifyXpath(element, xpath, driver);
	}

	/**
	 * generates a unique xpath for this element.
	 *
	 * @return an xpath that identifies this element uniquely
	 */
	public static String generateXpathUsingJsoup(Element element, Document doc, Attributes attributes, Map<String, Integer> xpath_cnt){
		List<String> attributeChecks = new ArrayList<>();
		List<String> valid_attributes = Arrays.asList(valid_xpath_attributes);
		Element element_copy = element.clone();
		String xpath = "/"+element.tagName();
		for(org.jsoup.nodes.Attribute attr : attributes.asList()){
			if(valid_attributes.contains(attr.getKey())){
				String attribute_values = attr.getValue();
				String trimmed_values = cleanAttributeValues(attribute_values.trim());
				//check if attribute is auto generated
				String reduced_values = removeAutoGeneratedValues(trimmed_values);
				if(reduced_values.length() > 0 && !reduced_values.contains("javascript") && !reduced_values.contains("void()")){
					attributeChecks.add("contains(@" + attr.getKey() + ",\"" + reduced_values.split(" ")[0] + "\")");
				}
			}
		}

		if(attributeChecks.size()>0){
			xpath += "["+ attributeChecks.get(0).toString()+"]";
		}

		Element last_element = element;
		Element parent = null;
		String last_element_tagname = last_element.tagName();
	    while(!"html".equals(last_element_tagname) && !"body".equals(last_element_tagname)){
	    	try{
	    		parent = last_element.parent();

	    		if(!isStructureTag(parent.tagName())){
	    			Elements elements = Xsoup.compile("//"+parent.tagName() + xpath).evaluate(doc).getElements();
		    		if( elements.isEmpty()){
		    			break;
		    		}
	    			else if( elements.size() == 1){
		    			return "//"+parent.tagName() + xpath;
		    		}
		    		else{
			    		xpath = "/" + parent.tagName() + xpath;
		    		}
		    		last_element = parent;
		    		last_element_tagname = last_element.tagName();
	    		}
	    		else{
	    			log.warn("Encountered structure tag. Aborting element xpath extraction..");
	    			break;
	    		}
	    	}catch(InvalidSelectorException e){
	    		parent = null;
	    		log.warn("Invalid selector exception occurred while generating xpath through parent nodes");
	    		break;
	    	}
	    }
	    if(!xpath.startsWith("//")){
			xpath = "/"+xpath;
		}

		return uniqifyXpath(element_copy, xpath, doc, xpath_cnt);
	}
	
	/**
	 * generates a unique xpath for this element.
	 *
	 * @return an xpath that identifies this element uniquely
	 */
	public static String generateCssSelectorFromXpath(String xpath){
		List<String> selectors = new ArrayList<>();
		
		//split xpath on '/' character
		String[] xpath_selectors = xpath.split("/");
		for(String xpath_selector : xpath_selectors) {
			//transform selector to css selector
			String css_select = transformXpathSelectorToCss(xpath_selector);
			selectors.add(css_select);
		}
		
		return buildCssSelector(selectors);
	}

	/**
	 * combines list of sub selectors into cohesive css_selector
	 * @param selectors
	 * @return
	 */
	private static String buildCssSelector(List<String> selectors) {
		String css_selector = "";
		
		for(String selector : selectors) {
			if(css_selector.isEmpty() && !selector.isEmpty()) {
				css_selector = selector;
			}
			else if(!css_selector.isEmpty() && !selector.isEmpty()){
				css_selector += " " + selector;
			}
		}
		
		return css_selector;
	}

	public static String transformXpathSelectorToCss(String xpath_selector) {
		String selector = "";
		
		//convert index value with format '[integer]' to css format		
		String pattern_string = "(\\[([0-9]+)\\])";
        Pattern pattern_index = Pattern.compile(pattern_string);
        Matcher matcher = pattern_index.matcher(xpath_selector);
        if(matcher.find()) {
        	String match = matcher.group(1);
        	match = match.replace("[", "");
        	match = match.replace("]", "");
        	int element_index = Integer.parseInt(match);
        	selector = xpath_selector.replaceAll(pattern_string, "");

			selector += ":nth-child(" + element_index + ")";
        }
        else {
        	selector = xpath_selector;
        }
        
		return selector.trim();
	}

	private static String removeAutoGeneratedValues(String trimmed_values) {
		String[] values = trimmed_values.split(" ");
		List<String> reduced_vals = new ArrayList<>();
		for(String val : values){
			//check if value is auto-generated
			if(!isAutoGenerated(val)){
				reduced_vals.add(val);
			}
		}
		return String.join(" ", reduced_vals);
	}

	private static boolean isAutoGenerated(String val) {
		//check if value ends in a number
		return val.length() > 0 && Character.isDigit(val.charAt(val.length()-1));
	}

	/**
	 * generates a unique xpath for this element.
	 *
	 * @return an xpath that identifies this element uniquely
	 */
	public static Map<String, String> generateAttributesMapUsingJsoup(Element element){
		Map<String, String> attributes = new HashMap<>();
		for(Attribute attribute : element.attributes() ){
			attributes.put(attribute.getKey(), attribute.getValue());
		}

		return attributes;
	}

	/**
	 * creates a unique xpath based on a given hash of xpaths
	 *
	 * @param driver
	 * @param xpathHash
	 *
	 * @return
	 */
	public static String uniqifyXpath(Element elem, String xpath, Document doc, Map<String, Integer> xpath_cnt){
		try {
			List<Element> elements = Xsoup.compile(xpath).evaluate(doc).getElements();
			if(elements.size() > 1){
				int count = 0;
				if(xpath_cnt.containsKey(xpath)){
					count = xpath_cnt.get(xpath);
				}
				xpath_cnt.put(xpath, ++count);
				String unique_xpath = "("+xpath+")[" + count + "]";
				return unique_xpath;
			}

		}catch(InvalidSelectorException e){
			log.warn(e.getMessage());
		}

		return xpath;
	}

	/**
	 * creates a unique xpath based on a given hash of xpaths
	 *
	 * @param driver
	 * @param xpathHash
	 *
	 * @return
	 */
	public static String uniqifyXpath(WebElement elem, String xpath, WebDriver driver){
		try {
			List<WebElement> elements = driver.findElements(By.xpath(xpath));
			String element_tag_name = elem.getTagName();

			if(elements.size()>1){
				int count = 1;
				for(WebElement element : elements){
					if(element.getTagName().equals(element_tag_name)
							&& element.getLocation().getX() == elem.getLocation().getX()
							&& element.getLocation().getY() == elem.getLocation().getY()){
						return "("+xpath+")[" + count + "]";
					}
					count++;
				}
			}

		}catch(InvalidSelectorException e){
			log.error(e.getMessage());
		}

		return xpath;
	}

	/**
	 * Extracts all forms including the child inputs and associated labels.
	 *
	 * @param elem
	 * @param tag
	 * @param driver
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public Set<Form> extractAllForms(long account_id, Domain domain, Browser browser) throws Exception {
		Set<Form> form_list = new HashSet<Form>();
		log.info("extracting forms from page with url    ::     "+browser.getDriver().getCurrentUrl());
		List<WebElement> form_elements = browser.getDriver().findElements(By.xpath("//form"));

		//String host = domain.getHost();
		for(WebElement form_elem : form_elements){
			//BrowserUtils.detectShortAnimation(browser, page.getUrl());
			if(!form_elem.isDisplayed() || doesElementHaveNegativePosition(form_elem.getLocation())) {
				continue;
			}
			
			//BufferedImage img = browser.getElementScreenshot(form_elem);
			//String checksum = PageState.getFileChecksum(img);
			//Map<String, String> css_map = Browser.loadCssProperties(form_elem);
			com.looksee.models.Element form_tag = new com.looksee.models.Element(
					form_elem.getText(), 
					uniqifyXpath(form_elem, "//form", browser.getDriver()), 
					form_elem.getTagName(), 
					browser.extractAttributes(form_elem), 
					new HashMap<>(), 
					form_elem.getAttribute("innerHTML"), 
					ElementClassification.ANCESTOR, 
					form_elem.getAttribute("outerHTML"));
			//String screenshot_url = UploadObjectSingleOperation.saveImageToS3ForUser(img, host, checksum, BrowserType.create(browser.getBrowserName()), user_id);
			//form_tag.setScreenshotUrl(screenshot_url);
			form_tag = element_service.saveFormElement(form_tag);
			
			/*
			double[] weights = new double[1];
		
			Set<Form> forms = domain_service.getForms(account_id, domain.getUrl());
			Form form = new Form(form_tag, 
								 new ArrayList<com.looksee.models.Element>(), 
								 findFormSubmitButton(form_elem, browser),
								 "Form #"+(forms.size()+1), 
								 weights, 
								 FormType.UNKNOWN, 
								 new Date(), 
								 FormStatus.DISCOVERED );

			List<WebElement> input_elements =  form_elem.findElements(By.tagName("input"));
			
			input_elements = BrowserService.filterNonDisplayedElements(input_elements);
			form.setFormFields(buildFormFields(account_id, input_elements, browser));

			log.info("weights :: "+ form.getPrediction());
			form.setType(FormType.UNKNOWN);
			form.setDateDiscovered(new Date());
			log.info("form record discovered date :: "+form.getDateDiscovered());

			Form form_record = form_service.findByKey(account_id, domain.getUrl(), form.getKey());
			if(form_record != null) {
				continue;
			}

			int form_count = domain_service.getFormCount(account_id, domain.getUrl());
			form.setName("Form #"+(form_count+1));
			log.info("name :: "+form.getName());
			
			form_list.add(form);
			*/
		}
		return form_list;
	}
	
	

	private List<com.looksee.models.Element> buildFormFields(long account_id, List<WebElement> input_elements, Browser browser) throws IOException {
		List<com.looksee.models.Element> elements = new ArrayList<>();
		for(WebElement input_elem : input_elements){
			boolean submit_elem_found = false;

			Map<String, String> attributes = browser.extractAttributes(input_elem);
			for(String attribute : attributes.keySet()){
				if(attributes.get(attribute).contains("submit")){
					submit_elem_found = true;
					break;
				}
			}

			if(submit_elem_found){
				continue;
			}
			
			if(input_elem.getLocation().getX() < 0 || input_elem.getLocation().getY() < 0){
				log.warn("element location x or y are negative");
				continue;
			}
			
			com.looksee.models.Element input_tag = new com.looksee.models.Element(input_elem.getText(),
					generateXpath(input_elem, browser.getDriver(), attributes), 
					input_elem.getTagName(), 
					attributes, 
					new HashMap<>(), 
					input_elem.getAttribute("innerHTML"), 
					input_elem.getAttribute("outerHTML"));
			com.looksee.models.Element tag_record = element_service.findByKeyAndUserId(account_id, input_tag.getKey());
			if( tag_record != null ) {
				input_tag = tag_record;
			}
			
			/*
			if( input_tag.getViewportScreenshotUrl() == null  || input_tag.getViewportScreenshotUrl().isEmpty()) {
				BufferedImage img = browser.getElementScreenshot(input_elem);
				String checksum = PageState.getFileChecksum(img);
				
				String screenshot = UploadObjectSingleOperation.saveImageToS3ForUser(img, (new URL(browser.getDriver().getCurrentUrl())).getHost(), checksum, BrowserType.create(browser.getBrowserName()), user_id);

				img.flush();
			}
			*/
			input_tag.getRules().addAll(extractor.extractInputRules(input_tag));
			input_tag = element_service.saveFormElement(input_tag);
			log.warn("rules applied to input tag   ::   "+input_tag.getRules().size());

			elements.add(input_tag);
		}
		
		return elements;
	}

	/**
	 * locates and returns the form submit button
	 * 
	 * @param form_elem
	 * @return
	 * @throws Exception
	 * 
	 * @pre user_id != null
	 * @pre !user_id.isEmpty()
	 * @pre form_elem != null
	 * @pre browser != null;
	 */
	@Deprecated
	private com.looksee.models.Element findFormSubmitButton(WebElement form_elem, 
															Browser browser
	) throws Exception {
		assert form_elem != null;
		assert browser != null;
		
		WebElement submit_element = null;

		boolean submit_elem_found = false;
		List<WebElement> form_elements = getNestedElements(form_elem);

		Map<String, String> attributes = new HashMap<>();
		for(WebElement elem : form_elements){
			attributes = browser.extractAttributes(elem);
			for(String attribute : attributes.keySet()){
				if(attributes.get(attribute).contains("submit")){
					submit_elem_found = true;
					break;
				}
			}

			if(submit_elem_found){
				submit_element = elem;
				break;
			}
		}

		if(submit_element == null){
			return null;
		}
		
		//Map<String, String> css_map = Browser.loadCssProperties(submit_element);
		com.looksee.models.Element elem = new com.looksee.models.Element(submit_element.getText(), 
																		 generateXpath(submit_element, browser.getDriver(), attributes), 
																		 submit_element.getTagName(), 
																		 attributes, 
																		 new HashMap<>(), 
																		 submit_element.getAttribute("innerHTML"), 
																		 submit_element.getAttribute("outerHTML"));
		//String screenshot_url = UploadObjectSingleOperation.saveImageToS3ForUser(img, (new URL(browser.getDriver().getCurrentUrl())).getHost(), checksum, BrowserType.create(browser.getBrowserName()), user_id);
		//elem.setViewportScreenshotUrl(screenshot_url);
		elem = element_service.saveFormElement(elem);

		return elem;
	}
	

	public Map<String, Template> findTemplates(List<com.looksee.models.Element> element_list){
		//create a map for the various duplicate elements
		Map<String, Template> element_templates = new HashMap<>();
		List<com.looksee.models.Element> parents_only_element_list = new ArrayList<>();
		for(com.looksee.models.Element element : element_list) {
			if(!element.isLeaf()) {
				parents_only_element_list.add(element);
			}
		}

		//iterate over all elements in list
		
		Map<String, Boolean> identified_templates = new HashMap<String, Boolean>();
		for(int idx1 = 0; idx1 < parents_only_element_list.size()-1; idx1++){
			com.looksee.models.Element element1 = parents_only_element_list.get(idx1);
			boolean at_least_one_match = false;
			if(identified_templates.containsKey(element1.getKey()) ) {
				continue;
			}
			//for each element iterate over all elements in list
			for(int idx2 = idx1+1; idx2 < parents_only_element_list.size(); idx2++){
				com.looksee.models.Element element2 = parents_only_element_list.get(idx2);
				if(identified_templates.containsKey(element2.getKey()) || !element1.getName().equals(element2.getName())){
					continue;
				}
				//get largest string length
				int max_length = element1.getTemplate().length();
				if(element2.getTemplate().length() > max_length){
					max_length = element2.getTemplate().length();
				}
				
				if(max_length == 0) {
					log.warn("max length of 0 between both templates");
					continue;
				}
				
				if(element1.getTemplate().equals(element2.getTemplate())){
					String template_str = element2.getTemplate();
					if(!element_templates.containsKey(template_str)){
						element_templates.put(template_str, new Template(TemplateType.UNKNOWN, template_str));
					}
					element_templates.get(template_str).getElements().add(element2);
					identified_templates.put(element2.getKey(), Boolean.TRUE);
					at_least_one_match = true;
					continue;
				}

				log.warn("getting levenshtein distance...");
				//double distance = StringUtils.getJaroWinklerDistance(element_list.get(idx1).getTemplate(), element_list.get(idx2).getTemplate());
				//calculate distance between loop1 value and loop2 value
				double distance = StringUtils.getLevenshteinDistance(element1.getTemplate(), element2.getTemplate());
				//if value is within threshold then add loop2 value to map for loop1 value xpath
				double avg_string_size = ((element1.getTemplate().length() + element2.getTemplate().length())/2.0);
				double similarity = distance / avg_string_size;
				//double sigmoid = new Sigmoid(0,1).value(similarity);

				//calculate distance of children if within 20%
				if(distance == 0.0 || similarity < 0.025){
					log.warn("Distance ;  Similarity :: "+distance + "  ;  "+similarity);
					String template_str = element1.getTemplate();
					if(!element_templates.containsKey(template_str)){
						element_templates.put(template_str, new Template(TemplateType.UNKNOWN, template_str));
					}
					element_templates.get(template_str).getElements().add(element2);
					identified_templates.put(element2.getKey(), Boolean.TRUE);

					at_least_one_match = true;
				}
			}
			if(at_least_one_match){
				String template_str = element1.getTemplate();
				element_templates.get(template_str).getElements().add(element1);
				identified_templates.put(element1.getKey(), Boolean.TRUE);
			}
			log.warn("****************************************************************");

		}

		return element_templates;
	}

	/**
	 * Checks if Attributes contains keywords indicative of a slider 
	 * @param attributes
	 * 
	 * @return true if any of keywords present, otherwise false
	 * 
	 * @pre attributes != null
	 * @pre !attributes.isEmpty()
	 */
	public static boolean doesAttributesContainSliderKeywords(Map<String, List<String>> attributes) {
		assert attributes != null;
		assert !attributes.isEmpty();
		for(String attr : attributes.keySet()) {
			if(attributes.get(attr).contains("slide")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Extracts template for element by using outer html and removing inner text
	 * @param element {@link Element}
	 * @return templated version of element html
	 */
	public static String extractTemplate(String outerHtml){
		assert outerHtml != null;
		assert !outerHtml.isEmpty();
		
		Document html_doc = Jsoup.parseBodyFragment(outerHtml);

		Cleaner cleaner = new Cleaner(Whitelist.relaxed());
		html_doc = cleaner.clean(html_doc);
		
		html_doc.select("script").remove()
				.select("link").remove()
				.select("style").remove();

		for(Element element : html_doc.getAllElements()) {
			element.removeAttr("id");
			element.removeAttr("name");
			element.removeAttr("style");
		}
		
		return html_doc.html();
	}
	
	

	public Map<String, Template> reduceTemplatesToParents(Map<String, Template> list_elements_list) {
		Map<String, Template> element_map = new HashMap<>();
		List<Template> template_list = new ArrayList<>(list_elements_list.values());
		//check if element is a child of another element in the list. if yes then don't add it to the list
		for(int idx1=0; idx1 < template_list.size(); idx1++){
			boolean is_child = false;
			for(int idx2=0; idx2 < template_list.size(); idx2++){
				if(idx1 != idx2 && template_list.get(idx2).getTemplate().contains(template_list.get(idx1).getTemplate())){
					is_child = true;
					break;
				}
			}

			if(!is_child){
				element_map.put(template_list.get(idx1).getTemplate(), template_list.get(idx1));
			}
		}

		//remove duplicates
		log.warn("total elements left after reduction :: " + element_map.values().size());
		return element_map;
	}

	/**
	 *
	 * Atom - A leaf element or an element that contains only 1 leaf element regardless of depth
	 * Molecule - Contains at least 2 atoms and cannot contain any molecules
	 * Organism - Contains at least 2 molecules or at least 1 molecule and 1 atom or at least 1 organism, Must not be an immediate child of body
	 * Template - An Immediate child of the body tag or the descendant such that the element is the first to have sibling elements
	 *
	 * @param template
	 * @return
	 */
	public TemplateType classifyTemplate(String template){
		Document html_doc = Jsoup.parseBodyFragment(template);
		Element root_element = html_doc.body();

		return classifyUsingChildren(root_element);
	}

	private TemplateType classifyUsingChildren(Element root_element) {
		assert root_element != null;

		int atom_cnt = 0;
		int molecule_cnt = 0;
		int organism_cnt = 0;
		int template_cnt = 0;
		if(root_element.children() == null || root_element.children().isEmpty()){
			return TemplateType.ATOM;
		}

		//categorize each eleemnt
		for(Element element : root_element.children()){
			TemplateType type = classifyUsingChildren(element);
			if(type == TemplateType.ATOM){
				atom_cnt++;
			}
			else if(type == TemplateType.MOLECULE){
				molecule_cnt++;
			}
			else if(type == TemplateType.ORGANISM){
				organism_cnt++;
			}
			else if(type == TemplateType.TEMPLATE){
				template_cnt++;
			}
		}

		if(atom_cnt == 1){
			return TemplateType.ATOM;
		}
		else if(atom_cnt > 1 && molecule_cnt == 0 && organism_cnt == 0 && template_cnt == 0){
			return TemplateType.MOLECULE;
		}
		else if( (molecule_cnt == 1 && atom_cnt > 0 || molecule_cnt > 1 || organism_cnt > 0) && template_cnt == 0){
			return TemplateType.ORGANISM;
		}
		else if(isTopLevelElement()){
			return TemplateType.TEMPLATE;
		}
		return TemplateType.UNKNOWN;

	}

	private boolean isTopLevelElement() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static boolean testContainsElement(List<String> keys) {
		for(String key : keys) {
			if(key.contains("elementstate")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param src
	 * @return
	 */
	public List<String> extractAllUniqueElementXpaths(String src) {
		assert src != null;
		
		Map<String, String> frontier = new HashMap<>();
		List<String> xpaths = new ArrayList<>();
		String body_src = extractBody(src);
		
		Document html_doc = Jsoup.parse(body_src);
		frontier.put("//body","");
		while(!frontier.isEmpty()) {
			String next_xpath = frontier.keySet().iterator().next();
			frontier.remove(next_xpath);
			xpaths.add(next_xpath);
			
			Elements elements = Xsoup.compile(next_xpath).evaluate(html_doc).getElements();
			if(elements.size() == 0) {
				log.warn("NO ELEMENTS WITH XPATH FOUND :: "+next_xpath);
				continue;
			}
			Element element = elements.first();
			List<Element> children = new ArrayList<Element>(element.children());
			Map<String, Integer> xpath_cnt = new HashMap<>();
			
			for(Element child : children) {
				if(isStructureTag(child.tagName())) {
					continue;
				}
				String xpath = next_xpath + "/" + child.tagName();
				
				if(xpath_cnt.containsKey(child.tagName()) ) {
					xpath_cnt.put(child.tagName(), xpath_cnt.get(child.tagName())+1);
				}
				else {
					xpath_cnt.put(child.tagName(), 1);
				}
				
				xpath = xpath + "["+xpath_cnt.get(child.tagName())+"]";

				frontier.put(xpath, "");
			}
		}	
		
		return xpaths;
	}

	public static String extractBody(String src) {
		String patternString = "<body[^\\>]*>([\\s\\S]*)<\\/body>";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(src);
        if(matcher.find()) {
        	return matcher.group();
        }
        return null;
	}

	public static Set<String> extractMetadata(String src) {
		Document html_doc = Jsoup.parse(src);
		Elements meta_tags = html_doc.getElementsByTag("meta");
		Set<String> meta_tag_html = new HashSet<String>();
		
		for(Element meta_tag : meta_tags) {
			meta_tag_html.add(meta_tag.outerHtml());
		}
		return meta_tag_html;
	}

	public static Set<String> extractStylesheets(String src) {
		Document html_doc = Jsoup.parse(src);
		Elements link_tags = html_doc.getElementsByTag("link");
		Set<String> stylesheet_urls = new HashSet<String>();
		
		for(Element link_tag : link_tags) {
			stylesheet_urls.add(link_tag.absUrl("href"));
		}
		return stylesheet_urls;
	}

	public static Set<String> extractScriptUrls(String src) {
		Document html_doc = Jsoup.parse(src);
		Elements script_tags = html_doc.getElementsByTag("script");
		Set<String> script_urls = new HashSet<String>();
		
		for(Element script_tag : script_tags) {
			String src_url = script_tag.absUrl("src");
			if(src_url != null && !src_url.isEmpty()) {
				script_urls.add(script_tag.absUrl("src"));
			}
		}
		return script_urls;
	}

	public static Set<String> extractIconLinks(String src) {
		Document html_doc = Jsoup.parse(src);
		Elements icon_tags = html_doc.getElementsByTag("link");
		Set<String> icon_urls = new HashSet<String>();
		
		for(Element icon_tag : icon_tags) {
			if(icon_tag.attr("rel").contains("icon")){
				icon_urls.add(icon_tag.absUrl("href"));
			}
		}
		return icon_urls;
	}

	public String getPageSource(Browser browser, URL sanitized_url) throws MalformedURLException {
		assert browser != null;
		assert sanitized_url != null;
		
		return browser.getSource();
	}
	
	public List<com.looksee.models.Element> extractElements(String page_src, URL url, List<RuleSet> rule_sets) throws IOException, XPathExpressionException {
		return getDomElements(page_src, url, rule_sets);
	}
	
	/**
	 * 
	 * @param page_source
	 * @param url
	 * @param rule_sets TODO
	 * @param reviewed_xpaths
	 * @return
	 * @throws IOException
	 * @throws XPathExpressionException 
	 */
	private synchronized List<com.looksee.models.Element> getDomElements(String page_source, URL url, List<RuleSet> rule_sets) throws IOException, XPathExpressionException {
		assert page_source != null;
		assert !page_source.isEmpty();
		assert url != null;
		assert rule_sets != null;
		
		List<com.looksee.models.Element> visited_elements = new ArrayList<>();
		Map<String, String> frontier = new HashMap<>();
		Map<String, Integer> xpath_cnt = new HashMap<>();
		
		//get html doc and get root element
		Document html_doc = Jsoup.parse(page_source);
		Element root = html_doc.getElementsByTag("body").get(0);
			
		//create element state from root node
		Map<String, String> attributes = generateAttributesMapUsingJsoup(root);
		log.warn("page source 1 :: "+page_source.length());
		log.warn("url 1  :: "+url);
		Map<String, String> css_props = new HashMap<>();
		try{
			css_props.putAll(Browser.loadCssPrerenderedPropertiesUsingParser(rule_sets, root));
		}
		catch(Exception e) {
			log.warn(e.getMessage());
		}
		
		com.looksee.models.Element root_element = buildElement("//body", attributes, root, ElementClassification.ANCESTOR, css_props );
		root_element = element_service.save(root_element);

		//put element on frontier
		frontier.put("//body",root_element.getKey());
		while(!frontier.isEmpty()) {
			String next_xpath = frontier.keySet().iterator().next();
			String parent_element_key = frontier.remove(next_xpath);
			//ElementState root_element = frontier.remove(next_xpath);
			//visited_elements.add(root_element);
			//get Element by xpath
			
			Elements elements = Xsoup.compile(next_xpath).evaluate(html_doc).getElements();
			if(elements.size() == 0) {
				log.warn("NO ELEMENTS WITH XPATH FOUND :: "+next_xpath + "   :     url :   "  + url.toString());
			}
			Element element = elements.first();
			//get child elements for element
			attributes = generateAttributesMapUsingJsoup(element);
			
			Map<String, String> pre_render_css_props = new HashMap<>();
			
			try{
				pre_render_css_props.putAll(Browser.loadCssPrerenderedPropertiesUsingParser(rule_sets, element));
			}
			catch(Exception e) {
				log.warn(e.getMessage());
			}
			
			ElementClassification classification = null;
			List<Element> children = new ArrayList<Element>(element.children());
			if(children.isEmpty()) {
				classification = ElementClassification.LEAF;
			}
			else if(isSliderElement(element)) {
				classification = ElementClassification.SLIDER;
			}
			else {
				classification = ElementClassification.ANCESTOR;
			}
			
			com.looksee.models.Element element_state = buildElement(next_xpath, attributes, element, classification, pre_render_css_props);
			
			element_state = element_service.save(element_state);
			visited_elements.add(element_state);
			element_service.addChildElement(parent_element_key, element_state.getKey());
			
			
			for(Element child : children) {
				if(isStructureTag(child.tagName())) {
					continue;
				}
				String xpath = next_xpath + "/" + child.tagName();
				
				if(xpath_cnt.containsKey(xpath)) {
					xpath_cnt.put(xpath, xpath_cnt.get(xpath)+1);
				}
				else {
					xpath_cnt.put(xpath, 1);
				}
				
				xpath = xpath + "["+xpath_cnt.get(xpath)+"]";

				frontier.put(xpath, element_state.getKey());
			}
		}
		return visited_elements;
	}
	
	
	private static String calculateSha256(String value) {
		return org.apache.commons.codec.digest.DigestUtils.sha256Hex(value);
	}
}


@ResponseStatus(HttpStatus.SEE_OTHER)
class ServiceUnavailableException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 794045239226319408L;

	public ServiceUnavailableException(String msg) {
		super(msg);
	}
}

@ResponseStatus(HttpStatus.SEE_OTHER)
class FiveZeroThreeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 452417401491490882L;

	public FiveZeroThreeException(String msg) {
		super(msg);
	}
}
