package com.looksee.models;


import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.looksee.models.enums.AlertChoice;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;

/**
 * Represents an Alert or Confirmation pop-up that is triggered by javascript within the page
 * 
 */
public class PageAlert extends LookseeObject {
	private static Logger log = LoggerFactory.getLogger(PageAlert.class);

	private String message;
	
	/**
	 * 
	 * @param page
	 * @param alertChoice
	 * @pre page!=null
	 * @pre alertChoice != null
	 * @pre {"accept","reject"}.contains(alertChoice)
	 * @pre message != null;
	 */
	public PageAlert(String message){
		this.message = message;
		this.setKey(generateKey());
	}
	
	/**
	 * 
	 * @param driver
	 * @param choice 
	 */
	public void performChoice(WebDriver driver, AlertChoice choice){
		try{
			Alert alert = driver.switchTo().alert();
			if(AlertChoice.ACCEPT.equals(choice)){
				alert.accept();
			}
			else{
				alert.dismiss();
			}
		}
		catch(NoAlertPresentException nae){
			log.warn( "Alert not present");
		}
	}
	
	public String getMessage() throws UnhandledAlertException{
		return this.message; 
	}
	
	public static String getMessage(Alert alert) throws UnhandledAlertException{
		return alert.getText(); 
	}
	
	@Override
	public boolean equals(Object o){
		if (this == o) return true;
        if (!(o instanceof PageAlert)) return false;
        
        PageAlert that = (PageAlert)o;
        
        return this.message.equals(that.getMessage());
   	}

	@Override
	public PageAlert clone() {
		return new PageAlert(this.getMessage());
	}

	@Override
	public String generateKey() {
		return "alert"+org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.getMessage());
	}
}
