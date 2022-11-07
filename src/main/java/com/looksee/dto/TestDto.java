package com.looksee.dto;

import java.util.ArrayList;
import java.util.List;

import com.looksee.models.ActionOLD;
import com.looksee.models.Element;
import com.looksee.models.LookseeObject;
import com.looksee.models.PageState;
import com.looksee.models.Test;

/**
 * Data transfer object for {@link Test} object that is designed to comply with
 * the data format for browser extensions
 */
public class TestDto {
	private String key;
	private String name;
	private List<Object> path;

	public TestDto(){}

	public TestDto(Test test){
		setKey(test.getKey());
		setName(test.getName());

		this.path = new ArrayList<Object>();

		List<LookseeObject> path_objects = test.getPathObjects();
		List<LookseeObject> ordered_path_objects = new ArrayList<LookseeObject>();
		//order by key
		for(String key : test.getPathKeys()){
			for(LookseeObject obj : path_objects){
				if(obj.getKey().equals(key)){
					ordered_path_objects.add(obj);
				}
			}
		}

		boolean first_page = true;
		for(int idx = 0; idx < ordered_path_objects.size(); idx++){
			if(ordered_path_objects.get(idx) instanceof PageState && first_page){
				first_page = false;
				this.path.add(new PageStateDto((PageState)ordered_path_objects.get(idx)));
			}
			else if(ordered_path_objects.get(idx) instanceof Element ){
				this.path.add(new ElementActionDto((Element)ordered_path_objects.get(idx), (ActionOLD)ordered_path_objects.get(++idx)));
			}
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Object> getPath() {
		return path;
	}

	public void setPath(List<Object> path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
