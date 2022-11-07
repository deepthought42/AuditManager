package com.looksee.models.audit;

import java.time.LocalDateTime;

public class SimpleScore {

	private LocalDateTime date_performed;
	private double score;
	
	public SimpleScore(LocalDateTime date_performed, double score) {
		setDatePerformed(date_performed);
		setScore(score);
	}
	
	public LocalDateTime getDatePerformed() {
		return date_performed;
	}
	public void setDatePerformed(LocalDateTime date_performed) {
		this.date_performed = date_performed;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
}
