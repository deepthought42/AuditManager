package com.looksee.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ImageSearchAnnotation extends LookseeObject{
	private float score;
	private Set<String> best_guess_label;
	private Set<String> full_matching_images;
	private Set<String> similar_images;
	
	public ImageSearchAnnotation() {
		setScore(0.0F);
		setBestGuessLabel(new HashSet<>());
		setFullMatchingImages(new HashSet<>());
		setSimilarImages(new HashSet<>());
	}
	
	public ImageSearchAnnotation(Set<String> best_guess_label,
								 Set<String> full_matching_images,
								 Set<String> similar_images
	) {
		setScore(score);
		setBestGuessLabel(best_guess_label);
		setFullMatchingImages(full_matching_images);
		setSimilarImages(similar_images);
	}
	
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public Set<String> getBestGuessLabel() {
		return best_guess_label;
	}
	public void setBestGuessLabel(Set<String> best_guess_label) {
		this.best_guess_label = best_guess_label;
	}
	public Set<String> getFullMatchingImages() {
		return full_matching_images;
	}
	public void setFullMatchingImages(Set<String> full_matching_images) {
		this.full_matching_images = full_matching_images;
	}
	public Set<String> getSimilarImages() {
		return similar_images;
	}
	public void setSimilarImages(Set<String> similar_images) {
		this.similar_images = similar_images;
	}

	@Override
	public String generateKey() {
		return "imagesearchannotation::"+UUID.randomUUID();
	}
}
