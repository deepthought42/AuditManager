package com.looksee.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.looksee.models.ActionOLD;

/**
 * Contains the Order of operations for actions. A lower value indicates that
 *   an action has less precedence.
 *
 */
public class ActionHelper {
	public static HashMap<String, Integer> actionOrderOfOperationsMap 
		= new HashMap<String, Integer>();
	
	private static List<List<ActionOLD>> action_lists = new ArrayList<List<ActionOLD>>();
	
	/*
	 * ACTION POLICIES
	 * <<<<<<<<<<<<>>>>>>>>>>>>>>>
	 * 
	 * Mouse over
	 * --------------------
	 * Click
	 * clickAt
	 * click and wait
	 * clickAtAndWait
	 * Click and hold
	 * Release
	 * Double Click
	 * -------------------------
	 * keypresses - all same policy
	 * --------------------
	 * drag and drop
	 * -------------------------
	 */
	static {
		List<ActionOLD> mouse_motion_actions = new ArrayList<ActionOLD>();
		//mouse_motion_actions.add(new Action("mouseover"));
		//mouse_motion_actions.add(new Action("scroll","100"));
		
		List<ActionOLD> click_actions = new ArrayList<ActionOLD>();
		//click_actions.add(new Action("clickAndHold"));
		click_actions.add(new ActionOLD("click"));
		//click_actions.add(new Action("clickAt"));
		//click_actions.add(new Action("clickAndWait"));
		//click_actions.add(new Action("clickAtAndWait"));
		//click_actions.add(new Action("release"));
		//click_actions.add(new Action("doubleClick"));
		
		//List<Action> keyboard_actions = new ArrayList<Action>();
		//keyboard_actions.add(new Action("sendKeys"));
		
		action_lists.add(mouse_motion_actions);
		action_lists.add(click_actions);
		//action_lists.add(keyboard_actions);
	}
	
	public static Integer getOrderOfOperationForAction(String actionName){
		return actionOrderOfOperationsMap.get(actionName);
	}

	public static List<List<ActionOLD>> getActionLists() {
		return action_lists;
	}
}
