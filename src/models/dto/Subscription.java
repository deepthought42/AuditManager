package com.looksee.models.dto;

public class Subscription {
	private String plan;
	private String price_id;
	
	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getPriceId() {
		return price_id;
	}

	public void setPriceId(String price_id) {
		this.price_id = price_id;
	}
}
