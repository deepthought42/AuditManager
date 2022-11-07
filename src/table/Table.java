package com.looksee.browsing.table;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 
 */
public class Table {
	public List<Row> headers;
	public List<Row> table_rows;
	
	public Table(){}
	
	/**
	 * Retrieves all cells in a table header passed
	 * 
	 * @param table_header header {@link WebElement}
	 *  
	 * @return List of {@link Row}s for table header
	 * 
	 * @pre table_header != null
	 */
	public List<Row> loadHeaders(WebElement table_header, WebDriver driver){
		assert table_header != null;
				
		List<WebElement> header_rows = table_header.findElements(By.xpath("./tr"));
		List<Row> rows = new ArrayList<Row>();
		/*
		int cnt = 1;
		for(WebElement header_row : header_rows){
			List<WebElement> header_cells = new ArrayList<WebElement>();

			header_cells.addAll(header_row.findElements(By.xpath("./td")));
			header_cells.addAll(header_row.findElements(By.xpath("./th")));
			
			Row row = new Row();
			String row_xpath = "//tr["+ cnt + "]";
			for(WebElement elem : header_cells){
				ElementState page_elem = new ElementState(elem.getText(), browser.generateXpath(elem, row_xpath, null, driver), elem.getTagName(), extractAttributes(elem, (JavascriptExecutor)driver), Browser.loadCssProperties(elem) );
				ElementNode<ElementState> node = new ElementNode<ElementState>(page_elem);
				//load all child elements into tree
				List<WebElement> children = Browser.getChildElements(elem);
				for(WebElement child_elem : children){
					ElementState child_page_elem = new ElementState(child_elem.getText(), Browser.generateXpath(child_elem, row_xpath, null, driver), child_elem.getTagName(), Browser.extractAttributes(child_elem, (JavascriptExecutor)driver), Browser.loadCssProperties(child_elem));
					node.addChild(child_page_elem);
				}
				
				row.getRowCells().add(node);
			}
			cnt++;
		}
		*/
		return rows;
	}
	
	public List<Row> loadRows(WebElement table_body){
		return null;
	}
}
