package com.looksee.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.Account;
import com.looksee.models.DiscoveryRecord;
import com.looksee.models.Domain;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.PageAuditRecord;
import com.looksee.models.repository.AccountRepository;

/**
 * Contains business logic for interacting with and managing accounts
 *
 */
@Service
public class AccountService {

	@Autowired
	private AccountRepository account_repo;
	
	public void addDomainToAccount(Account acct, Domain domain){
		boolean domain_exists_for_acct = false;
		for(Domain acct_domain : acct.getDomains()){
			if(acct_domain.equals(domain)){
				domain_exists_for_acct = true;
			}
		}
		
		if(!domain_exists_for_acct){
			account_repo.addDomain(domain.getKey(), acct.getEmail());
			acct.addDomain(domain);
			account_repo.save(acct);
		}
	}

	public Account findByEmail(String email) {
		assert email != null;
		assert !email.isEmpty();
		
		return account_repo.findByEmail(email);
	}

	public Account save(Account acct) {
		return account_repo.save(acct);
	}

	public Account findByUserId(String id) {
		return account_repo.findByUserId(id);
	}

	public void deleteAccount(long account_id) {
        account_repo.deleteAccount(account_id);
	}
	
	public void removeDomain(long account_id, long domain_id) {
		account_repo.removeDomain(account_id, domain_id);
	}
	
	public Set<Domain> getDomainsForAccount(long account_id) {		
		return account_repo.getDomainsForAccount(account_id);
	}
	
	public Set<DiscoveryRecord> getDiscoveryRecordsByMonth(String username, int month) {
		return account_repo.getDiscoveryRecordsByMonth(username, month);
	}

	public int getTestCountByMonth(String username, int month) {
		return account_repo.getTestCountByMonth(username, month);
	}

	public Optional<Account> findById(long id) {
		return account_repo.findById(id);
	}

	public Domain findDomain(String email, String url) {
		assert email != null;
		assert !email.isEmpty();
		assert url != null;
		assert !url.isEmpty();
		
		return account_repo.findDomain(email, url);
	}

	public AuditRecord addAuditRecord(String username, long audit_record_id) {
		assert username != null;
		assert !username.isEmpty();
		
		return account_repo.addAuditRecord(username, audit_record_id);
	}
	
	public AuditRecord addAuditRecord(long id, long audit_record_id) {
		
		return account_repo.addAuditRecord(id, audit_record_id);
	}

	public Set<Account> findForAuditRecord(long id) {
		return account_repo.findAllForAuditRecord(id);
	}

	public Set<PageAuditRecord> findMostRecentPageAudits(long account_id) {
		return account_repo.findMostRecentAuditsByAccount(account_id);
	}

	public int getPageAuditCountByMonth(long account_id, int month) {
		return account_repo.getPageAuditCountByMonth(account_id, month);
	}

	public Account findByCustomerId(String customer_id) {
		return account_repo.findByCustomerId(customer_id);
	}
	
	public int getDomainAuditCountByMonth(long account_id, int month) {
		return account_repo.geDomainAuditRecordCountByMonth(account_id, month);
	}
}
