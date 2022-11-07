package com.looksee.models.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.Group;
import com.looksee.models.LookseeObject;
import com.looksee.models.PageState;
import com.looksee.models.Test;
import com.looksee.models.TestRecord;

/**
 *
 */
public interface TestRepository extends Neo4jRepository<Test, Long> {
	public Test findByName(@Param("name") String name);
	
	@Query("MATCH (acct:Account)-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test{key:$key}) MATCH a=(t)-[r:HAS_PATH_OBJECT]->(p) WHERE id(acct)=$account_id return a")
	public Test findByKey(@Param("key") String key, @Param("url") String url, @Param("account_id") long account_id);

	@Query("MATCH (a:Account{user_id: $user_id})-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test{key:$key}) MATCH (t)-[r:HAS_TEST_RECORD]->(tr:TestRecord) RETURN tr ORDER BY tr.ran_at DESC LIMIT 1")
	public TestRecord getMostRecentRecord(@Param("key") String key, @Param("url") String url, @Param("user_id") String user_id);

	@Query("MATCH (a:Account{user_id:$user_id})-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test{key:$key}) MATCH (t)-[r:HAS_PATH_OBJECT]->(p) OPTIONAL MATCH b=(p)-[:HAS]->() WHERE id(a)=$account_id RETURN p,b")
	public List<LookseeObject> getPathObjects(@Param("key") String key, @Param("url") String url, @Param("account_id") long account_id);
	
	@Query("MATCH (a:Account{user_id: $user_id})-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test{key:$key}) MATCH (t)-[r:HAS_TEST_RECORD]->(tr:TestRecord) MATCH ps=(tr)-[:HAS_PAGE_STATE]->(p) RETURN ps ORDER BY tr.ran_at DESC")
	public List<TestRecord> findAllTestRecords(@Param("key") String key, @Param("url") String url, @Param("user_id") String user_id);

	@Query("MATCH (t:Test)-[r:HAS_PATH_OBJECT]->(e:ElementState{key:$element_state_key}) MATCH (t)-[]->(p:PageState{key:$page_state_key}) RETURN t")
	public List<Test> findTestWithElementState(@Param("page_state_key") String page_state_key, @Param("element_state_key") String element_state_key);

	@Query("MATCH (account:Account)-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test) MATCH (t)-[r:HAS_PATH_OBJECT]->(p:PageState{key:$page_state_key}) WHERE id(account)=$account_id RETURN t")
	public List<Test> findTestWithPageState(@Param("page_state_key") String page_state_key, @Param("url") String url, @Param("account_id") long account_id);

	@Query("MATCH (:Test{key:$key})-[:HAS_GROUP]-(g) RETURN g")
	public Set<Group> getGroups(@Param("key") String key);
	
	@Query("MATCH (a:Account{user_id: $user_id})-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test),(tr:TestRecord{key:$test_record_key}) MERGE (t)-[r:HAS_TEST_RECORD]->(tr) RETURN r")
	public void addTestRecord(@Param("key") String key, @Param("test_record_key") String test_record_key);
	
	@Query("MATCH (a:Account{user_id:$user_id})-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test{key:$test_key}) MATCH (t)-[:HAS_RESULT]->(p:PageState) RETURN p")
	public PageState getResult(@Param("test_key") String test_key, @Param("url") String url, @Param("user_id") String user_id);
	
	@Query("MATCH (a:Account{user_id: $user_id})-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test) WHERE {path_obj_key} in t.path_keys RETURN t")
	public Set<Test> findAllTestRecordsContainingKey(@Param("path_obj_key") String path_object_key, @Param("url") String url, @Param("user_id") String user_id);
	
	@Query("MATCH (a:Account{user_id: $user_id})-[:HAS_DOMAIN]->(d:Domain{url:$url}) MATCH (d)-[:HAS_TEST]->(t:Test{key:$test_key}),(g:Group{key:$group_key}) MERGE (t)-[r:HAS_GROUP]->(g) RETURN r")
	public void addGroup(@Param("test_key") String test_key, @Param("group_key") String group_key, @Param("url") String url, @Param("user_id") String user_id);
}
