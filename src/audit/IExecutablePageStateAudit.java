package com.looksee.models.audit;

import com.looksee.models.PageState;
import com.looksee.models.designsystem.DesignSystem;

public interface IExecutablePageStateAudit {
	/**
	 * Executes audit on {@link PageState page}
	 * 
	 * @param page_state
	 * @param audit_record TODO
	 * @param design_system TODO
	 * @return
	 */
	public Audit execute(PageState page_state, AuditRecord audit_record, DesignSystem design_system);
}
