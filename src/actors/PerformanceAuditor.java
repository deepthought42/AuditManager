package com.looksee.actors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

import com.google.api.client.util.ArrayMap;
import com.google.api.services.pagespeedonline.v5.model.AuditRefs;
import com.google.api.services.pagespeedonline.v5.model.LighthouseAuditResultV5;
import com.google.api.services.pagespeedonline.v5.model.PagespeedApiPagespeedResponseV5;
import com.looksee.gcp.PageSpeedInsightUtils;
import com.looksee.models.ElementState;
import com.looksee.models.PageState;
import com.looksee.models.audit.UXIssueMessage;
import com.looksee.models.audit.performance.AccessibilityDetailNode;
import com.looksee.models.audit.performance.AssetSize;
import com.looksee.models.audit.performance.AssetSizeOpportunityDetail;
import com.looksee.models.audit.performance.AuditDetail;
import com.looksee.models.audit.performance.BlockingResource;
import com.looksee.models.audit.performance.BootUpTime;
import com.looksee.models.audit.performance.CachingDetail;
import com.looksee.models.audit.performance.DiagnosticDetail;
import com.looksee.models.audit.performance.DomSize;
import com.looksee.models.audit.performance.GroupWorkBreakdown;
import com.looksee.models.audit.performance.MetricsDetail;
import com.looksee.models.audit.performance.NetworkRequestDetail;
import com.looksee.models.audit.performance.PageSpeedAudit;
import com.looksee.models.audit.performance.PerformanceInsight;
import com.looksee.models.audit.performance.ResourceSummary;
import com.looksee.models.audit.performance.ScreenshotThumbnailDetails;
import com.looksee.models.audit.performance.ThirdPartySummaryDetail;
import com.looksee.models.audit.performance.TimingDetail;
import com.looksee.models.audit.performance.WebPImageDetail;
import com.looksee.models.audit.recommend.Recommendation;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.CaptchaResult;
import com.looksee.models.enums.FormFactor;
import com.looksee.models.enums.InsightType;
import com.looksee.models.enums.ObservationType;
import com.looksee.models.enums.Priority;
import com.looksee.services.AuditDetailService;
import com.looksee.services.ElementStateService;
import com.looksee.services.PageSpeedAuditService;
import com.looksee.services.PerformanceInsightService;
import com.looksee.utils.BrowserUtils;

/**
 * Manages a browser instance and sets a crawler upon the instance using a given path to traverse
 *
 */
@Component
@Scope("prototype")
public class PerformanceAuditor extends AbstractActor {
	private static Logger log = LoggerFactory.getLogger(PerformanceAuditor.class.getName());
	private Cluster cluster = Cluster.get(getContext().getSystem());
	
	@Autowired
	private ElementStateService element_state_service;

	@Autowired
	private PageSpeedAuditService audit_service;
	
	@Autowired
	private AuditDetailService audit_detail_service;
	
	@Autowired
	private PerformanceInsightService performance_insight_service;
	
