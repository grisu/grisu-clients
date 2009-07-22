package org.vpac.grisu.client.control.files;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.files.GrisuFileObject;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class FileTransferManager implements FileTransferListener {
	
	static final Logger myLogger = Logger.getLogger(FileTransferManager.class
			.getName());
	
	private EventList<FileTransfer> transfers = new BasicEventList<FileTransfer>();
	
	public FileTransfer addTransfer(GrisuFileObject[] sources, GrisuFileObject targetDirectory, int overwrite_mode, boolean join) throws FileTransferException {
		FileTransfer temp = new FileTransfer(sources, targetDirectory, overwrite_mode);
		addTransfer(temp, join);
		return temp;
	}
	
	public FileTransfer addDownload(GrisuFileObject[] sources, GrisuFileObject targetDirectory, String sources_folder, int overwrite_mode, boolean join) throws FileTransferException {
		FileTransfer temp = new FileTransfer(targetDirectory, sources_folder, sources, overwrite_mode);
		addTransfer(temp, join);
		return temp;
	}
	
	/**
	 * Adds the transfer
	 * @param transfer the transfer
	 * @throws FileTransferException 
	 */
	public void addTransfer(FileTransfer transfer, boolean join) throws FileTransferException {
		transfers.add(transfer);
		transfer.addListener(this);
		transfer.startTransfer(join);
	}
	
	public void fileTransferEventOccured(FileTransferEvent e) {
		fireFileTransferEvent(e);		
	}

	public EventList getTransferList() {
		return transfers;
	}
	
	
	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<FileTransferListener> fileTransferListeners;

	private void fireFileTransferEvent(FileTransferEvent event) {
		// if we have no mountPointsListeners, do nothing...
		if (fileTransferListeners != null && !fileTransferListeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<FileTransferListener> targets;
			synchronized (this) {
				targets = (Vector<FileTransferListener>) fileTransferListeners.clone();
			}

			// walk through the listener list 
			Enumeration<FileTransferListener> e = targets.elements();
			while (e.hasMoreElements()) {
				FileTransferListener l = (FileTransferListener) e.nextElement();
				try {
					l.fileTransferEventOccured(event);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	// register a listener
	synchronized public void addListener(FileTransferListener l) {
		if (fileTransferListeners == null)
			fileTransferListeners = new Vector<FileTransferListener>();
		fileTransferListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeListener(FileTransferListener l) {
		if (fileTransferListeners == null) {
			fileTransferListeners = new Vector<FileTransferListener>();
		}
		fileTransferListeners.removeElement(l);
	}
	
}
	
