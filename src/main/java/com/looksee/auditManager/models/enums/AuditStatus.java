package com.looksee.auditManager.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines all {@link AuditStatus status} of {@link Audit audits} that exist in the system
 */
public enum AuditStatus {
	STARTED("STARTED"),
	STOPPED("STOPPED"),
	COMPLETE("COMPLETE");
	
	private String shortName;

    AuditStatus (String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return shortName;
    }

    @JsonCreator
    public static AuditStatus create (String value) {
        for(AuditStatus v : values()) {
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
