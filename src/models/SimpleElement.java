package com.looksee.models;

public class SimpleElement {
	private String key;
	private String screenshot_url;
	private int x_location;
	private int y_location;
	private int width;
	private int height;
	private String text;
	private String css_selector;
	private boolean image_flagged;
	private boolean adult_content;
	
	public SimpleElement(String key, 
						 String screenshot_url, 
						 int x, 
						 int y, 
						 int width, 
						 int height, 
						 String css_selector, 
						 String text, 
						 boolean is_image_flagged, 
						 boolean is_adult_content) {
		setKey(key);
		setScreenshotUrl(screenshot_url);
		setXLocation(x);
		setYLocation(y);
		setWidth(width);
		setHeight(height);
		setCssSelector(css_selector);
		setText(text);
		setImageFlagged(is_image_flagged);
		setAdultContent(is_adult_content);
	}
	
	public String getScreenshotUrl() {
		return screenshot_url;
	}
	public void setScreenshotUrl(String screenshot_url) {
		this.screenshot_url = screenshot_url;
	}
	
	public int getXLocation() {
		return x_location;
	}
	public void setXLocation(int x_location) {
		this.x_location = x_location;
	}
	
	public int getYLocation() {
		return y_location;
	}
	public void setYLocation(int y_location) {
		this.y_location = y_location;
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCssSelector() {
		return css_selector;
	}

	public void setCssSelector(String css_selector) {
		this.css_selector = css_selector;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isImageFlagged() {
		return image_flagged;
	}

	public void setImageFlagged(boolean is_image_flagged) {
		this.image_flagged = is_image_flagged;
	}

	public boolean isAdultContent() {
		return adult_content;
	}

	public void setAdultContent(boolean adult_content) {
		this.adult_content = adult_content;
	}	
}
