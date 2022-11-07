package com.looksee.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines all types of {@link Audit audits} that exist in the system
 */
public enum AuditName {
	//color management
	COLOR_PALETTE("Color Palette"),
	TEXT_BACKGROUND_CONTRAST("Text Background Contrast"),
	NON_TEXT_BACKGROUND_CONTRAST("Non Text Background Contrast"),
	LINKS("Links"),
	TYPEFACES("Typefaces"),
	FONT("Font"),
	PADDING("Padding"),
	MARGIN("Margin"),
	MEASURE_UNITS("Measure Units"),
	TITLES("Titles"),
	ALT_TEXT("Alt Text"),
	PARAGRAPHING("Paragraphing"),
	METADATA("Metadata"),
	UNKNOWN("Unknown"), 
	IMAGE_COPYRIGHT("Image Copyright"), 
	IMAGE_POLICY("Image Policy"), 
	READING_COMPLEXITY("Reading Complexity");
	
	private String shortName;

    AuditName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return shortName;
    }

    @JsonCreator
    public static AuditName create(String value) {
        if(value == null) {
            return UNKNOWN;
        }
        for(AuditName v : values()) {
            if(value.equalsIgnoreCase(v.getShortName())) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }

    public String getShortName() {
        return shortName;
    }
}
