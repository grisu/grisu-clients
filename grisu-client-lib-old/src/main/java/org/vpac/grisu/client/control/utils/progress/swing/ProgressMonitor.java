package org.vpac.grisu.client.control.utils.progress.swing;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MySwing: Advanced Swing Utilites Copyright (C) 2005 Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
public class ProgressMonitor {
	int total, current = -1;
	boolean indeterminate;
	int milliSecondsToWait = 1000; // half second
	String status;

	private Vector listeners = new Vector();

	private ChangeEvent ce = new ChangeEvent(this);

	public ProgressMonitor(int total, boolean indeterminate) {
		this.total = total;
		this.indeterminate = indeterminate;
	}

	public ProgressMonitor(int total, boolean indeterminate,
			int milliSecondsToWait) {
		this.total = total;
		this.indeterminate = indeterminate;
		this.milliSecondsToWait = milliSecondsToWait;
	}

	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	private void fireChangeEvent() {

		Vector targets;
		synchronized (this) {
			targets = (Vector) listeners.clone();
		}

		Iterator iter = targets.iterator();
		while (iter.hasNext()) {
			((ChangeListener) iter.next()).stateChanged(ce);
		}
	}

	public int getCurrent() {
		return current;
	}

	public int getMilliSecondsToWait() {
		return milliSecondsToWait;
	}

	public String getStatus() {
		return status;
	}

	/*--------------------------------[ ListenerSupport ]--------------------------------*/

	public int getTotal() {
		return total;
	}
	public boolean isIndeterminate() {
		return indeterminate;
	}

	public void removeChangeListener(ChangeListener listener) {
		listeners.remove(listener);
	}

	public void setCurrent(String status, int current) {
		if (current == -1)
			throw new IllegalStateException("not started yet");
		this.current = current;
		if (status != null)
			this.status = status;
		fireChangeEvent();
	}

	public void start(String status) {
		if (current != -1)
			throw new IllegalStateException("not started yet");
		this.status = status;
		current = 0;
		fireChangeEvent();
	}
}
