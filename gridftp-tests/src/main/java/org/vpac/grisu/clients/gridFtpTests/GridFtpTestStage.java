package org.vpac.grisu.clients.gridFtpTests;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

public class GridFtpTestStage {

	private final String name;
	private GridFtpTestStageStatus status = GridFtpTestStageStatus.INITIALIZED;
	private Exception possibleException;

	protected SortedMap<Date, String> messages = new TreeMap<Date, String>();

	private Date beginDate;

	private Date endDate;

	public GridFtpTestStage(String name) {
		this.beginDate = new Date();
		this.name = name;
	}

	public synchronized void addMessage(String message) {
		Date now = new Date();
		while (messages.get(now) != null) {
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
	public Date getBeginDate() {
		return beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public SortedMap<Date, String> getMessages() {
		return messages;
	}

	public String getMessagesString() {
		StringBuffer result = new StringBuffer();
		for (Date date : messages.keySet()) {
			result.append(date.toString() + ": " + messages.get(date) + "\n");
		}
		return result.toString();
	}

	public String getName() {
		return name;
	}

	public Exception getPossibleException() {
		return possibleException;
	}

	public GridFtpTestStageStatus getStatus() {
		return status;
	}

	public boolean isRunning() {
		if (this.status.equals(GridFtpTestStageStatus.RUNNING)) {
			return true;
		} else {
			return false;
		}
	}

	public void printMessages() {
		for (Date date : messages.keySet()) {
			System.out.println(date.toString() + ": " + messages.get(date));
		}
	}

	public void setPossibleException(Exception possibleException) {
		this.possibleException = possibleException;
	}

	private void setStageFinished() {
		this.endDate = new Date();
	}

	public void setStatus(GridFtpTestStageStatus status) {
		this.status = status;

		if (this.status.equals(GridFtpTestStageStatus.FINISHED_ERROR)
				|| this.status.equals(GridFtpTestStageStatus.FINISHED_SUCCESS)
				|| this.status.equals(GridFtpTestStageStatus.NOT_EXECUTED)) {
			setStageFinished();
		}
	}

	public boolean wasSuccessful() {
		if (this.status.equals(GridFtpTestStageStatus.FINISHED_SUCCESS)) {
			return true;
		} else {
			return false;
		}
	}

}
