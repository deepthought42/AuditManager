package com.looksee.models.message;

public class JourneyExaminationProgressMessage extends Message{

	private int examinedJourneys;
	private int generatedJourneys;
	
	public JourneyExaminationProgressMessage(long accountId, 
											 long auditRecordId, 
											 long domainId, 
											 int examined_journeys,
											 int generated_journeys
	) {
		setAccountId(accountId);
		setAuditRecordId(auditRecordId);
		setDomainId(domainId);
		setExaminedJourneys(examined_journeys);
		setGeneratedJourneys(generated_journeys);
	}

	public int getExaminedJourneys() {
		return examinedJourneys;
	}

	public void setExaminedJourneys(int examinedJourneys) {
		this.examinedJourneys = examinedJourneys;
	}

	public int getGeneratedJourneys() {
		return generatedJourneys;
	}

	public void setGeneratedJourneys(int generatedJourneys) {
		this.generatedJourneys = generatedJourneys;
	}

}
