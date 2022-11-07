package com.looksee.models.designsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.neo4j.ogm.annotation.NodeEntity;

import com.looksee.models.LookseeObject;
import com.looksee.models.enums.AudienceProficiency;
import com.looksee.models.enums.WCAGComplianceLevel;

/**
 * Defines a design system for use in defining and evaluating standards based on 
 * the settings withing the design system
 */
@NodeEntity
public class DesignSystem extends LookseeObject{

	private String wcag_compliance_level;
	private String audience_proficiency;
	
	private List<String> allowed_image_characteristics;
	private List<String> color_palette;

	public DesignSystem() {
		wcag_compliance_level = WCAGComplianceLevel.AAA.toString();
		audience_proficiency = AudienceProficiency.GENERAL.toString();
		allowed_image_characteristics = new ArrayList<String>();
		color_palette = new ArrayList<>();
	}
	
	public WCAGComplianceLevel getWcagComplianceLevel() {
		return WCAGComplianceLevel.create(wcag_compliance_level);
	}

	public void setWcagComplianceLevel(WCAGComplianceLevel wcag_compliance_level) {
		this.wcag_compliance_level = wcag_compliance_level.toString();
	}

	public AudienceProficiency getAudienceProficiency() {
		return AudienceProficiency.create(audience_proficiency);
	}

	/**
	 * sets the reading and topic proficiency level 
	 * 
	 * @param audience_proficiency {@link AudienceProficiency} string value
	 */
	public void setAudienceProficiency(AudienceProficiency audience_proficiency) {
		this.audience_proficiency = audience_proficiency.toString();
	}
	
	@Override
	public String generateKey() {
		return "designsystem"+UUID.randomUUID();
	}

	public List<String> getAllowedImageCharacteristics() {
		return allowed_image_characteristics;
	}

	public void setAllowedImageCharacteristics(List<String> allowed_image_characteristics) {
		this.allowed_image_characteristics = allowed_image_characteristics;
	}

	public List<String> getColorPalette() {
		return color_palette;
	}

	public void setColorPalette(List<String> color_palette) {
		this.color_palette = color_palette;
	}
	
	public boolean addColor(String color){
		if(!getColorPalette().contains(color)) {
			return getColorPalette().add(color);
		}
		
		return true;	
	}

	public boolean removeColor(String color) {
		return getColorPalette().remove(color);
	}
}
