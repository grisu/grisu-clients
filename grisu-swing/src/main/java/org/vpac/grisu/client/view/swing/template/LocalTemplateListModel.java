package org.vpac.grisu.client.view.swing.template;

import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import org.apache.commons.collections.list.TreeList;

public class LocalTemplateListModel extends AbstractListModel {
	
	private Set<String> localTemplates = new TreeSet<String>();

	public Object getElementAt(int index) {

	
		return new TreeList(localTemplates).get(index);
	}

	public int getSize() {
		return localTemplates.size();
	}
	
	public void addElement(String item) {
		localTemplates.add(item);
		fireContentsChanged(this, 0, getSize());
	}

	public void removeElement(String item) {
		localTemplates.remove(item);
		fireContentsChanged(this, 0, getSize());
	}
	
	public int size() {
		return getSize();
	}
	
	public String get(int index) {
		return (String)getElementAt(index);
	}
	
}
