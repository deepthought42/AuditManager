package com.looksee.structs;

import java.util.ArrayList;
import java.util.List;


public class TreeNode<E> {
	private E root;
	private List<TreeNode<E>> child_nodes;
	
	public TreeNode(E root){
		this.root = root;
		this.child_nodes = new ArrayList<TreeNode<E>>();
	}
	
	public E getRoot(){
		return this.root;
	}
	
	public boolean addChildNode(TreeNode<E> child_node){
		return this.child_nodes.add(child_node);
	}
	
	public List<TreeNode<E>> getChildNodes(){
		return this.child_nodes;
	}
	
	public void addChildNodes(List<TreeNode<E>> list){
		this.child_nodes.addAll(list);
	}
}
