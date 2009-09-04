package org.vpac.grisu.clients.gridTests;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

public class GridTestStage {

	private final String name;
	private GridTestStageStatus status = GridTestStageStatus.INITIALIZED;
	private Exception possibleException;
	
	protected SortedMap<Date, String> messages = new TreeMap<Date, String>(); 
	
	public SortedMap<Date, String> getMessages() {
		return messages;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	private Date beginDate;
	private Date endDate;
	
	public GridTestStage(String name) {
		this.beginDate = new Date();
		this.name = name;
	}
	
	private void setStageFinished() {
		this.endDate = new Date();
	}
	
	public GridTestStageStatus getStatus() {
		return status;
	}
	
	public boolean wasSuccessful() {
		if ( this.status.equals(GridTestStageStatus.FINISHED_SUCCESS) ) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isRunning() {
		if ( this.status.equals(GridTestStageStatus.RUNNING) ) {
			return true;
		} else {
			return false;
		}
	}

	public void setStatus(GridTestStageStatus status) {
		this.status = status;
		
		if ( this.status.equals(GridTestStageStatus.FINISHED_ERROR) || this.status.equals(GridTestStageStatus.FINISHED_SUCCESS) || this.status.equals(GridTestStageStatus.NOT_EXECUTED)) {
			setStageFinished();
		}
	}

	public Exception getPossibleException() {
		return possibleException;
	}

	public void setPossibleException(Exception possibleException) {
		this.possibleException = possibleException;
	}

	public String getName() {
		return name;
	}
	
	public synchronized void addMessage(String message) {
		Date now = new Date();
		while ( messages.get(now) != null ) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			now = new Date();
		}
		messages.put(new Date(), message);
	}
	
	public void printMessages() {
		for ( Date date : messages.keySet() ) {
			System.out.println(date.toString()+": "+messages.get(date));
		}
	}
	
	public String getMessagesString() {
		StringBuffer result = new StringBuffer();
		for (Date date : messages.keySet() ) {
			result.append(date.toString()+": "+messages.get(date)+"\n");
		}
		return result.toString();
	}
	

}
