package org.vpac.grisu.clients.gridFtpTests;

import java.util.Date;

public class GridFtpActionItem {

	private final GridFtpAction action;
	private final String source;
	private final String target;
	private final String runId;

	private boolean executed = false;

	private Date startDate;

	private Date endDate;
	private Exception possibleException;

	private boolean success = true;
	public GridFtpActionItem(String runId, GridFtpAction action, String source,
			String target) {
		this.runId = runId;
		this.action = action;
		this.source = source;
		this.target = target;
	}

	public Thread createActionThread() {
		Thread thread = new Thread() {
			public void run() {
				executed = true;
				System.out.println("Starting: "
						+ GridFtpActionItem.this.toString());
				action.executeAction(GridFtpActionItem.this, source, target);
				System.out.println("Finished: "
						+ GridFtpActionItem.this.toString());
				if (isSuccess()) {
					System.out.println("Success.");
				} else {
					action.getController().addFailedTestRunId(runId);
					System.out.println("Failed: "
							+ getPossibleException().getLocalizedMessage());
				}
			}
		};
		return thread;
	}

	public GridFtpAction getAction() {
		return action;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Exception getPossibleException() {
		return possibleException;
	}

	public String getResult(boolean showFullException, boolean shortVersion) {
		StringBuilder result = new StringBuilder();
		if (!shortVersion) {
			result.append("---------------------------------\n");
			result.append("Testaction:\t" + getAction().getName() + "\n");
			result.append("Source:\t" + getSource() + "\n");
			result.append("Target:\t" + getTarget() + "\n");
			result.append("Duration:\t" + getStartDate() + " - " + getEndDate()
					+ "\n");
			result.append("Executed:\t" + isExecuted() + "\n");
		}
		if (isExecuted()) {
			result.append("Success:\t" + isSuccess() + "\n");
			if (getPossibleException() != null) {
				if (showFullException) {
					result.append("Exception: "
							+ Utils.fromException(getPossibleException())
							+ "\n");
				} else {
					result.append("Exception: "
							+ getPossibleException().getLocalizedMessage()
							+ "\n");
					if (getPossibleException().getCause() != null) {
						result.append("Cause: "
								+ getPossibleException().getCause()
										.getLocalizedMessage() + "\n");
					}
				}
			}
		}
		result.append("-----------------------------------\n\n");

		return result.toString();
	}

	public String getSource() {
		return source;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTarget() {
		return target;
	}

	public boolean isExecuted() {
		return executed;
	}

	public boolean isSuccess() {
		return success;
	}

	public String runId() {
		return runId;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setException(Exception e) {
		this.possibleException = e;
		success = false;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		return getAction() + ": " + getSource() + " " + getTarget();
	}
}
