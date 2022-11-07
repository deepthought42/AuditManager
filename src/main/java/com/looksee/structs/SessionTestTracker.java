package com.looksee.structs;

import java.util.HashMap;
import java.util.Map;


/**
 * Tracks element action sequences by session
 * 
 * @author Brandon Kindred
 *
 */
public class SessionTestTracker {
	private Map<String, TestMapper> sessionSequences;
	private static SessionTestTracker instance = null;

	/**
	 * 
	 */
	protected SessionTestTracker() {
		sessionSequences = new HashMap<String, TestMapper>();
	}
	
	/**
	 * 
	 * @return
	 */
    public TestMapper getSequencesForSession(String session_key){
	   return this.sessionSequences.get(session_key);
    }
   
    /**
	 * Add session to tracker with empty hash map for element actions
	 */
    public void addSessionSequences(String session_key){
    	this.sessionSequences.put(session_key, new TestMapper());
    }
    
	/**
	 * @return singleton instance of session sequence tracker
	 */
	public static SessionTestTracker getInstance() {
      if(instance == null) {
         instance = new SessionTestTracker();
      }
      return instance;
   }	   
}