	//subscribe to cluster changes
	@Override
	public void preStart() {
	  cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
	      MemberEvent.class, UnreachableMember.class);
	}

	//re-subscribe when restart
	@Override
    public void postStop() {
	  cluster.unsubscribe(getSelf());
    }

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(PageState.class, page -> {
					/*
					Timeout timeout = Timeout.create(Duration.ofSeconds(120));
					Future<Object> future = Patterns.ask(message.getDomainActor(), new DiscoveryActionRequest(message.getDomain(), message.getAccountId()), timeout);
					DiscoveryAction discovery_action = (DiscoveryAction) Await.result(future, timeout.duration());
					
					if(discovery_action == DiscoveryAction.STOP) {
						log.warn("path message discovery actor returning");
						return;
					}
					*/
										
					//log.warn("page states count :: " + page.getPageStates().size());
					PagespeedApiPagespeedResponseV5 page_speed_response = PageSpeedInsightUtils.getPageInsights(BrowserUtils.sanitizeUrl(page.getUrl(), page.isSecure()));
					log.warn("page speed response length :: " + page_speed_response.toPrettyString().length());
					List<UXIssueMessage> ux_issues = PageSpeedInsightUtils.extractFontSizeIssues(page_speed_response);

					//List<UXIssueMessage> ux_issues = extractInsights(page_speed_response);
					
					log.warn("UX Issues found by performance issues :: "+ux_issues.size());
					log.warn("??????????????????????????????????????????????????????????????????");
					log.warn("??????????????????????????????????????????????????????????????????");
					//Page page = new Page(page.getUrl());
					//page.setPerformanceScore(performance_insight.getSpeedScore());
					//page.setAccessibilityScore(performance_insight.getAccessibilityScore());
					//page.setSeoScore(performance_insight.getSeoScore());
					//page.setOverallScore(performance_insight.getOverallScore());
					//page = page_state_service.save(page);
					
					//domain_service.addPageState(message.getDomain().getUrl(), page_state, message.getAccount());
					//performance_insight_service.save(performance_insight);
					
					//TODO compile insights into ux issue messages that are then added to the current audit
					//page_state_service.addPerformanceInsight(message.getAccountId(), message.getDomain().getEntryPath(), page.getKey(), performance_insight.getKey());
					//domain_service.addPage(new URL(BrowserUtils.sanitizeUrl(page.getUrl())).getHost(), page.getKey());

					log.warn("creating landing page performance and SEO insights");					
				})
				.match(MemberUp.class, mUp -> {
					log.info("Member is Up: {}", mUp.member());
				})
				.match(UnreachableMember.class, mUnreachable -> {
					log.info("Member detected as unreachable: {}", mUnreachable.member());
				})
				.match(MemberRemoved.class, mRemoved -> {
					log.info("Member is Removed: {}", mRemoved.member());
				})
				.matchAny(o -> {
					log.info("received unknown message");
				})
				.build();
	}
	
	
	/**
	 * Extract page speed insights data and performance audits
	 * 
	 * @param page_speed_response
	 * @return
	 */
	public List<UXIssueMessage> extractInsights(PagespeedApiPagespeedResponseV5 page_speed_response) {
		List<UXIssueMessage> ux_issues = new ArrayList<UXIssueMessage>();
		
		PerformanceInsight speed_insight = new PerformanceInsight(
				new Date(),
				page_speed_response.getLighthouseResult().getTiming().getTotal(),
				page_speed_response.getId(),
				page_speed_response.getLighthouseResult().getConfigSettings().getLocale(),
				CaptchaResult.create(page_speed_response.getCaptchaResult()),
				page_speed_response.getLighthouseResult().getRunWarnings(),
				FormFactor.create(page_speed_response.getLighthouseResult().getConfigSettings().getEmulatedFormFactor() ));
	    
	    if(page_speed_response.getLighthouseResult().getRuntimeError() != null) {
	    	speed_insight.setRuntimeErrorCode( page_speed_response.getLighthouseResult().getRuntimeError().getCode() );
	    	speed_insight.setRuntimeErrorMessage( page_speed_response.getLighthouseResult().getRuntimeError().getMessage() );
	    }
	    
	    log.warn("speed insight object built...");
	    Map<String, LighthouseAuditResultV5> audit_map = page_speed_response.getLighthouseResult().getAudits();
	    
	    log.warn("accessiblity exists :: "+page_speed_response.getLighthouseResult().getCategories().getAccessibility().toString());
	    Map<InsightType, List<String>> audit_ref_map = new HashMap<>();
	    List<AuditRefs> audit_refs = page_speed_response.getLighthouseResult().getCategories().getPerformance().getAuditRefs();
	    for(AuditRefs ref : audit_refs) {
	    	if(!audit_ref_map.containsKey(InsightType.PERFORMANCE)) {
	    		audit_ref_map.put(InsightType.PERFORMANCE, new ArrayList<String>());
	    	}
	    	audit_ref_map.get(InsightType.PERFORMANCE).add(ref.getId());
	    }
	    
	    audit_refs = page_speed_response.getLighthouseResult().getCategories().getAccessibility().getAuditRefs();
	    for(AuditRefs ref : audit_refs) {
	    	if(!audit_ref_map.containsKey(InsightType.ACCESSIBILITY)) {
	    		audit_ref_map.put(InsightType.ACCESSIBILITY, new ArrayList<String>());
	    	}
	    	audit_ref_map.get(InsightType.ACCESSIBILITY).add(ref.getId());
	    }
	    
    	for(LighthouseAuditResultV5 audit_record  : audit_map.values()) {
			Set<Recommendation> recommendations = new HashSet<>();
			
    		UXIssueMessage issue_msg = new UXIssueMessage(
    											Priority.HIGH,
    											audit_record.getDescription(), 
    											ObservationType.PAGE_STATE, 
    											AuditCategory.INFORMATION_ARCHITECTURE, 
    											"wcag compliance", 
    											new HashSet<>(), 
    											audit_record.getExplanation(),
    											audit_record.getTitle(),
    											0, 
    											1, 
    											"");
    				
    		ux_issues.add(issue_msg);
    		
    		InsightType insight_type = getAuditType(audit_record, audit_ref_map);
    		
    		log.warn("audit record id  ....  "+audit_record.getId());
    		log.warn("audit record description  ....  "+audit_record.getDescription());
    		log.warn("audit record display value  ....  "+audit_record.getDisplayValue());
    		log.warn("audit record error msg  ....  "+audit_record.getErrorMessage());
    		log.warn("audit record explanation  ....  "+audit_record.getExplanation());
    		log.warn("audit record score display mode ....  "+audit_record.getScoreDisplayMode());
    		log.warn("audit record title  ....  "+audit_record.getTitle());
    		log.warn("audit record numeric value  ....  "+audit_record.getNumericValue());
    		log.warn("audit record score ....  "+audit_record.getScore());
    		log.warn("audit record warnings  ....  "+audit_record.getWarnings());
    		
    		PageSpeedAudit audit = new PageSpeedAudit(
    				audit_record.getId(),
    				audit_record.getDescription(),
    				audit_record.getDisplayValue(),
    				audit_record.getErrorMessage(),
    				audit_record.getExplanation(),
    				audit_record.getNumericValue(),
    				audit_record.getScoreDisplayMode(),
    				audit_record.getTitle());
    		Double score = convertScore(audit_record.getScore());
    		audit.setScore(score);
    		audit.setType(insight_type);
    		audit = audit_service.save(audit);
    		
    		speed_insight.addAudit(audit);
    	}
    	
    	double speed_score = convertScore(page_speed_response.getLighthouseResult().getCategories().getPerformance().getScore());
    	double accessibility_score = convertScore(page_speed_response.getLighthouseResult().getCategories().getAccessibility().getScore());
    	double seo_score = convertScore(page_speed_response.getLighthouseResult().getCategories().getSeo().getScore());
    	speed_insight.setSeoScore(seo_score);
    	speed_insight.setAccessibilityScore(accessibility_score);
    	speed_insight.setSpeedScore(speed_score);
    	speed_insight.setOverallScore(calculatePageScore(speed_insight));
    	log.warn("speed insight audits found :: "+speed_insight.getAudits().size());
    	return ux_issues;
	}

	/**
	 * 
	 * @param performance_insight
	 * @return
	 * 
	 * @pre performance_insight != null
	 */
	private double calculatePageScore(PerformanceInsight performance_insight) {
		assert performance_insight != null;
		
		int insight_cnt = 0;
		double score_total = 0.0;
		
		if(performance_insight.getAccessibilityScore() > 0.0) {
			score_total += performance_insight.getAccessibilityScore();
			insight_cnt++;
		}
		
		log.warn("accessibility score :: " + performance_insight.getAccessibilityScore());
		if(performance_insight.getSpeedScore() > 0.0) {
			score_total += performance_insight.getSpeedScore();
			insight_cnt++;
		}
		log.warn("speed score :: "+performance_insight.getSpeedScore());
		return score_total/insight_cnt;
	}
	
	
	
	/**
	 * Extracts the details object list from the JSON object and formats the data into the appropriate details object
	 * 
	 * @param user_id
	 * @param name
	 * @param details
	 *
	 * @return
	 * 
	 * @pre details != null;
	 */
	private List<AuditDetail> extractAuditDetails(long account_id, String name, Map<String, Object> details) {
		assert name != null;
		assert !name.isEmpty();
		assert details != null;
		
		List<AuditDetail> audit_details = new ArrayList<>();
		if(details != null) {
			List<Object> items = (List<Object>)details.get("items");
			if(items == null) {
				return audit_details;
			}
			for(Object item: items) {
				ArrayMap<String, Object> detail_obj = (ArrayMap)item;

				if("screenshot-thumbnails".contentEquals(name) || "final-screenshot".contentEquals(name)){
					AuditDetail audit_detail = new ScreenshotThumbnailDetails(getDoubleValue(detail_obj, "timing"),
																			  Long.valueOf(detail_obj.get("timestamp").toString()),
																			  detail_obj.get("data").toString());
					audit_details.add(audit_detail);
				}
				else if("resource-summary".contentEquals(name)) {
					AuditDetail audit_detail = new ResourceSummary(getIntegerValue(detail_obj, "requestCount"), 
																   getStringValue(detail_obj, "resourceType"), 
																   getStringValue(detail_obj, "label"), 
																   getLongValue(detail_obj, "size"));
					audit_details.add(audit_detail);
				}
				else if("render-blocking-resources".contentEquals(name)) {
					AuditDetail audit_detail = new BlockingResource(getStringValue(detail_obj, "url"), 
							 										getIntegerValue(detail_obj, "totalBytes"), 
							 										getDoubleValue(detail_obj, "wastedMs"));
					audit_details.add(audit_detail);
				}
				else if("font-display".contentEquals(name)) {
					AuditDetail audit_detail = new BlockingResource(getStringValue(detail_obj, "url"), null, Double.parseDouble(detail_obj.get("wastedMs").toString()));
					audit_details.add(audit_detail);
				}
				else if("user-timings".contentEquals(name) || "main-thread-tasks".contentEquals(name)) {
					AuditDetail audit_detail = new TimingDetail(getStringValue(detail_obj, "name"), 
																getDoubleValue(detail_obj, "startTime"), 
															    getDoubleValue(detail_obj, "duration"), 
															    getStringValue(detail_obj, "timingType"));
					audit_details.add(audit_detail);
				}
				else if("bootup-time".contentEquals(name)) {
					AuditDetail audit_detail = new BootUpTime(detail_obj.get("url").toString(), 
															  getDoubleValue(detail_obj, "total"),
															  getDoubleValue(detail_obj, "scripting"),
															  getDoubleValue(detail_obj, "scriptParseCompile"));
					audit_details.add(audit_detail);
				}
				else if("total-byte-weight".contentEquals(name)) {
					AuditDetail audit_detail = new AssetSize(detail_obj.get("url").toString(), 
															 Integer.parseInt(detail_obj.get("totalBytes").toString()));
					audit_details.add(audit_detail);
				}
				else if("mainthread-work-breakdown".contentEquals(name)) {
					AuditDetail audit_detail = new GroupWorkBreakdown(detail_obj.get("group").toString(), 
																	  getDoubleValue(detail_obj, "duration"), 
																	  detail_obj.get("groupLabel").toString());
					audit_details.add(audit_detail);
				}
				else if("color-contract".contentEquals(name) 
						|| "frame-title".contentEquals(name) 
						|| "link-name".contentEquals(name) 
						|| "meta-viewport".contentEquals(name) 
						|| "label".contentEquals(name)
				) {
					ElementState element_state = element_state_service.findByOuterHtml(account_id,  getStringValue(detail_obj, "snippet"));
					String explanation = getStringValue(detail_obj, "explanation");

					int fix_all_idx = explanation.indexOf("Fix all of the following:");
					int fix_any_idx = explanation.indexOf("Fix any of the following:");
					explanation = explanation.trim();
					
					String optional_details = null;
					String required_details = null;
					if(fix_all_idx > 0 && fix_any_idx == 0) {
						optional_details = explanation.substring(0, fix_all_idx);
						required_details = explanation.substring(fix_all_idx);
					}
					else if(fix_all_idx == 0 && fix_any_idx > 0) {
						required_details = explanation.substring(0, fix_all_idx);
						optional_details = explanation.substring(fix_any_idx);
					}
					else {
						if(fix_all_idx >= 0) {
							required_details = explanation;
							optional_details = "";
						}
						else if(fix_any_idx >= 0) {
							optional_details = explanation;
							required_details = "";
						}
					}

					String[] optional_change_messages = optional_details.split("\\n");
					String[] required_change_messages = required_details.split("\\n");
					
					AuditDetail audit_detail = new AccessibilityDetailNode(required_change_messages, optional_change_messages, element_state);
					audit_details.add(audit_detail);
				}
				else if("dom-size".contentEquals(name)) {
					AuditDetail audit_detail = new DomSize(getStringValue(detail_obj, "statistic"), getStringValue(detail_obj, "value"), (Map)detail_obj.get("element"));
					audit_details.add(audit_detail);
				}
				else if("metrics".contentEquals(name)) {
					AuditDetail audit_detail = new MetricsDetail(getIntegerValue(detail_obj, "firstContentfulPaint"),
																 getLongValue(detail_obj, "observedFirstPaintTs"),
																 getIntegerValue(detail_obj, "speedIndex"),
																 getLongValue(detail_obj, "observedSpeedIndexTs"),
																 getIntegerValue(detail_obj, "observedFirstContentfulPaint"),
																 getLongValue(detail_obj, "observedNavigationStartTs"),
																 getLongValue(detail_obj, "observedLargestContentfulPaintTs"),
																 getIntegerValue(detail_obj, "observedFirstVisualChange"),
																 getLongValue(detail_obj, "observedLoadTs"),
																 getIntegerValue(detail_obj, "firstMeaningfulPaint"),
																 getIntegerValue(detail_obj, "observedTraceEnd"),
																 getIntegerValue(detail_obj, "observedFirstMeaningfulPaint"),
																 getIntegerValue(detail_obj, "firstCPUIdle"),
																 getLongValue(detail_obj, "observedTraceEndTs"),
																 getLongValue(detail_obj, "observedFirstMeaningfulPaintTs"),
																 getIntegerValue(detail_obj, "observedDomContentLoaded"),
																 getLongValue(detail_obj, "observedFirstVisualChangeTs"),
																 getIntegerValue(detail_obj, "interactive"),
																 getIntegerValue(detail_obj, "observedNavigationStart"),
																 getLongValue(detail_obj, "observedFirstContentfulPaintTs"),
																 getLongValue(detail_obj, "observedLastVisualChangeTs"),
																 getIntegerValue(detail_obj, "observedLoad"),
																 getIntegerValue(detail_obj, "observedLargestContentfulPaint"),
																 getLongValue(detail_obj, "observedDomContentLoadedTs"),
																 getIntegerValue(detail_obj, "observedSpeedIndex"),
																 getIntegerValue(detail_obj, "estimatedInputLatency"),
																 getIntegerValue(detail_obj, "totalBlockingTime"),
																 getIntegerValue(detail_obj, "observedFirstPaint"),
																 getIntegerValue(detail_obj, "observedLastVisualChange"),
																 getBooleanValue(detail_obj, "lcpInvalidated"));
					audit_details.add(audit_detail);
				}
				else if("uses-long-cache-ttl".contentEquals(name)) {
					//if debug data is null this might create issues
					AuditDetail audit_detail = new CachingDetail(getStringValue(detail_obj, "url"), 
																 getDoubleValue(detail_obj, "wastedBytes"),
																 getIntegerValue(detail_obj, "totalBytes"),
																 getDoubleValue(detail_obj, "cacheHitProbability"),
																 getLongValue(detail_obj, "cacheLifetimeMs"));
					audit_details.add(audit_detail);
				}
				else if("unminified-css".contentEquals(name) || "unused-css-rules".contentEquals(name) || "offscreen-images".contentEquals(name) || "uses-responsive-images".contentEquals(name)) {
					AuditDetail audit_detail = new AssetSizeOpportunityDetail(detail_obj.get("url").toString(), Integer.parseInt(detail_obj.get("wastedBytes").toString()), Double.parseDouble(detail_obj.get("wastedPercent").toString()), Integer.parseInt(detail_obj.get("totalBytes").toString()));
					audit_details.add(audit_detail);
				}
				else if("third-party-summary".contentEquals(name)) {
					AuditDetail audit_detail = new ThirdPartySummaryDetail(Integer.parseInt(detail_obj.get("transferSize").toString()), 
																		   Double.parseDouble(detail_obj.get("blockingTime").toString()),
																		   Double.parseDouble(detail_obj.get("mainThreadTime").toString()),
																		   (Map)detail_obj.get("entity"));
					audit_details.add(audit_detail);
				}
				else if("network-requests".contentEquals(name)) {
					AuditDetail audit_detail = new NetworkRequestDetail(Integer.parseInt(detail_obj.get("transferSize").toString()), 
																		getStringValue(detail_obj, "url"),
																		getIntegerValue(detail_obj, "statusCode"), 
																		getStringValue(detail_obj, "resourceType"),
																		getStringValue(detail_obj, "mimeType"),
																		getIntegerValue(detail_obj, "resourceSize"),
																		getDoubleValue(detail_obj, "endTime"),
																		getDoubleValue(detail_obj, "startTime"));
					audit_details.add(audit_detail);
				}
				else if("uses-webp-images".contentEquals(name)) {
					AuditDetail audit_detail = new WebPImageDetail(Integer.parseInt(detail_obj.get("wastedBytes").toString()), 
																   detail_obj.get("url").toString(), 
																   Boolean.getBoolean(detail_obj.get("fromProtocol").toString()), 
																   Boolean.getBoolean(detail_obj.get("isCrossOrigin").toString()), 
																   Integer.parseInt(detail_obj.get("totalBytes").toString()));
					audit_details.add(audit_detail);
				}
				else if("diagnostics".contentEquals(name)) {
					AuditDetail audit_detail = new DiagnosticDetail(Integer.parseInt(detail_obj.get("numStylesheets").toString()), 
																	Double.parseDouble(detail_obj.get("throughput").toString()),
																	Integer.parseInt(detail_obj.get("numTasksOver10ms").toString()), 
																	Integer.parseInt(detail_obj.get("numTasksOver25ms").toString()), 
																	Integer.parseInt(detail_obj.get("numTasksOver50ms").toString()), 
																	Integer.parseInt(detail_obj.get("numTasksOver100ms").toString()),
																	Integer.parseInt(detail_obj.get("numTasksOver500ms").toString()), 
																	Integer.parseInt(detail_obj.get("numRequests").toString()),
																	Double.parseDouble(detail_obj.get("totalTaskTime").toString()),
																	Integer.parseInt(detail_obj.get("mainDocumentTransferSize").toString()),
																	Integer.parseInt(detail_obj.get("totalByteWeight").toString()),
																	Integer.parseInt(detail_obj.get("numTasks").toString()),
																	Double.parseDouble(detail_obj.get("rtt").toString()),
																	Double.parseDouble(detail_obj.get("maxRtt").toString()),
																	Integer.parseInt(detail_obj.get("numFonts").toString()),
																	Integer.parseInt(detail_obj.get("numScripts").toString()));
					audit_details.add(audit_detail);
				}
			}
		}
	
		return audit_details;
	}

	private Integer getIntegerValue(ArrayMap<String, Object> detail_obj, String object_key) {
		Integer value = null;
		if(detail_obj.get(object_key) != null) {
			value = Integer.parseInt(detail_obj.get(object_key).toString());
		}
		else {
			value = Integer.MIN_VALUE;
		}
		return value;
	}
	
	private Boolean getBooleanValue(ArrayMap<String, Object> detail_obj, String object_key) {
		Boolean value = null;
		if(detail_obj.get(object_key) != null) {
			value = Boolean.parseBoolean(detail_obj.get(object_key).toString());
		}
		else {
			value = null;
		}
		return value;
	}
	
	private Long getLongValue(ArrayMap<String, Object> detail_obj, String object_key) {
		Long value = null;
		if(detail_obj.get(object_key) != null) {
			value = Long.parseLong(detail_obj.get(object_key).toString());
		}
		else {
			value = Long.MIN_VALUE;
		}
		return value;
	}
	
	private Double getDoubleValue(ArrayMap<String, Object> detail_obj, String object_key) {
		Double value = null;
		if(detail_obj.get(object_key) != null) {
			value = Double.parseDouble(detail_obj.get(object_key).toString());
		}
		else {
			value = Double.MIN_VALUE;
		}
		return value;
	}

	private String getStringValue(ArrayMap<String, Object> detail_obj, String object_key) {
		String mime_type = null;
		if(detail_obj.get(object_key) != null) {
			mime_type = detail_obj.get(object_key).toString();
		}
		
		return mime_type;
	}

	public static InsightType getAuditType(
			LighthouseAuditResultV5 audit_record,
			Map<InsightType, List<String>> audit_ref_map
	) {
		for(InsightType type: audit_ref_map.keySet()) {
			for(String audit_id : audit_ref_map.get(type)) {
				if(audit_record.getId().equals(audit_id)){
					return type;
				}
			}
		}
		
		return InsightType.UNKNOWN;
	}

	private Double convertScore(Object score_obj) {
		Double score = null;
		try {
			score = Double.parseDouble(score_obj.toString());
		}
		catch(Exception e) {
			//e.printStackTrace();
			score = new Double(0);
		}
		
		return score;
	}
	
	
}
