package org.vpac.grisu.client.gridFtpTests;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.utils.FileHelpers;

public class GridFtpTestElement {
	
	static final Logger myLogger = Logger.getLogger(GridFtpTestElement.class.getName());
	
	enum Action {
		copy,
		download,
		upload,
		getLastModifiedTime,
		getFileSize,
		getFileType,
		ls,
		getChildrenFiles
	};
	
	private final ServiceInterface si;
	private final GrisuRegistry registry;
	
	private final String source;
	private final String target;
	private final Action action;
	
	private Exception possibleException;
	
	public GridFtpTestElement(ServiceInterface si, Action action, String source) {
		if ( this.action.equals(Action.copy) || this.action.equals(Action.download) 
				|| this.action.equals(Action.upload) ) {
			throw new IllegalArgumentException("No target specified for action "+action);
		}
		this.si = si;
		this.registry = GrisuRegistryManager.getDefault(si);
		this.action = action;
		this.source = source;
		this.target = null;
	}
	
	public GridFtpTestElement(ServiceInterface si, Action action, String source, String target) {
		this.si = si;
		this.registry = GrisuRegistryManager.getDefault(si);
		this.action = action;
		this.source = source;
		this.target = target;
	}
	
	public void executeTest() {
		
		switch (action) {
		case copy: copy(); break;
		case download: download(); break;
		case upload: upload(); break;
		case getLastModifiedTime: getLastModifiedTime(); break;
		case getFileSize: getFileSize(); break;
		case getFileType: getFileType(); break;
		case ls: ls(); break;
		case getChildrenFiles: getChildrenFile(); break;
		
		}
		
	}

	private void getChildrenFile() {

		try {
			si.getChildrenFileNames(source, false);
		} catch (Exception e) {
			myLogger.debug("Error for test "+toString()+": "+e.getLocalizedMessage());
		}
		
	}

	private void ls() {
		try {
			si.ls(source, 1);
		} catch (Exception e) {
			myLogger.debug("Error for test "+toString()+": "+e.getLocalizedMessage());
		}
	}

	private void getFileType() {
		try {
			si.isFolder(source);
		} catch (Exception e) {
			myLogger.debug("Error for test "+toString()+": "+e.getLocalizedMessage());
		}
	}

	private void getFileSize() {
		try {
			si.getFileSize(source);
		} catch (Exception e) {
			myLogger.debug("Error for test "+toString()+": "+e.getLocalizedMessage());
		}
	}

	private void getLastModifiedTime() {
		try {
			si.lastModified(source);
		} catch (Exception e) {
			myLogger.debug("Error for test "+toString()+": "+e.getLocalizedMessage());
		}
	}

	private void upload() {
		try {
			si.upload(new DataHandler(new FileDataSource(source)), target, false);
		} catch (Exception e) {
			myLogger.debug("Error for test "+toString()+": "+e.getLocalizedMessage());
		}
	}

	private void download() {
		try {
			DataHandler dh = si.download(source);
			FileHelpers.saveToDisk(dh.getDataSource(), new File(target));
		} catch (Exception e) {
			myLogger.debug("Error for test "+toString()+": "+e.getLocalizedMessage());
		}
	}

	private void copy() {
		try {
			si.cp(source, target, true, false);
		} catch (Exception e) {
			myLogger.debug("Error for test "+toString()+": "+e.getLocalizedMessage());
		}
	}
	
	
	@Override
	public String toString() {
		if ( target == null ) {
			return action+"_"+source;
		} else {
			return action+"_"+source+"_target";
		}
	}

}
