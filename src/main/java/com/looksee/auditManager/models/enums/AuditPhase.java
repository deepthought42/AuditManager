package com.looksee.auditManager.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines all phases of a website/web app {@link Audit audits} 
 */
public enum AuditPhase {
	PAGE_DATA_EXTRACTION("PAGE_DATA_EXTRACTION"),
	JOURNEY_DATA_EXTRACTION("JOURNEY_DATA_EXTRACTION"),
	START_AUDIT("INIT_AUDIT"),
	AUDIT_UPDATE("AUDIT_UPDATE"),
	AUDIT_COMPLETE("AUDIT_COMPLETE"),
	UNKNOWN("UNKNOWN");
	
	private String shortName;

    AuditPhase (String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return shortName;
    }

    @JsonCreator
    public static AuditPhase create (String value) {
        if(value == null) {
            return UNKNOWN;
        }
        
        for(AuditPhase v : values()) {
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
