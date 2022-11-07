package com.looksee.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SubscriptionPlan {
	FREE("FREE"), 
	ENTERPRISE("ENTERPRISE"), 
	STARTUP("STARTUP"), 
	COMPANY_PRO("PRO"),
	COMPANY_PREMIUM("PREMIUM"), 
	AGENCY("AGENCY"), 
	AGENCY_PRO("AGENCY PRO"), 
	AGENCY_PREMIUM("AGENCY PREMIUM"),
	UNLIMITED("UNLIMITED");


    private final String short_name;

    /**
     * @param text
     */
    private SubscriptionPlan(final String name) {
        this.short_name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return short_name;
    }
    
    @JsonCreator
    public static SubscriptionPlan create (String value) {
        if(value == null) {
            throw new IllegalArgumentException();
        }
        for(SubscriptionPlan v : values()) {
            if(value.equalsIgnoreCase(v.getShortName())) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }
    
    public String getShortName() {
        return short_name;
    }
}
