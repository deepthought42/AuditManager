package com.looksee.models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.looksee.models.enums.BrowserType;
import com.looksee.services.BrowserService;

/**
 * A reference to a web page
 *
 */
@NodeEntity
public class PageState extends LookseeObject {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PageState.class);

	private String src;
	private String url;
	private String urlAfterLoading;

	private boolean loginRequired;
	private boolean isSecure;

	private String viewportScreenshotUrl;
	private String fullPageScreenshotUrlOnload;
	private String fullPageScreenshotUrlComposite;
	private String browser;
	private boolean landable;
	private long scrollXOffset;
	private long scrollYOffset;
	private int viewportWidth;
	private int viewportHeight;
	private int fullPageWidth;
	private int fullPageHeight;
	private String pageName;

	private String title;
	private Set<String> script_urls;
	private Set<String> stylesheet_urls;
	private Set<String> metadata;	
	private Set<String> favicon_url;
	private Set<String> keywords;
	private int httpStatus;
	
	@Relationship(type = "HAS", direction = Relationship.INCOMING)
	private List<ElementState> elements;


	public PageState() {
		super();
		setElements(new ArrayList<>());
		setKeywords(new HashSet<>());
		setScriptUrls(new HashSet<>());
		setStylesheetUrls(new HashSet<>());
		setMetadata(new HashSet<>());
		setFaviconUrl(new HashSet<>());
	}
	
	/**
	 * 	 Constructor
	 * 
	 * @param screenshot_url
	 * @param elements
	 * @param src
	 * @param isLandable
	 * @param scroll_x_offset
	 * @param scroll_y_offset
	 * @param viewport_width
	 * @param viewport_height
	 * @param browser
	 * @param full_page_screenshot_url_onload
	 * @param full_page_width TODO
	 * @param full_page_height TODO
	 * @param url
	 * @param title TODO
	 * @param is_secure TODO
	 * @param http_status_code TODO
	 * @param full_page_screenshot_url_composite TODO
	 * @param url_after_page_load TODO
	 * @throws MalformedURLException 
	 */
	public PageState(String screenshot_url, 
			List<ElementState> elements, 
			String src, 
			boolean isLandable, 
			long scroll_x_offset, 
			long scroll_y_offset,
			int viewport_width, 
			int viewport_height, 
			BrowserType browser, 
			String full_page_screenshot_url_onload,
			int full_page_width, 
			int full_page_height, 
			String url, 
			String title, 
			boolean is_secure, 
			int http_status_code, 
			String full_page_screenshot_url_composite, 
			String url_after_page_load
	) {
		assert screenshot_url != null;
		assert elements != null;
		assert src != null;
		assert browser != null;
		assert full_page_screenshot_url_onload != null;
		assert url != null;
		assert !url.isEmpty();
		
		setViewportScreenshotUrl(screenshot_url);
		setViewportWidth(viewport_width);
		setViewportHeight(viewport_height);
		setBrowser(browser);
		setElements(elements);
		setLandable(isLandable);
		setSrc(src);
		setScrollXOffset(scroll_x_offset);
		setScrollYOffset(scroll_y_offset);
	    setLoginRequired(false);
		setFullPageScreenshotUrlOnload(full_page_screenshot_url_onload);
		setFullPageScreenshotUrlComposite(full_page_screenshot_url_composite);
		setFullPageWidth(full_page_width);
		setFullPageHeight(full_page_height);
		setUrl(url);
		setUrlAfterLoading(url_after_page_load);
		setTitle(title);
		setIsSecure(is_secure);
		setHttpStatus(http_status_code);
		
		setPageName( generatePageName(getUrl()) );
		setMetadata( BrowserService.extractMetadata(src) );
		setStylesheetUrls( BrowserService.extractStylesheets(src));
		setScriptUrls( BrowserService.extractScriptUrls(src));
		setFaviconUrl(BrowserService.extractIconLinks(src));
		setKeywords(new HashSet<>());
		setKey(generateKey());
	}

	/**
	 * Gets counts for all tags based on {@link Element}s passed
	 *
	 * @param page_elements
	 *            list of {@link Element}s
	 *
	 * @return Hash of counts for all tag names in list of {@ElementState}s
	 *         passed
	 */
	public Map<String, Integer> countTags(Set<Element> tags) {
		Map<String, Integer> elem_cnts = new HashMap<String, Integer>();
		for (Element tag : tags) {
			if (elem_cnts.containsKey(tag.getName())) {
				int cnt = elem_cnts.get(tag.getName());
				cnt += 1;
				elem_cnts.put(tag.getName(), cnt);
			} else {
				elem_cnts.put(tag.getName(), 1);
			}
		}
		return elem_cnts;
	}

	/**
	 * Compares two images pixel by pixel.
	 *
	 * @param imgA
	 *            the first image.
	 * @param imgB
	 *            the second image.
	 * @return whether the images are both the same or not.
	 */
	public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
		// The images must be the same size.
		if (imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
			int width = imgA.getWidth();
			int height = imgA.getHeight();

			// Loop over every pixel.
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// Compare the pixels for equality.
					if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
						return false;
					}
				}
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Checks if Pages are equal
	 *
	 * @param page
	 *            the {@link PageVersion} object to compare current page to
	 *
	 * @pre page != null
	 * @return boolean value
	 *
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PageState))
			return false;

		PageState that = (PageState) o;
		
		return this.getKey().equals(that.getKey());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageState clone() {
		List<ElementState> elements = new ArrayList<ElementState>(getElements());
		return new PageState(getViewportScreenshotUrl(), 
							 elements, 
							 getSrc(), 
							 isLandable(), 
							 getScrollXOffset(), 
							 getScrollYOffset(), 
							 getViewportWidth(), 
							 getViewportHeight(), 
							 getBrowser(), 
							 getFullPageScreenshotUrlOnload(), 
							 getFullPageWidth(), 
							 getFullPageHeight(), 
							 getUrl(),
							 getTitle(),
							 isSecure(),
							 getHttpStatus(),
							 getFullPageScreenshotUrlComposite(),
							 getUrlAfterLoading());
	}

	@JsonIgnore
	public List<ElementState> getElements() {
		return this.elements;
	}

	@JsonIgnore
	public void setElements(List<ElementState> elements) {
		this.elements = elements;
	}

	public void setLandable(boolean isLandable) {
		this.landable = isLandable;
	}

	public boolean isLandable() {
		return this.landable;
	}

	public void addElement(ElementState element) {
		this.elements.add(element);
	}	

	/**
	 * Generates page name using path
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public String generatePageName(String url) {
		String name = "";

		try {
			String path = new URL(url).getPath().trim();
			path = path.replace("/", " ");
			path = path.trim();
			if("/".equals(path) || path.isEmpty()){
				path = "home";
			}
			name += path;
			
			return name.trim();
		} catch(MalformedURLException e){}
		
		return url;
	}
	
	/**
	 * 
	 * @param buff_img
	 * @return
	 * @throws IOException
	 */
	public static String getFileChecksum(BufferedImage buff_img) throws IOException {
		assert buff_img != null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean foundWriter = ImageIO.write(buff_img, "png", baos);
		assert foundWriter; // Not sure about this... with jpg it may work but
							// other formats ?
		// Get file input stream for reading the file content
		byte[] data = baos.toByteArray();
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			sha.update(data);
			byte[] thedigest = sha.digest(data);
			return Hex.encodeHexString(thedigest);
		} catch (NoSuchAlgorithmException e) {
			log.error("Error generating checksum of buffered image");
		}
		return "";

	}
	
	/**
	 * {@inheritDoc}
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 *
	 * @pre page != null
	 */
	public String generateKey() {
		/*
		List<ElementState> elements = new ArrayList<>(this.getElements());
		Collections.sort(elements);
		String key = "";
		for(ElementState element : elements) {
			key += element.getKey();
		}
		*/
		return "pagestate" + org.apache.commons.codec.digest.DigestUtils.sha256Hex( this.getUrl() + BrowserService.generalizeSrc(BrowserService.extractBody(this.getSrc()) ));
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public long getScrollXOffset() {
		return scrollXOffset;
	}

	public void setScrollXOffset(long scrollXOffset) {
		this.scrollXOffset = scrollXOffset;
	}

	public long getScrollYOffset() {
		return scrollYOffset;
	}

	public void setScrollYOffset(long scrollYOffset) {
		this.scrollYOffset = scrollYOffset;
	}

	public String getViewportScreenshotUrl() {
		return viewportScreenshotUrl;
	}

	public void setViewportScreenshotUrl(String viewport_screenshot_url) {
		this.viewportScreenshotUrl = viewport_screenshot_url;
	}

	public BrowserType getBrowser() {
		return BrowserType.create(browser);
	}

	public void setBrowser(BrowserType browser) {
		this.browser = browser.toString();
	}


	public int getViewportWidth() {
		return viewportWidth;
	}

	public void setViewportWidth(int viewport_width) {
		this.viewportWidth = viewport_width;
	}

	public int getViewportHeight() {
		return viewportHeight;
	}

	public void setViewportHeight(int viewport_height) {
		this.viewportHeight = viewport_height;
	}

	public boolean isLoginRequired() {
		return loginRequired;
	}

	public void setLoginRequired(boolean login_required) {
		this.loginRequired = login_required;
	}
	
	public String getFullPageScreenshotUrlOnload() {
		return fullPageScreenshotUrlOnload;
	}

	public void setFullPageScreenshotUrlOnload(String full_page_screenshot_url) {
		this.fullPageScreenshotUrlOnload = full_page_screenshot_url;
	}

	public int getFullPageWidth() {
		return fullPageWidth;
	}
	
	public void setFullPageWidth(int full_page_width) {
		this.fullPageWidth = full_page_width;
	}
	
	public int getFullPageHeight() {
		return fullPageHeight;
	}

	public void setFullPageHeight(int full_page_height) {
		this.fullPageHeight = full_page_height;
	}

	public void addElements(List<ElementState> elements) {
		//check for duplicates before adding
		for(ElementState element : elements) {
			if(element != null && !this.elements.contains(element)) {				
				this.elements.add(element);
			}
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String page_name) {
		this.pageName = page_name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<String> getScriptUrls() {
		return script_urls;
	}

	public void setScriptUrls(Set<String> script_urls) {
		this.script_urls = script_urls;
	}

	public Set<String> getStylesheetUrls() {
		return stylesheet_urls;
	}

	public void setStylesheetUrls(Set<String> stylesheet_urls) {
		this.stylesheet_urls = stylesheet_urls;
	}

	public Set<String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Set<String> metadata) {
		this.metadata = metadata;
	}

	public Set<String> getFaviconUrl() {
		return favicon_url;
	}

	public void setFaviconUrl(Set<String> favicon_url) {
		this.favicon_url = favicon_url;
	}

	public boolean isSecure() {
		return isSecure;
	}

	public void setIsSecure(boolean is_secure) {
		this.isSecure = is_secure;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getFullPageScreenshotUrlComposite() {
		return fullPageScreenshotUrlComposite;
	}

	public void setFullPageScreenshotUrlComposite(String full_page_screenshot_url_composite) {
		this.fullPageScreenshotUrlComposite = full_page_screenshot_url_composite;
	}

	public void setHttpStatus(int http_status) {
		this.httpStatus = http_status;
	}

	public String getUrlAfterLoading() {
		return urlAfterLoading;
	}

	public void setUrlAfterLoading(String url_after_loading) {
		this.urlAfterLoading = url_after_loading;
	}
	
	@Override
	public String toString() {
		return "(page => { key = "+getKey()+"; url = "+getUrl();
	}
}
