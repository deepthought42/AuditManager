package com.looksee.helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.looksee.browsing.Browser;
import com.looksee.models.enums.BrowserEnvironment;
import com.looksee.models.enums.BrowserType;

import io.github.resilience4j.retry.annotation.Retry;

@Retry(name="webdriver")
public class BrowserConnectionHelper {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(BrowserConnectionHelper.class);

	//GOOGLE CLOUD CLUSTER
	//private static final String[] CHROME_DISCOVERY_HUB_IP_ADDRESS = {"35.239.77.58:4444", "23.251.149.198:4444"};
	//private static final String[] FIREFOX_DISCOVERY_HUB_IP_ADDRESS = {"35.239.245.6:4444", "173.255.118.118:4444"};

	private static final String[] RESOURCE_HEAVY_REQUEST_HUB_IP_ADDRESS = {"34.121.191.15:4444"};
    
	/*
	private static final String[] RESOURCE_HEAVY_REQUEST_HUB_IP_ADDRESS = {"35.224.152.230:4444",
    																	   "34.121.191.15:4444",
    																	   "34.70.80.131:4444"};
    */
    private static final String RAPID_REQUEST_HUB_IP_ADDRESS = "34.121.191.15:4444";

	// PRODUCTION HUB ADDRESS
	//private static final String HUB_IP_ADDRESS= "142.93.192.184:4444";

	//STAGING HUB ADDRESS
	//private static final String HUB_IP_ADDRESS="159.89.226.116:4444";

	/**
	 * Creates a {@linkplain WebDriver} connection
	 * 
	 * @param browser
	 * @param environment
	 * 
	 * @return
	 * 
	 * @pre browser != null
	 * @pre environment != null
	 * 
	 * @throws MalformedURLException
	 */
    @Retry(name="webdriver")
	public static Browser getConnection(BrowserType browser, BrowserEnvironment environment) throws MalformedURLException{
		assert browser != null;
		assert environment != null;
		
		URL hub_url = null;
		if(environment.equals(BrowserEnvironment.TEST)){
			hub_url = new URL( "http://"+RAPID_REQUEST_HUB_IP_ADDRESS+"/wd/hub" );
		}
		else if(environment.equals(BrowserEnvironment.DISCOVERY) && "chrome".equalsIgnoreCase(browser.toString())){
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(RESOURCE_HEAVY_REQUEST_HUB_IP_ADDRESS.length);
			hub_url = new URL( "http://"+RESOURCE_HEAVY_REQUEST_HUB_IP_ADDRESS[randomInt]+"/wd/hub");
		}
		else if(environment.equals(BrowserEnvironment.DISCOVERY) && "firefox".equalsIgnoreCase(browser.toString())){
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(RESOURCE_HEAVY_REQUEST_HUB_IP_ADDRESS.length);
			hub_url = new URL( "http://"+RESOURCE_HEAVY_REQUEST_HUB_IP_ADDRESS[randomInt]+"/wd/hub");
		}

		return new Browser(browser.toString(), hub_url);
	}
}
