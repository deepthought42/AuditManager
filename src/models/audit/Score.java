package com.looksee.models.audit;

import java.util.Set;

/**
 * Represents score as a combination of a score achieved and max possible score. This object also contains a set of
 * {@link UXIssueMessage issues} that were experienced while generating score
 */
public class Score {

	private int points_achieved;
	private int max_possible_points;
	private Set<UXIssueMessage> messages;
	
	public Score(int points, int max_points, Set<UXIssueMessage> issue_messages) {
		setPointsAchieved(points);
		setMaxPossiblePoints(max_points);
		setIssueMessages(issue_messages);
	}
	
	public int getPointsAchieved() {
		return points_achieved;
	}
	public void setPointsAchieved(int points_achieved) {
		this.points_achieved = points_achieved;
	}
	public int getMaxPossiblePoints() {
		return max_possible_points;
	}
	public void setMaxPossiblePoints(int max_possible_points) {
		this.max_possible_points = max_possible_points;
	}

	public Set<UXIssueMessage> getIssueMessages() {
		return messages;
	}

	public void setIssueMessages(Set<UXIssueMessage> messages) {
		this.messages = messages;
	}
}
