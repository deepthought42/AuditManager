package com.looksee.models;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A set of vertex objects that form a sequential movement through a graph
 */
public class ExploratoryPath {
	private static Logger log = LoggerFactory.getLogger(ExploratoryPath.class);
	
	private List<String> path_keys;
	private List<LookseeObject> path_objects;
	private List<ActionOLD> possible_actions;

	/**
	 * Creates new instance of path setting it to the given path
	 * 
	 * @param current_path
	 */
	@Deprecated
	public ExploratoryPath(List<String> path_keys, List<LookseeObject> current_path, List<ActionOLD> actions){
		setPathKeys(path_keys);
		setPathObjects(current_path);
		setPossibleActions(actions);
	}

	/**
	 * Creates new instance of path setting it to the given path
	 * 
	 * @param current_path
	 */
	public ExploratoryPath(List<String> path_keys, List<LookseeObject> current_path){
		setPathKeys(path_keys);
		setPathObjects(current_path);
	}
		
	/**
	 * Adds an object to path and sets whether or not this path spans multiple domains
	 * 
	 * @param obj
	 * @return
	 */
	public boolean add(LookseeObject obj){
		return getPathObjects().add(obj);
	}
	
	public int size(){
		return getPathObjects().size();
	}
	
	/**
	 * Gets the last Vertex in a path that is of type {@link PageState}
	 * 
	 * @return
	 */
	public PageState findLastPage(){
		List<LookseeObject> path_obj_list = getPathObjects();
		PageState page = null;

		for(LookseeObject obj : path_obj_list){
			if(obj instanceof PageState){
				page = (PageState)obj;
			}
		}

		return page;
	}
	
	/**
	 * Checks if the path has 2 pages that are the equal
	 * 
	 * @param path
	 * 
	 * @pre path_key_list != null
	 * @pre !path_key_list.isEmpty()
	 * @pre page != null
	 * 
	 * @return true if sequence appears more than once
	 */
	public static boolean hasCycle(List<PageState> path_objects, PageState page, boolean isSinglePage){
		assert page != null;
	
		if(isSinglePage){
			return false;
		}
		
		//extract all pages
		//iterate through pages to see if any match
		log.info("Checking if exploratory path has a cycle");
		for(PageState path_obj : path_objects){
			if(path_obj.equals(page)){
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Checks if the path has the same page more than once. 
	 * 
	 * @param path
	 * @return true if sequence appears more than once
	 */
	public static boolean hasPageCycle(List<LookseeObject> path){
		for(int i = path.size()-1; i > 0; i--){
			for(int j = i-1; j>= 0; j--){
				if(path.get(i) instanceof PageState 
						&& path.get(j) instanceof PageState
						&& path.get(i).equals(path.get(j)))
				{
					return true;
				}
			}			
		}
		return false;
	}

	public PageState firstPage() {
		
		for(LookseeObject obj : getPathObjects()){
			if(obj instanceof PageState){
				return (PageState)obj;
			}
		}
		return null;
	}

	/**
	 * Adds an object to path
	 * 
	 * @param obj
	 * @return
	 */
	public boolean addToPathKeys(String key){
		return this.getPathKeys().add(key);
	}
	
	public void addPathObject(LookseeObject path_obj) {
		this.path_objects.add(path_obj);
	}

	public List<LookseeObject> getPathObjects() {
		return this.path_objects;
	}

	public void setPathObjects(List<LookseeObject> path_objects) {
		this.path_objects = path_objects;
	}
	
	public List<ActionOLD> getPossibleActions() {
		return possible_actions;
	}

	public void setPossibleActions(List<ActionOLD> possible_actions) {
		this.possible_actions = possible_actions;
	}

	public List<String> getPathKeys() {
		return path_keys;
	}

	public void setPathKeys(List<String> path_keys) {
		this.path_keys = path_keys;
	}
	
	/**
	 * Clone {@link Path} object
	 * 
	 * @param path
	 * @return
	 */
	public static ExploratoryPath clone(ExploratoryPath path){		
		List<LookseeObject> path_objects = new ArrayList<LookseeObject>(path.getPathObjects());
		List<String> path_keys = new ArrayList<String>(path.getPathKeys());
		
		return new ExploratoryPath(path_keys, path_objects);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof ExploratoryPath){
			ExploratoryPath path = (ExploratoryPath)o;
			List<LookseeObject> comparator_nodes = new ArrayList<LookseeObject>(path.getPathObjects());
			for(LookseeObject obj : getPathObjects()){
				int idx = 0;
				for(LookseeObject comparator_obj : path.getPathObjects()){
					if(comparator_obj.equals(obj)){
						comparator_nodes.remove(idx);
						break;
					}
					idx++;
				}
			}
			return comparator_nodes.isEmpty();
		}
		return false;
	}
}


