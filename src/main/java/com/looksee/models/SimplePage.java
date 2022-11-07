package com.looksee.models;

/**
 * A simplified data set for page consisting of full page and viewport screenshots, url and the height and width
 *  of the full page screenshot
 *
 */
public class SimplePage {
	private long id;
	private String url;
	private String screenshot_url;
	private String full_page_screenshot_url_onload;
	private String full_page_screenshot_url_composite;
	private long width;
	private long height;
	private String html_source;
	private String key;
	
	public SimplePage(
			String url, 
			String screenshot_url, 
			String full_page_screenshot_url, 
			String full_page_screenshot_composite_url, 
			long width, 
			long height, 
			String html_source,
			String page_state_key,
			long id
	) {
		setId(id);
		setUrl(url);
		setScreenshotUrl(screenshot_url);
		setFullPageScreenshotUrlOnload(full_page_screenshot_url);
		setFullPageScreenshotUrlComposite(full_page_screenshot_composite_url);
		setWidth(width);
		setHeight(height);
		setHtmlSource(html_source);
		setKey(page_state_key);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getScreenshotUrl() {
		return screenshot_url;
	}

	public void setScreenshotUrl(String screenshot_url) {
		this.screenshot_url = screenshot_url;
	}

	public String getFullPageScreenshotUrlOnload() {
		return full_page_screenshot_url_onload;
	}

	public void setFullPageScreenshotUrlOnload(String full_page_screenshot_url) {
		this.full_page_screenshot_url_onload = full_page_screenshot_url;
	}

	public String getFullPageScreenshotUrlComposite() {
		return full_page_screenshot_url_composite;
	}

	public void setFullPageScreenshotUrlComposite(String full_page_screenshot_url_composite) {
		this.full_page_screenshot_url_composite = full_page_screenshot_url_composite;
	}

	public long getWidth() {
		return width;
	}

	public void setWidth(long width) {
		this.width = width;
	}

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public String getHtmlSource() {
		return html_source;
	}

	public void setHtmlSource(String html_source) {
		this.html_source = html_source;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
