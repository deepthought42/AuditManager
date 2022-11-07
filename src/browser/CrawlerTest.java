package browser;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.looksee.browsing.Browser;
import com.looksee.browsing.Crawler;
import com.looksee.helpers.BrowserConnectionHelper;
import com.looksee.models.ElementState;
import com.looksee.models.enums.BrowserEnvironment;
import com.looksee.models.enums.BrowserType;

public class CrawlerTest {
	//@Test
	public void verifycharacter() {
		String xpath = "(//div/input[contains(@name,\"FNAME\") and contains(@class,\"form-control\") and contains(@id,\"mce-FNAME\")])[1]";
		Pattern p = Pattern.compile("\\((\\/.*)\\)");
		Matcher m = p.matcher(xpath);

	    // if an occurrence if a pattern was found in a given string...
	    if (m.find()) {
	        // ...then you can use group() methods.
	        xpath = m.group(1); // whole matched expression
	    }
		assertTrue(xpath.equals("//div/input[contains(@name,\"FNAME\") and contains(@class,\"form-control\") and contains(@id,\"mce-FNAME\")]"));
		
	}
	
	
	//@Test
	public void verifyClickCoordinateGenerationForParentElement() throws MalformedURLException{
		boolean error = false;
		do{
			Browser browser = null;
			try{
				browser = BrowserConnectionHelper.getConnection(BrowserType.CHROME, BrowserEnvironment.DISCOVERY);
				System.err.println("navigating to url");
				browser.navigateTo("https://staging-marketing.qanairy.com");
				
				System.err.println("finding parent element");
				WebElement web_element = browser.getDriver().findElement(By.xpath("//nav//div[contains(@class,\"navbar-collapse\")]"));
				System.err.println("finding child element");
				WebElement child_web_element = browser.getDriver().findElement(By.xpath("//div//ul[contains(@class,'navbar-nav')]"));
				
				System.err.println("generating location");
				ElementState child_element = new ElementState();
				child_element.setXLocation(child_web_element.getLocation().getX());
				child_element.setYLocation(child_web_element.getLocation().getY());
				child_element.setWidth(child_web_element.getSize().getWidth());
				child_element.setHeight(child_web_element.getSize().getHeight());
				System.err.println("gnerating actual click location");
				Point coord = Crawler.generateRandomLocationWithinElementButNotWithinChildElements(web_element, child_element);
				
				System.err.println("child coordinate  :   "+child_element.getXLocation() + "  ,  "+child_element.getYLocation());
				System.err.println("child size  :    "+child_element.getWidth() + "  ,  "+child_element.getHeight());
				System.err.println("coordinate :: " + coord.getX() + "  ,  " + coord.getY());
				error = false;
				break;
			}catch(Exception e){
				if(browser != null){
					browser.close();
				}
				error = true;
			}
		}while(error);
	}
}
