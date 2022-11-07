package com.looksee.models.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.Element;
import com.looksee.models.ElementState;
import com.looksee.models.Form;
import com.looksee.models.PageState;

public interface FormRepository extends Neo4jRepository<Form, Long> {

	@Deprecated
	@Query("MATCH (account:Account)-[]->(d:Domain{url:$url}) MATCH (d)-[]->(p:Page) MATCH (p)-[]->(ps:PageState) MATCH (ps)-[]->(f:Form{key:$form_key}) Match form=(f)-[]->() WHERE id(account)=$account_id RETURN form LIMIT 1")
	public Form findByKeyForUserAndDomain(@Param("account_id") long account_id, @Param("url") String url, @Param("form_key") String form_key);
	
	@Query("MATCH (p:Page{url:$page_url})-[]->(ps:PageState) MATCH (ps)-[]->(f:Form{key:$form_key}) Match form=(f)-[]->() RETURN form LIMIT 1")
	public Form findByKey(@Param("page_url") String url, @Param("form_key") String form_key);
	
	@Query("MATCH (:Account{user_id:$user_id})-[]->(d:Domain{url:$url}) MATCH (d)-[]->(p:Page) MATCH (p)-[]->(ps:PageState) MATCH (ps)-[]->(ps:PageState) MATCH (ps)-[:HAS]->(:Form{key:$key}) RETURN ps")
	public PageState getPageState(@Param("user_id") String user_id, @Param("url") String url, @Param("key") String key);

	@Query("MATCH (:Account{user_id:$user_id})-[]->(d:Domain{url:$url}) MATCH (d)-[]->(p:Page) MATCH (p)-[]->(ps:PageState) MATCH (ps)-[]->(ps:PageState) MATCH (ps)-[]->(f:Form{key:$form_key}) Match (f)-[:HAS]->(e:ElementState) RETURN e")
	public List<ElementState> getElementStates(@Param("user_id") String user_id, @Param("url") String url, @Param("form_key") String form_key);
	
	@Query("MATCH (:Account{user_id:$user_id})-[]->(d:Domain{url:$url}) MATCH (d)-[]->(p:Page) MATCH (p)-[]->(ps:PageState) MATCH (ps)-[]->(f:Form{key:$form_key}) Match (f)-[:HAS_SUBMIT]->(e) RETURN e")
	public ElementState getSubmitElement(@Param("user_id") String user_id, @Param("url") String url, @Param("form_key") String form_key);
	
	@Query("MATCH (:Account{user_id:$user_id})-[]->(d:Domain{url:$url}) MATCH (d)-[]->(p:Page) MATCH (p)-[]->(ps:PageState) MATCH (ps)-[]->(f:Form{key:$form_key}) Match (f)-[:DEFINED_BY]->(e) RETURN e")
	public ElementState getFormElement(@Param("user_id") String user_id, @Param("url") String url, @Param("form_key") String form_key);

	@Query("MATCH (:Account{user_id:$user_id})-[]->(d:Domain) MATCH (d)-[]->(p:Page) MATCH (p)-[]->(ps:PageState) MATCH (ps)-[]->(f:Form{key:$form_key}) Match (f)-[hbm:HAS]->(b:BugMessage) DELETE hbm,b")
	public Form clearBugMessages(@Param("user_id") String user_id, @Param("form_key") String form_key);
}
