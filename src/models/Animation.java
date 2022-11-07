package com.looksee.models;

import java.util.Collections;
import java.util.List;

import com.looksee.models.enums.AnimationType;

public class Animation extends LookseeObject {

	private List<String> image_urls;
	private List<String> image_checksums;
	private AnimationType animation_type;
	
	public Animation(){}

	/**
	 * 
	 * @param image_urls 
	 * 
	 * @pre image_urls != null
	 */
	public Animation(List<String> image_urls, List<String> image_checksums, AnimationType type) {
		assert image_urls != null;
		setImageUrls(image_urls);
		setImageChecksums(image_checksums);
		setAnimationType(type);
		setKey(generateKey());
	}

	@Override
	public String generateKey() {
		String key = "";
		Collections.sort(image_urls);
		for(String url : image_urls){
			key += url;
		}
		
		return "animation:"+org.apache.commons.codec.digest.DigestUtils.sha256Hex(key);
	}

	public List<String> getImageChecksums() {
		return image_checksums;
	}

	public void setImageChecksums(List<String> image_checksums) {
		this.image_checksums = image_checksums;
	}

	public AnimationType getAnimationType() {
		return animation_type;
	}

	public void setAnimationType(AnimationType animation_type) {
		this.animation_type = animation_type;
	}

	public List<String> getImageUrls() {
		return image_urls;
	}

	public void setImageUrls(List<String> image_urls) {
		this.image_urls = image_urls;
	}
}
