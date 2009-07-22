package org.vpac.grisu.client.control.files;

public class FileTransferEvent {

	public static final int TRANSFER_STARTED = 0;
	public static final int TRANSFER_PROGRESS_CHANGED = 1;
	public static final int TRANSFER_FINISHED_EITHER_WAY = 10;
	public static final int TRANSFER_CANCELLED = 11;
	public static final int TRANSFER_FAILED = 12;
	public static final int TRANSFER_FINISHED = 13;

	private int type = -1;
	private Exception exception = null;
	private FileTransfer transfer = null;
	
	public FileTransferEvent(FileTransfer transfer, int type) {
		this.transfer = transfer;
		this.type = type;
	}
	
	/**
	 * Use this if the transfer failed
	 * @param e the reason why the transfer failed
	 */
	public FileTransferEvent(FileTransfer transfer, Exception e) {
		this.transfer = transfer;
		this.type = TRANSFER_FAILED;
		this.exception = e;
	}
	
	public FileTransfer getTransfer() {
		return transfer;
	}
	
	public int getType() {
		return this.type;
	}
	
	public Exception getReasonFailed() {
		return exception;
	}

}
