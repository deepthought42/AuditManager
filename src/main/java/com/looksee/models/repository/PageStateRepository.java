package com.looksee.models.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.ElementState;
import com.looksee.models.PageState;
import com.looksee.models.Screenshot;
import com.looksee.models.audit.Audit;
import com.looksee.models.audit.PageAuditRecord;

import io.github.resilience4j.retry.annotation.Retry;

/**
 * 
 */
@Repository
@Retry(name = "neoforj")
public interface PageStateRepository extends Neo4jRepository<PageState, Long> {
	@Query("MATCH (:Account{username:$user_id})-[*]->(p:PageState{key:$key}) RETURN p LIMIT 1")
	public PageState findByKeyAndUsername(@Param("user_id") String user_id, @Param("key") String key);

	@Query("MATCH (p:PageState{key:$key}) RETURN p LIMIT 1")
	public PageState findByKey(@Param("key") String key);

	@Deprecated
	@Query("MATCH (:Account{username:$user_id})-[]->(d:Domain{url:$url}) MATCH (d)-[]->(p:PageState) MATCH a=(p)-[h:HAS]->() WHERE $screenshot_checksum IN p.screenshot_checksums RETURN a")
	public List<PageState> findByScreenshotChecksumsContainsForUserAndDomain(@Param("user_id") String user_id, @Param("url") String url, @Param("screenshot_checksum") String checksum );
	
	@Query("MATCH (p:PageState{url:$url})-[h:HAS]->() WHERE $screenshot_checksum IN p.screenshot_checksums RETURN a")
	public List<PageState> findByScreenshotChecksumAndPageUrl(@Param("url") String url, @Param("screenshot_checksum") String checksum );
	
	@Query("MATCH (p:PageState{full_page_checksum:$screenshot_checksum}) MATCH a=(p)-[h:HAS_CHILD]->() RETURN a")
	public List<PageState> findByFullPageScreenshotChecksum(@Param("screenshot_checksum") String checksum );

	@Query("MATCH (p:PageState{key:$page_key})-[:HAS]->(e:ElementState) RETURN DISTINCT e")
	public List<ElementState> getElementStates(@Param("page_key") String key);
	
	@Query("MATCH (p:PageState)-[:HAS]->(e:ElementState) WHERE id(p)=$page_state_id RETURN DISTINCT e")
	public List<ElementState> getElementStates(@Param("page_state_id") long page_state_id);

	@Query("MATCH (p:PageState)-[:HAS]->(e:ElementState{name:'a'}) WHERE id(p)=$page_state_id RETURN DISTINCT e")
	public List<ElementState> getLinkElementStates(@Param("page_state_id") long page_state_id);

	@Query("MATCH (:Account{username:$user_id})-[*]->(p:PageState{key:$page_key}) MATCH (p)-[h:HAS]->(s:Screenshot) RETURN s")
	public List<Screenshot> getScreenshots(@Param("user_id") String user_id, @Param("page_key") String page_key);

	@Query("MATCH (:Account{username:$user_id})-[*]->(p:PageState{key:$page_key}) WHERE $screenshot_checksum IN p.animated_image_checksums RETURN p LIMIT 1")
	public PageState findByAnimationImageChecksum(@Param("user_id") String user_id, @Param("screenshot_checksum") String screenshot_checksum);

	@Query("MATCH (a:Account)-[]->(d:Domain{url:$url}) MATCH (d)-[]->(p:PageState) MATCH (p)-[:HAS]->(f:Form{key:$form_key}) WHERE id(account)=$account_id RETURN p")
	public List<PageState> findPageStatesWithForm(@Param("account_id") long account_id, @Param("url") String url, @Param("form_key") String form_key);

	@Query("MATCH (d:Domain{url:$url})-[:HAS]->(ps:PageState{src_checksum:$src_checksum}) MATCH a=(ps)-[h:HAS]->() RETURN a")
	public List<PageState> findBySourceChecksumForDomain(@Param("url") String url, @Param("src_checksum") String src_checksum);
	
	@Query("MATCH (ps:PageState{key:$page_state_key})<-[]-(a:Audit) RETURN a")
	public List<Audit> getAudits(@Param("page_state_key") String page_state_key);

	@Query("MATCH (p:PageState{key:$page_state_key})-[*]->(a:Audit{subcategory:$subcategory}) RETURN a")
	public Audit findAuditBySubCategory(@Param("subcategory") String subcategory, @Param("page_state_key") String page_state_key);

	@Query("MATCH (p:PageState{key:$page_state_key})-[:HAS]->(e:ElementState{classification:'leaf'}) where e.visible=true RETURN e")
	public List<ElementState> getVisibleLeafElements(@Param("page_state_key") String page_state_key);

	@Query("ps:PageState{key:$page_state_key}) return p LIMIT 1")
	public PageState getParentPage(@Param("page_state_key") String page_state_key);

	@Query("MATCH (p:PageState{url:$url}) RETURN p ORDER BY p.created_at DESC LIMIT 1")
	public PageState findByUrl(@Param("url") String url);

	@Query("MATCH (p:PageState) WITH p MATCH (element:ElementState) WHERE id(p)=$page_id AND id(element)=$element_id MERGE (p)-[:HAS]->(element) RETURN element LIMIT 1")
	public ElementState addElement(@Param("page_id") long page_id, @Param("element_id") long element_id);

	@Query("MATCH (p:PageState)-[:HAS]->(element:ElementState) WHERE id(p)=$page_id AND id(element)=$element_id RETURN element ORDER BY p.created_at DESC LIMIT 1")
	public Optional<ElementState> getElementState(@Param("page_id") long page_id, @Param("element_id") long element_id);

	@Query("MATCH (a:PageAuditRecord)-[:FOR]->(ps:PageState) WHERE id(ps)=$id RETURN a ORDER BY a.created_at DESC LIMIT 1")
	public PageAuditRecord getAuditRecord(@Param("id") long id);

	@Query("MATCH (ps:PageState) WHERE id(ps)=$id SET ps.fullPageScreenshotUrlComposite = $composite_img_url RETURN ps")
	public void updateCompositeImageUrl(@Param("id") long id, @Param("composite_img_url") String composite_img_url);

	@Query("MATCH (p:PageState) WITH p MATCH (element:ElementState) WHERE id(p)=$page_state_id AND id(element) IN $element_id_list MERGE (p)-[:HAS]->(element) RETURN element")
	public void addAllElements(@Param("page_state_id") long page_state_id, @Param("element_id_list") List<Long> element_id_list);
}
