package com.looksee.structs;

import java.util.HashMap;
import java.util.Map;

import com.looksee.models.Test;


/**
 * Holds elements in a map, that is keyed by element action sequence and content. 
 *
 */
public class TestMapper {
	private Map<String, Test> testHash;	
	
	/**
	 * Creates a new instance of the tracker
	 */
	public TestMapper(){
		testHash = new HashMap<String, Test>();
	}
	
	/**
	 * Adds a new entry to the element action path
	 * 
	 * @param elem
	 * @param action
	 */
	public void addTest(Test test){
		int hash_code = test.hashCode();
		
		if(!this.testHash.containsKey(Integer.toString(hash_code))){
			this.testHash.put(Integer.toString(hash_code), test);
		}
	}
	
	/**
	 * Checks if element action sequence exists.
	 * 
	 * @param elem
	 * @param action
	 * @return
	 */
	public boolean containsTest(Test test){
		String hash_key = Integer.toString(test.hashCode());
		return  this.testHash.containsKey(hash_key);
	}
	
	/** 
	 * @return hash of element action sequences
	 */
	public Map<String, Test> getTestHash(){
		return testHash;
	}
}
