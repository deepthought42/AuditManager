package com.looksee.models.audit;

public class AuditScore {
	private double content_score;
	private double readability;
	private double spelling_grammar;
	private double image_quality;
	private double alt_text;

	private double information_architecture_score;
	private double links;
	private double metadata;
	private double seo;
	private double security;
	
	private double aesthetics_score;
	private double color_contrast;
	private double whitespace;
	
	private double interactivity_score;
	private double accessibility_score;
	
	private double text_contrast;
	private double non_text_contrast;
	
	public AuditScore(double content_score,
					  double readability,
					  double spelling_grammar,
					  double image_quality,
					  double alt_text, 
					  double information_architecture_score, 
					  double links, 
					  double metadata, 
					  double seo, 
					  double security, 
					  double aesthetic_score, 
					  double color_contrast, 
					  double whitespace, 
					  double interactivity_score, 
					  double accessibility_score, 
					  double text_contrast, 
					  double non_text_contrast) {
		setContentScore(content_score);
		setReadability(readability);
		setSpellingGrammar(spelling_grammar);
		setImageQuality(image_quality);
		setAltText(alt_text);
		
		setInformationArchitectureScore(information_architecture_score);
		setLinks(links);
		setMetadata(metadata);
		setSEO(seo);
		setSecurity(security);
		
		setAestheticsScore(aesthetic_score);
		setColorContrast(color_contrast);
		setWhitespace(whitespace);
		
		setInteractivityScore(interactivity_score);
		setAccessibilityScore(accessibility_score);
		
		setTextContrastScore(text_contrast);
		setNonTextContrastScore(non_text_contrast);
	}
	
	
	public double getContentScore() {
		return content_score;
	}
	
	public void setContentScore(double content_score) {
		this.content_score = content_score;
	}

	public double getInformationArchitectureScore() {
		return information_architecture_score;
	}

	public void setInformationArchitectureScore(double information_architecture_score) {
		this.information_architecture_score = information_architecture_score;
	}

	public double getAestheticsScore() {
		return aesthetics_score;
	}

	public void setAestheticsScore(double aesthetics_score) {
		this.aesthetics_score = aesthetics_score;
	}

	public double getInteractivityScore() {
		return interactivity_score;
	}

	public void setInteractivityScore(double interactivity_score) {
		this.interactivity_score = interactivity_score;
	}

	public double getAccessibilityScore() {
		return accessibility_score;
	}

	public void setAccessibilityScore(double accessibility_score) {
		this.accessibility_score = accessibility_score;
	}


	public double getReadability() {
		return readability;
	}


	public void setReadability(double readability) {
		this.readability = readability;
	}


	public double getSpellingGrammar() {
		return spelling_grammar;
	}


	public void setSpellingGrammar(double spelling_grammar) {
		this.spelling_grammar = spelling_grammar;
	}

	public double getImageQuality() {
		return image_quality;
	}


	public void setImageQuality(double image_quality) {
		this.image_quality = image_quality;
	}


	public double getAltText() {
		return alt_text;
	}


	public void setAltText(double alt_text) {
		this.alt_text = alt_text;
	}


	public double getLinks() {
		return links;
	}


	public void setLinks(double links) {
		this.links = links;
	}


	public double getMetadata() {
		return metadata;
	}


	public void setMetadata(double metadata) {
		this.metadata = metadata;
	}


	public double getSEO() {
		return seo;
	}


	public void setSEO(double seo) {
		this.seo = seo;
	}


	public double getSecurity() {
		return security;
	}


	public void setSecurity(double security) {
		this.security = security;
	}


	public double getColorContrast() {
		return color_contrast;
	}


	public void setColorContrast(double color_contrast) {
		this.color_contrast = color_contrast;
	}


	public double getWhitespace() {
		return whitespace;
	}


	public void setWhitespace(double whitespace) {
		this.whitespace = whitespace;
	}


	public double getTextContrastScore() {
		return text_contrast;
	}


	public void setTextContrastScore(double text_contrast) {
		this.text_contrast = text_contrast;
	}


	public double getNonTextContrastScore() {
		return non_text_contrast;
	}


	public void setNonTextContrastScore(double non_text_contrast) {
		this.non_text_contrast = non_text_contrast;
	}
}
