package com.looksee.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.looksee.models.rules.RuleType;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class RuleValueRequiredException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7200878662560716216L;

	public RuleValueRequiredException(RuleType type) {
		super("The provided rule " + type + " requires a value.");
	}
}
