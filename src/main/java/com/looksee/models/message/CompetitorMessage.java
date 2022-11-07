package com.looksee.models.message;

import com.looksee.models.competitiveanalysis.Competitor;
import com.looksee.models.competitiveanalysis.Message;

public class CompetitorMessage extends Message {

	private Competitor competitor;
	
	public CompetitorMessage(Competitor competitor) {
		super();
	}

	public CompetitorMessage(long competitor_id, long account_id, Competitor competitor) {
		super(competitor_id, account_id, -1);
		setCompetitor(competitor);
	}

	public Competitor getCompetitor() {
		return competitor;
	}

	public void setCompetitor(Competitor competitor) {
		this.competitor = competitor;
	}

}
