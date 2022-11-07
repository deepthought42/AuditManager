package com.looksee.models.audit.domain;

import java.util.List;

import com.looksee.models.Domain;
import com.looksee.models.audit.Audit;

public interface IExecutableDomainAudit {
	/**
	 * Executes audit using list of {@link Audit audits}
	 * 
	 * @param audits {@link List} of audits to be analyzed as a whole
	 * 
 	 * @return {@link Audit audit}
	 */
	public Audit execute(Domain domain);
}
