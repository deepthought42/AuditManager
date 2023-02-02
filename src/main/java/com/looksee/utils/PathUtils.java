package com.looksee.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.looksee.auditManager.models.LookseeObject;
import com.looksee.auditManager.models.PageState;


public class PathUtils {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PathUtils.class);

	/**
	 * Retrieves the last {@link PageState} in the given list of {@link LookseeObject}s
	 * 
	 * @param pathObjects list of {@link LookseeObject}s in sequential order
	 * 
	 * @return last page state in list
	 * 
	 * @pre pathObjects != null
	 */
	public static PageState getLastPageStateOLD(List<LookseeObject> path_objects) {
		assert(path_objects != null);
				
		for(int idx = path_objects.size()-1; idx >= 0; idx--){
			if(path_objects.get(idx).getKey().contains("page")){
				return (PageState)path_objects.get(idx);
			}
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * 
	 * @param path_keys
	 * @return
	 */
	public static int getIndexOfLastElementState(List<String> path_keys){
		for(int element_idx=path_keys.size()-1; element_idx >= 0; element_idx--){
			if(path_keys.get(element_idx).contains("elementstate")){
				return element_idx;
			}
		}

		return -1;
	}

	/**
	 * 
	 * @param path_keys
	 * @param path_objects
	 * @return
	 */
	public static List<LookseeObject> orderPathObjects(List<String> path_keys, List<LookseeObject> path_objects) {
		List<LookseeObject> ordered_path_objects = new ArrayList<>();
		List<String> temp_path_keys = new ArrayList<>(path_keys);
		//Ensure Order path objects
		for(String path_obj_key : temp_path_keys){
			for(LookseeObject obj : path_objects){
				if(obj.getKey().equals(path_obj_key)){
					ordered_path_objects.add(obj);
				}
			}
		}

		LookseeObject last_path_obj = null;
		List<LookseeObject> reduced_path_obj = new ArrayList<>();
		//scrub path objects for duplicates
		for(LookseeObject obj : ordered_path_objects){
			if(last_path_obj == null || !obj.getKey().equals(last_path_obj.getKey())){
				last_path_obj = obj;
				reduced_path_obj.add(obj);
			}
		}

		return reduced_path_obj;
	}

	public static List<LookseeObject> reducePathObjects(List<LookseeObject> ordered_path_objects) {
		//scrub path objects for duplicates
		List<LookseeObject> reduced_path_objs = new ArrayList<>();
		LookseeObject last_path_obj = null;
		for(LookseeObject obj : ordered_path_objects){
			if(last_path_obj == null || !obj.getKey().equals(last_path_obj.getKey())){
				last_path_obj = obj;
				reduced_path_objs.add(obj);
			}
		}
				
		return reduced_path_objs;
	}

	public static PageState getFirstPage(List<LookseeObject> ordered_path_objects) {
		//find first page
		for(LookseeObject obj : ordered_path_objects){
			if(obj instanceof PageState){
				return ((PageState)obj);
			}
		}
		
		return null;
	}

	public static PageState getSecondToLastPageStateOLD(List<LookseeObject> path_objects) {
		assert(path_objects != null);
		
		int page_states_seen = 0;
		log.warn("path objects length while getting second to last page state ;: "+path_objects.size());
		for(int idx = path_objects.size()-1; idx >=0; idx--){
			if(path_objects.get(idx).getKey().contains("pagestate")){
				if(page_states_seen >= 1){
					return (PageState)path_objects.get(idx);
				}
				page_states_seen++;
			}
		}
		
		return null;
	}

	public static List<String> reducePathKeys(List<String> final_key_list) {
		//scrub path objects for duplicates
		List<String> reduced_path_keys = new ArrayList<>();
		String last_path_key = null;
		for(String key : final_key_list){
			if(!key.equals(last_path_key)){
				last_path_key = key;
				reduced_path_keys.add(key);
			}
		}
		return reduced_path_keys;
	}

}
