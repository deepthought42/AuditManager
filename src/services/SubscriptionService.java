package com.looksee.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Value;
import com.looksee.models.Account;
import com.looksee.models.DiscoveryRecord;
import com.looksee.models.enums.SubscriptionPlan;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.SubscriptionItemCollection;
import com.stripe.model.checkout.Session;

/**
 * Provides methods to check if an {@link Account} user has permission to access a restricted resource and verifying that
 * the {@link Account} user has not exceeded their usage.
 * 
 */
@Service
@PropertySource("classpath:application.properties")
public class SubscriptionService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${stripe.agency_basic_price_id}")
	private String agency_basic_price_id;
	
	@Value("${stripe.agency_pro_price_id}")
	private String agency_pro_price_id;
	
	@Value("${stripe.company_basic_price_id}")
	private String company_basic_price_id;
	
	@Value("${stripe.company_pro_price_id}")
	private String company_pro_price_id;
	
	@Autowired
	private StripeService stripe_client;
	
	@Autowired
	private AccountService account_service;
	
	
	public SubscriptionService(AccountService account_service){
		this.account_service = account_service;
	}

	/**
	 * Updates the {@link Subscription} for a given {@link Account}
	 * 
	 * @param acct
	 * @param plan
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	@Deprecated
	public void changeSubscription(Account acct, SubscriptionPlan plan) throws Exception{
		String plan_str = plan.toString();
		
		//retrive stripe customer info
		Customer customer = null;
		if(acct.getCustomerToken() == null 
				|| acct.getCustomerToken().isEmpty()
		) {
			customer = this.stripe_client.createCustomer(null, acct.getEmail());
			acct.setCustomerToken(customer.getId());
		}
		else {
			customer = stripe_client.getCustomer(acct.getCustomerToken());
		}
		acct.setSubscriptionType(plan_str);

		if("FREE".equals(plan_str)){
			//check if account has a subscription, if so then unsubscribe and remove subscription token
			if(acct.getSubscriptionToken() != null && 
					!acct.getSubscriptionToken().isEmpty()){
	    		stripe_client.cancelSubscription(acct.getSubscriptionToken());
	    		acct.setSubscriptionToken("");
			}
			else{
				log.warn("User already has free plan");
			}
		}
		else if("COMPANY_BASIC".equals(plan_str)){
			//STAGING
			//plan_tier = Plan.retrieve("plan_GKyHict9ublpsa");
			//PRODUCTION
			//plan_tier = Plan.retrieve("plan_GJrQYSjKUHpRB1");
			String companyBasicPrice = "price_1JuNRWKuRH6u2PgWXdcWmZUD";
			
	    	Subscription subscription = null;
	    	
	    	if(acct.getSubscriptionToken() == null || acct.getSubscriptionToken().isEmpty()){
	    		subscription = stripe_client.subscribe(companyBasicPrice, customer);
	    	}else{
	    		subscription = stripe_client.getSubscription(acct.getSubscriptionToken());
	    		stripe_client.update_subscription(companyBasicPrice, subscription);
	        	Map<String, Object> item = new HashMap<>();
	        	subscription = stripe_client.getSubscription(acct.getSubscriptionToken());
	        	item.put("id", subscription.getId());
	        	item.put("price", companyBasicPrice);
	
	        	Map<String, Object> items = new HashMap<>();
	        	items.put("0", item);
	
	        	Map<String, Object> params = new HashMap<>();
	        	params.put("items", items);
	        	
	        	subscription.update(params);
	    	}
	    	
	    	acct.setSubscriptionToken(subscription.getId());
    		acct.setSubscriptionType(plan_str);
		}
		else if("COMPANY_PRO".equals(plan_str)){

		}
		else if("AGENCY_BASIC".equals(plan_str)){

		}
		else if("AGENCY_PRO".equals(plan_str)){
		
		}
		else if("UNLIMITED".equals(plan_str)){
			
		}
		
    	account_service.save(acct);
	}
	
	/**
	 * Updates the {@link Subscription} for a given {@link Account}
	 * 
	 * @param acct
	 * @param subscription_id TODO
	 * @param plan
	 * @return
	 * 
	 * @throws Exception
	 */
	public void changeSubscription(Account acct, String subscription_id) throws Exception{		
		assert acct != null;
		
		//retrive stripe customer info
		Customer customer = null;
		if(acct.getCustomerToken() == null 
				|| acct.getCustomerToken().isEmpty()
		) {
			customer = this.stripe_client.createCustomer(null, acct.getEmail());
			acct.setCustomerToken(customer.getId());
		}
		else {
			customer = stripe_client.getCustomer(acct.getCustomerToken());
		}

		//get price info
		Subscription subscription = stripe_client.getSubscription(subscription_id);
		
		SubscriptionItem item = Collections.max(subscription.getItems().getData(), Comparator.comparing(SubscriptionItem::getCreated));
		String product_id = item.getPrice().getProduct();
		Product product = stripe_client.getProduct(product_id);

    	if(acct.getSubscriptionToken() != null && !acct.getSubscriptionToken().isEmpty()){
    		//stripe_client.update_subscription(item.getPrice().getId(), subscription);		
    		//cancel existing subscription
    		stripe_client.cancelSubscription(acct.getSubscriptionToken());
    	}

    	acct.setSubscriptionType(product.getName());
    	acct.setSubscriptionToken(subscription_id);
    	account_service.save(acct);
	}
	
	/**
	 * checks if user has exceeded limit for page audits based on their subscription
	 * 
	 * @param acct {@link Account}
	 * 
	 * @return true if user has exceeded limits for their {@link SubscriptionPlan}, otherwise false
	 * 
	 * @throws StripeException
	 */
	public boolean hasExceededSinglePageAuditLimit(SubscriptionPlan plan, int page_audit_cnt) {    	
    	if(plan.equals(SubscriptionPlan.FREE) && page_audit_cnt >= 100){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.COMPANY_PRO) && page_audit_cnt >= 1000){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.COMPANY_PREMIUM)){ //UNLIMITED
    		return false;
    	}
    	else if(plan.equals(SubscriptionPlan.AGENCY_PRO) && page_audit_cnt >= 5000){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.AGENCY_PREMIUM) ){ //UNLIMITED
    		return false;
    	}
    	else if(plan.equals(SubscriptionPlan.UNLIMITED)){
    		return false;
    	}
    	
    	return false;
	}
	
	/**
	 * checks if user has exceeded limit for page limit for domain audit based on their subscription
	 * @param plan TODO
	 * @param page_audit_count TODO
	 * @param acct {@link Account}
	 * @return true if user has exceeded limits for their {@link SubscriptionPlan}, otherwise false
	 * 
	 * @pre plan != null
	 * 
	 * @throws StripeException
	 */
	public boolean hasExceededDomainPageAuditLimit(SubscriptionPlan plan, int page_audit_count) {				    	
    	if(plan.equals(SubscriptionPlan.FREE) && page_audit_count >= 20){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.COMPANY_PRO) && page_audit_count >= 200){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.COMPANY_PREMIUM) && page_audit_count >= 500){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.AGENCY_PRO) && page_audit_count >= 500){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.AGENCY_PREMIUM) && page_audit_count >= 2000){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.UNLIMITED)){
    		return false;
    	}
    	
    	return false;
	}
	

	/**
	 * Checks if account has exceeded the allowed number of domain audits 
	 * @param create
	 * @param domain_audit_cnt
	 * @return
	 */
	public boolean hasExceededDomainAuditLimit(SubscriptionPlan plan, int domain_audit_cnt) {
		if(plan.equals(SubscriptionPlan.FREE) && domain_audit_cnt >= 5){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.COMPANY_PRO) && domain_audit_cnt >= 20){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.COMPANY_PREMIUM) && domain_audit_cnt >= 100){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.AGENCY_PRO) && domain_audit_cnt >= 50){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.AGENCY_PREMIUM) && domain_audit_cnt >= 200){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.UNLIMITED)){
    		return true;
    	}
    	
    	return false;
	}
	
	/**
	 * checks if user has exceeded test run limit for their subscription
	 * 
	 * @param acct {@link Account}
	 * 
	 * @return true if user has exceeded limits for their {@link SubscriptionPlan}, otherwise false
	 * 
	 * @throws StripeException
	 */
	public boolean hasExceededSubscriptionTestRunsLimit(Account acct, SubscriptionPlan plan) throws StripeException{
    	//check if user has exceeded freemium plan
    	Date date = new Date();
    	int test_run_cnt = account_service.getTestCountByMonth(acct.getEmail(), date.getMonth());
    	//check if user has exceeded freemium plan
    	Set<DiscoveryRecord> discovery_records = account_service.getDiscoveryRecordsByMonth(acct.getEmail(), date.getMonth());
    	int discovered_test_cnt = 0;
    	
    	for(DiscoveryRecord record : discovery_records){
    		if(record.getStartTime().getMonth() == date.getMonth()){
    			discovered_test_cnt += record.getTestCount();
    		}
    	}
    	
    	test_run_cnt -= discovered_test_cnt;
    	
    	if(plan.equals(SubscriptionPlan.FREE) && test_run_cnt > 200){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.COMPANY_PRO) && test_run_cnt > 2000){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.UNLIMITED)){
    		return true;
    	}
    	
    	return false;
	}
	
	/**
	 * Checks if a user has exceeded their {@link Subscription} limit on {@link Discovery}s
	 * 
	 * @param acct {@link Account}
	 * @param plan {@Subscription}
	 * 
	 * @return true if user has exceeded the limits for their {@SubscriptionPlan}
	 * 
	 * @throws StripeException
	 */
	public boolean hasExceededSubscriptionDiscoveredLimit(Account acct, SubscriptionPlan plan) throws StripeException{    	
    	//check if user has exceeded freemium plan
    	Date date = new Date();
    	Set<DiscoveryRecord> discovery_records = account_service.getDiscoveryRecordsByMonth(acct.getEmail(), date.getMonth());
    	int discovered_test_cnt = 0;
    	
    	for(DiscoveryRecord record : discovery_records){
    		if(record.getStartTime().getMonth() == date.getMonth()){
    			discovered_test_cnt += record.getTestCount();
    		}
    	}
    	
    	if(plan.equals(SubscriptionPlan.FREE) && discovered_test_cnt > 100){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.COMPANY_PRO) && discovered_test_cnt > 250){
    		return true;
    	}
    	else if(plan.equals(SubscriptionPlan.UNLIMITED)){
    		return true;
    	}
    	
    	
    	return false;
	}
	
	/**
	 * Uses the user {@link Account} to retrieve a subscription
	 * 
	 * @param acct
	 * @return
	 * @throws StripeException
	 */
	public SubscriptionPlan getSubscriptionPlanName(Account acct) throws StripeException {
		Subscription subscription = null;
		SubscriptionPlan account_subscription = null;
    	if(acct.getSubscriptionToken() == null || acct.getSubscriptionToken().isEmpty()){
    		//free plan
    		account_subscription = SubscriptionPlan.FREE;
    	}
    	else {
    		subscription = stripe_client.getSubscription(acct.getSubscriptionToken());
    		SubscriptionItemCollection items = subscription.getItems();
    		
        	for(SubscriptionItem item: items.getData()) {
	        	if(item.getPrice().getId().equals(agency_basic_price_id)){
	        		return SubscriptionPlan.AGENCY_PRO;
	        	}
	        	else if(item.getPrice().getId().equals(agency_pro_price_id)){
	        		return SubscriptionPlan.AGENCY_PREMIUM;
	        	}
	        	else if(item.getPrice().getId().equals(company_basic_price_id)){
	        		return SubscriptionPlan.COMPANY_PRO;
	        	}
	        	else if(item.getPrice().getId().equals(company_pro_price_id)){
	        		return SubscriptionPlan.COMPANY_PREMIUM;
	        	}
	        	else{
	        		return SubscriptionPlan.UNLIMITED;
	        	}
        	}
    	}
    	
    	return account_subscription;
	}
	
	/**
	 * Uses the user {@link Account} to retrieve a subscription
	 * 
	 * @param acct
	 * @return
	 * @throws StripeException
	 */
	public SubscriptionPlan getSubscriptionPlanName(String subscription_token) throws StripeException {
		Subscription subscription = null;
		SubscriptionPlan account_subscription = null;
    	if(subscription_token == null || subscription_token.isEmpty()){
    		//free plan
    		account_subscription = SubscriptionPlan.FREE;
    	}
    	else {
    		subscription = stripe_client.getSubscription(subscription_token);        	
        	//check for product
    		
    		SubscriptionItem item = Collections.max(subscription.getItems().getData(), Comparator.comparing(SubscriptionItem::getCreated));

        	if(item.getPrice().getId().equals(agency_basic_price_id)){
        		account_subscription = SubscriptionPlan.AGENCY_PRO;
        	}
        	else if(item.getPrice().getId().equals(agency_pro_price_id)){
        		account_subscription = SubscriptionPlan.AGENCY_PREMIUM;
        	}
        	else if(item.getPrice().getId().equals(company_basic_price_id)){
        		account_subscription = SubscriptionPlan.COMPANY_PRO;
        	}
        	else if(item.getPrice().getId().equals(company_pro_price_id)){
        		account_subscription = SubscriptionPlan.COMPANY_PREMIUM;
        	}
        	else{
        		account_subscription = SubscriptionPlan.UNLIMITED;
        	}
    	}
    	
    	return account_subscription;
	}

	/**
	 * Creates new checkout session
	 * 
	 * @param price_id
	 * @param customer_id TODO
	 * @param customer_email TODO
	 * @return
	 * @throws StripeException
	 */
	public Session createCheckoutSession(String price_id, 
										 String customer_id, 
										 String customer_email
	 ) throws StripeException {
		return stripe_client.createCheckoutSession(price_id, 
												   customer_id, 
												   customer_email);
	}

	/**
	 * Checks if the given {@link Account} can access the competitive analysis functionality. As of 2/14/2022, this feature is available to anyone with a paid account
	 * @param account
	 * @return
	 */
	public boolean canAccessCompetitiveAnalysis(Account account) {
    	if(account.getSubscriptionToken() == null || account.getSubscriptionToken().isEmpty()){
    		//free plan
    		return false;
    	}
    	else {
    		return true;
    	}
	}

}
