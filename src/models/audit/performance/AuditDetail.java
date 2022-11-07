package com.looksee.models.audit.performance;

import java.util.Date;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * 
 */
@NodeEntity
public abstract class AuditDetail {

	@GeneratedValue
    @Id
	private Long id;
	private Date created_date = new Date();
	
	public Long getId() {
		return this.id;
	}

	public Date getCreatedDate() {
		return created_date;
	}

	public void setCreatedDate(Date created_date) {
		this.created_date = created_date;
	}
}
