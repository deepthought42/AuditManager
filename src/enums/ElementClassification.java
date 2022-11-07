package com.looksee.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ElementClassification {
	TEMPLATE("template"), LEAF("leaf"), SLIDER("slider"), ANCESTOR("ancestor");
	
	private String shortName;

	ElementClassification(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return shortName;
    }

    @JsonCreator
    public static ElementClassification create(String value) {
        if(value == null) {
            throw new IllegalArgumentException();
        }
        for(ElementClassification v : values()) {
            if(value.equals(v.getShortName())) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }

    public String getShortName() {
        return shortName;
    }
}
