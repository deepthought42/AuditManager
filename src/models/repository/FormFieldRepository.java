package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.looksee.browsing.form.FormField;

public interface FormFieldRepository extends Neo4jRepository<FormField, Long> {

}
