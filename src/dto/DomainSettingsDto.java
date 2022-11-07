package com.looksee.models.dto;

import java.util.Set;

import com.looksee.models.TestUser;
import com.looksee.models.designsystem.DesignSystem;

public class DomainSettingsDto {
	private DesignSystem design_system;
	private Set<TestUser> test_users;
	
	public DomainSettingsDto(DesignSystem design_system, Set<TestUser> test_users) {
		this.setDesignSystem(design_system);
		this.setTestUsers(test_users);
	}

	public DesignSystem getDesignSystem() {
		return design_system;
	}

	public void setDesignSystem(DesignSystem design_system) {
		this.design_system = design_system;
	}

	public Set<TestUser> getTestUsers() {
		return test_users;
	}

	public void setTestUsers(Set<TestUser> test_users) {
		this.test_users = test_users;
	}
}
