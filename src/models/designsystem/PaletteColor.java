package com.looksee.models.designsystem;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.annotation.Properties;

/**
 * Contains data for individual palette primary colors and the shades, tints, and tones associated with them
 *
 */
public class PaletteColor {

	private String primary_color;
	private double primary_color_percent;
	
	private Map<String, String> tints_shades_tones = new HashMap<>();
	
	public PaletteColor() {}
	
	public PaletteColor(String primary_color, double primary_color_percent, Map<String, String> tints_shades_tones) {
		setPrimaryColor(primary_color.trim());
		setPrimaryColorPercent(primary_color_percent);
		addTintsShadesTones(tints_shades_tones);
	}

	public String getPrimaryColor() {
		return primary_color;
	}

	private void setPrimaryColor(String primary_color) {
		this.primary_color = primary_color;
	}

	public double getPrimaryColorPercent() {
		return primary_color_percent;
	}

	private void setPrimaryColorPercent(double primary_color_percent) {
		this.primary_color_percent = primary_color_percent;
	}

	public Map<String, String> getTintsShadesTones() {
		return tints_shades_tones;
	}

	public void addTintsShadesTones(Map<String, String> tints_shades_tones) {
		this.tints_shades_tones.putAll(tints_shades_tones);
	}
}
