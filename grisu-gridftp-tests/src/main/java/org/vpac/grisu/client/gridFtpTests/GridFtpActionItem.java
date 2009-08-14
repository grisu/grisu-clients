package org.vpac.grisu.client.gridFtpTests;

import java.util.Date;

public class GridFtpActionItem {
	
	private final GridFtpAction action;
	private final String source;
	private final String target;
	private final String runId;
	
	private boolean executed = false;
	
	public boolean isExecuted() {
		return executed;
	}

	private Date startDate;
	private Date endDate;
	
	private Exception possibleException;
	private boolean success = true;
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public GridFtpAction getAction() {
		return action;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public GridFtpActionItem(String runId, GridFtpAction action, String source, String target) {
		this.runId = runId;
		this.action = action;
		this.source = source;
		this.target = target;
	}
	
	public Thread createActionThread() {
		Thread thread = new Thread() {
			public void run() {
				executed = true;
				System.out.println("Starting: "+GridFtpActionItem.this.toString());
				action.executeAction(GridFtpActionItem.this, source, target);
				System.out.println("Finished: "+GridFtpActionItem.this.toString());
				if ( isSuccess() ) {
					System.out.println("Success.");
				} else {
					action.getController().addFailedTestRunId(runId);
					System.out.println("Failed: "+getPossibleException().getLocalizedMessage());
				}
			}
		};
		return thread;
	}
	
	@Override
	public String toString() {
		return getAction()+": "+getSource()+" " + getTarget();
	}
	
	public void setException(Exception e) {
		this.possibleException = e;
		success = false;
	}
	
	public Exception getPossibleException() {
		return possibleException;
	}

	public boolean isSuccess() {
		return success;
	}
	
	public String runId() {
		return runId;
	}

	public String getResult(boolean showFullException) {
		StringBuilder result = new StringBuilder();
		result.append("---------------------------------\n");
		result.append("Testaction:\t"
				+ getAction().getName() + "\n");
		result.append("Source:\t" + getSource() + "\n");
		result.append("Target:\t" + getTarget() + "\n");
		result.append("Duration:\t" + getStartDate()
				+ " - " + getEndDate() + "\n");
		result.append("Executed:\t" + isExecuted() + "\n");
		if (isExecuted()) {
			result.append("Success:\t" + isSuccess()
					+ "\n");
			if (getPossibleException() != null) {
				if ( showFullException ) {
					result.append("Exception: "+Utils.fromException(getPossibleException())+"\n");
				} else {
					result.append("Exception: "
						+ getPossibleException()
								.getLocalizedMessage() + "\n");
					if ( getPossibleException().getCause() != null ) {
						result.append("Cause: "+getPossibleException().getCause().getLocalizedMessage()+"\n");
					}
				}
			}
		}
		result.append("-----------------------------------\n\n");
		
		return result.toString();
	}
}
