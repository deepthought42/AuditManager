package com.looksee.models.repository;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.Account;
import com.looksee.models.DiscoveryRecord;
import com.looksee.models.Domain;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.PageAuditRecord;

import io.github.resilience4j.retry.annotation.Retry;

@Repository
@Retry(name = "neoforj")
public interface AccountRepository extends Neo4jRepository<Account, Long> {
	@Query("MATCH (account:Account{email:$email}) RETURN account LIMIT 1")
	Account findByEmail(@Param("email") String username);

	@Query("MATCH (account:Account{user_id:$user_id}) RETURN account")
	Account findByUserId(@Param("user_id") String user_id);
	
	/** 
	 * NOTE:: relic from old days. Remove at first chance
	 * @param username
	 * @param month
	 * @return
	 */
	@Query("MATCH (account:Account {username:$user_id})-[]->(d:DiscoveryRecord) WHERE datetime(d.started_at).month=$month return d")
	@Deprecated
	public Set<DiscoveryRecord> getDiscoveryRecordsByMonth(@Param("username") String username, 
														  @Param("month") int month);
	
	@Query("MATCH (account:Account)-[hd:HAS]->(domain:Domain) WHERE id(account)=$account_id AND id(domain)=$domain_id DELETE hd")
	public void removeDomain(@Param("account_id") long account_id, @Param("domain_id") long domain_id);

	@Query("MATCH (account:Account {username:$acct_key})-[]->(d:Domain) MATCH (d)-[:HAS_TEST]-(t:Test) MATCH (t)-[:HAS_TEST_RECORD]->(tr:TestRecord) WHERE datetime(tr.ran_at).month=$month RETURN COUNT(tr)")
	public int getTestCountByMonth(@Param("acct_key") String acct_key, 
								   @Param("month") int month);

	@Query("MATCH (account:Account) WHERE id(account)=$account_id DETACH DELETE account")
	public void deleteAccount(@Param("account_id") long account_id);

	@Query("MATCH (account:Account{api_key:$api_key}) RETURN account")
	public Account getAccountByApiKey(@Param("api_key") String api_key);

	@Query("MATCH (t:Account{email:$email}) WITH t MATCH (a:Domain{key:$domain_key}) MERGE (t)-[r:BELONGS_TO]->(a) RETURN r")
	public void addDomain(@Param("domain_key") String key, @Param("email") String username);

	@Query("MATCH (account:Account{email:$email})-[:HAS]->(domain:Domain{url:$url}) RETURN domain LIMIT 1")
	public Domain findDomain(@Param("email") String email, @Param("url") String url);

	@Query("MATCH (t:Account{username:$username}) WITH t MATCH (a:AuditRecord) WHERE id(a)=$audit_record_id MERGE (t)-[r:HAS]->(a) RETURN r")
	public AuditRecord addAuditRecord(@Param("username") String username, @Param("audit_record_id") long audit_record_id);

	@Query("MATCH (t:Account) WITH t MATCH (a:AuditRecord) WHERE id(a)=$audit_record_id AND id(t)=$account_id MERGE (t)-[r:HAS]->(a) RETURN a")
	public AuditRecord addAuditRecord(@Param("account_id") long account_id, @Param("audit_record_id") long audit_record_id);

	@Query("MATCH (account:Account)-[]->(audit_record:AuditRecord) WHERE id(audit_record)=$audit_record_id RETURN account")
	public Set<Account> findAllForAuditRecord(@Param("audit_record_id") long id);

	@Query("MATCH (account:Account)-[]->(audit_record:PageAuditRecord) WHERE id(account)=$account_id RETURN audit_record ORDER BY audit_record.created_at DESC LIMIT 5")
	public Set<PageAuditRecord> findMostRecentAuditsByAccount(long account_id);

	@Query("MATCH (account:Account)-[:HAS]->(domain:Domain) WHERE id(account)=$account_id RETURN domain")
	Set<Domain> getDomainsForAccount(@Param("account_id") long account_id);

	@Query("MATCH (account:Account)-[]->(page_audit:PageAuditRecord) WHERE id(account)=$account_id AND datetime(page_audit.created_at).month=$month RETURN COUNT(page_audit)")
	int getPageAuditCountByMonth(@Param("account_id") long account_id, @Param("month") int month);

	@Query("MATCH (account:Account{customer_id:$customer_id}) RETURN account")
	public Account findByCustomerId(@Param("customer_id") String customer_id);
	
	@Query("MATCH (account:Account)-[:HAS]->(domain:Domain) MATCH (domain)<-[:HAS]-(audit_record:DomainAuditRecord) WHERE id(account)=$account_id AND datetime(audit_record.created_at).month=$month RETURN COUNT(audit_record)")
	public int geDomainAuditRecordCountByMonth(@Param("account_id") long account_id, @Param("month") int month);
}
