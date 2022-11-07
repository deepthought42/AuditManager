package com.looksee.models.journeys;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Redirect extends Step {
	private String start_url;
	private List<String> urls;
	private List<String> image_checksums;
	private List<String> image_urls;
	
	public Redirect() throws MalformedURLException{
		setUrls(new ArrayList<String>());
		setKey(generateKey());
	}
	
	public Redirect(String start_url, List<String> urls) throws MalformedURLException{
		assert urls != null;
		assert !urls.isEmpty();
		assert start_url != null;
		assert !start_url.isEmpty();
		
		setStartUrl(start_url);
		setUrls(urls);
		setKey(generateKey());
	}

	@Override
	public String generateKey() {
		String url_string = "";
		for(String url : urls){
			url_string += url;
		}
		return "redirect"+org.apache.commons.codec.digest.DigestUtils.sha256Hex(url_string);
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) throws MalformedURLException {
		List<String> clean_urls = new ArrayList<>();
		for(String url : urls){
			clean_urls.add(url);
		}
		this.urls = clean_urls;
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
		List<String> deduped_list = new ArrayList<>();
		//remove sequential duplicates from list
		String last_url = "";
		for(String url : image_urls){
			if(!last_url.equals(url)){
				deduped_list.add(url);
				last_url = url;
			}
		}
		
		this.image_urls = deduped_list;
	}

	public String getStartUrl() {
		return start_url;
	}

	public void setStartUrl(String start_url) {
		int params_idx = start_url.indexOf("?");
		String new_url = start_url;
		if(params_idx > -1){
			new_url = start_url.substring(0, params_idx);
		}
		this.start_url = new_url;
	}

}
