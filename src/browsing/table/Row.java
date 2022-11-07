package com.looksee.browsing.table;

import java.util.List;

import com.looksee.browsing.ElementNode;
import com.looksee.models.Element;

/**
 *	Contains the {@link ElementNode}s that make up a row in a [@link Table} within a {@link PageVersion}. 
 */
public class Row {
	private List<ElementNode<Element>> row_cells;
	
	public Row(){}
	
	public Row(List<ElementNode<Element>> table_row){
		this.setRowCells(table_row);
	}

	public List<ElementNode<Element>> getRowCells() {
		return row_cells;
	}

	public void setRowCells(List<ElementNode<Element>> row_cells) {
		this.row_cells = row_cells;
	}
}
