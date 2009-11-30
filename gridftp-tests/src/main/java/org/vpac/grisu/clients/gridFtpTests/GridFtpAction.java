package org.vpac.grisu.clients.gridFtpTests;

import java.io.File;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoStringList;
import org.vpac.grisu.utils.FileHelpers;

public class GridFtpAction {
	
	static final Logger myLogger = Logger.getLogger(GridFtpAction.class.getName());
	
	public enum Action {

		copy(true, true),
		download(true, false),
		upload(false, true),
		getLastModifiedTime(true, false),
		getFileSize(true, false),
		getFileType(true, false),
		ls(true, false),
		recursiveLs(true, false),
		getChildrenFiles(true, false),
		exists(true, false),
		delete(true, false);
		
		private boolean needsSource;
		private boolean needsTarget;
		
		private Action(boolean needsSource, boolean needsTarget) {
			this.needsSource = needsSource;
			this.needsTarget = needsTarget;
		}
		
		public boolean needsSource() {
			return this.needsSource;
		}
		
		public boolean needsTarget() {
			return this.needsTarget;
		}
		
	};
	
	private final ServiceInterface si;
	private final GridFtpTestController controller;
	
	
	private final Action action;
	private final String name;
	
	public GridFtpAction(Action action, String name, GridFtpTestController controller) {
		this.action = action;
		this.name = name;
		this.controller = controller;
		this.si = controller.getServiceInterface();
	}
	
	public Action getAction() {
		return action;
	}
	
	public String getName() {
		return name;
	}
	
	public GridFtpTestController getController() {
		return controller;
	}
	
	
	public void executeAction(GridFtpActionItem actionItem, String source, String target) {
		
		actionItem.setStartDate(new Date());
		
		switch (action) {
		case copy: copy(actionItem, source, target); break;
		case download: download(actionItem, source, target); break;
		case upload: upload(actionItem, source, target); break;
		case getLastModifiedTime: getLastModifiedTime(actionItem, source); break;
		case getFileSize: getFileSize(actionItem, source); break;
		case getFileType: getFileType(actionItem, source); break;
		case ls: ls(actionItem, source); break;
		case recursiveLs: recursiveLs(actionItem, source); break;
		case getChildrenFiles: getChildrenFile(actionItem, source); break;
		case exists: exists(actionItem, source); break;
		case delete: delete(actionItem, source); break;
		}

		actionItem.setEndDate(new Date());
	}
	
	@Override
	public String toString() {
		return name+" ("+action.toString()+")";
	}
	
	private void delete(GridFtpActionItem actionItem, String source) {
		
		try {
			si.deleteFile(source);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}
	
	private void exists(GridFtpActionItem actionItem, String source) {

		try {
			si.fileExists(source);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}

	private void getChildrenFile(GridFtpActionItem actionItem, String source) {

		try {
			si.getChildrenFileNames(source, false);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}

	private void ls(GridFtpActionItem actionItem, String source) {
		try {
			si.ls(source, 1);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}
	
	private void recursiveLs(GridFtpActionItem actionItem, String source) {
		try {
			si.ls(source, 0);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}

	private void getFileType(GridFtpActionItem actionItem, String source) {
		try {
			si.isFolder(source);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}

	private void getFileSize(GridFtpActionItem actionItem, String source) {
		try {
			si.getFileSize(source);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}

	private void getLastModifiedTime(GridFtpActionItem actionItem, String source) {
		try {
			si.lastModified(source);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}

	private void upload(GridFtpActionItem actionItem, String source, String target) {
		try {
//			si.deleteFile(target);
//			GrisuRegistryManager.getDefault(si).getFileManager().uploadFile(source, target);
			si.upload(new DataHandler(new FileDataSource(source)), target);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}

	private void download(GridFtpActionItem actionItem, String source, String target) {
		try {
			DataHandler dh = si.download(source);
			FileHelpers.saveToDisk(dh.getDataSource(), new File(target));
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}

	private void copy(GridFtpActionItem actionItem, String source, String target) {
		try {
			si.cp(DtoStringList.fromSingleString(source), target, true, false);
		} catch (Exception e) {
			actionItem.setException(e);
			return;
		}
	}
	
	public boolean needsSource() {
		return this.action.needsSource();
	}

	public boolean needsTarget() {
		return this.action.needsTarget();
	}

}
