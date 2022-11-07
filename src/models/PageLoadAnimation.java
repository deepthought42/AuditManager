package com.looksee.models;

import java.util.List;


/**
 * 
 */
public class PageLoadAnimation extends LookseeObject {
	
	private List<String> image_urls;
	private List<String> image_checksums;
	private String page_url;
	
	public PageLoadAnimation(){}

	/**
	 * 
	 * @param image_urls 
	 * 
	 * @pre image_urls != null
	 */
	public PageLoadAnimation(List<String> image_urls, List<String> image_checksums, String page_url) {
		assert image_urls != null;
		setImageUrls(image_urls);
		setImageChecksums(image_checksums);
		setPageUrl(page_url);
		setKey(generateKey());
	}

	@Override
	public String generateKey() {
		return "pageloadanimation:"+getPageUrl();
	}

	public List<String> getImageChecksums() {
		return image_checksums;
	}

	public void setImageChecksums(List<String> image_checksums) {
		this.image_checksums = image_checksums;
	}

	public List<String> getImageUrls() {
		return image_urls;
	}

	public void setImageUrls(List<String> image_urls) {
		this.image_urls = image_urls;
	}

	public String getPageUrl() {
		return page_url;
	}

	public void setPageUrl(String page_url) {
		this.page_url = page_url;
	}
}
