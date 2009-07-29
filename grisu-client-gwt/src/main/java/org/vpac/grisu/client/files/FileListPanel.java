package org.vpac.grisu.client.files;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vpac.grisu.client.GwtServiceInterfaceWrapperAsync;
import org.vpac.grisu.client.model.GwtGrisuRemoteFile;
import org.vpac.grisu.client.model.GwtMountPointWrapper;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.FitLayout;

public class FileListPanel extends Panel implements GridCellListener,
		HasValueChangeHandlers<GwtGrisuRemoteFile> {

	private final GwtServiceInterfaceWrapperAsync service;
	
	private String forcedRootDirectory;
	private boolean currentlyOnForcedRootDirectory = false;

	private final String randomId = "grid-panel-" + Random.nextInt();

	private Map<String, Set<GwtMountPointWrapper>> mountPointCache = null;
	private Map<String, GwtMountPointWrapper> mountPointMap = null;
	private GridPanel gridPanel;
	private final Store store;
	private ColumnModel columnModel;
	private RecordDef recordDef = new RecordDef(new FieldDef[] {
			//new StringFieldDef("name"), 
			new ObjectFieldDef("name"),
			new StringFieldDef("size") });

	private int level = 0;
	private String currentSite = null;
	private String currentMountPoint = null;
	private String currentPath = null;
	private Map<String, GwtGrisuRemoteFile> currentFolderMap = new TreeMap<String, GwtGrisuRemoteFile>();

	private Panel holderPanel = null;

	/**
	 * Don't use that. Just so that gwt designer doesn't complain.
	 * 
	 * @wbp.parser.constructor
	 */
	public FileListPanel(boolean loadSites) {
		this(null, loadSites);
	}
	
	public FileListPanel(GwtServiceInterfaceWrapperAsync service) {
		this(service, true);
	}

	public FileListPanel(GwtServiceInterfaceWrapperAsync service, boolean loadSites) {
		setLayout(new FitLayout());
		this.service = service;
		
		ColumnConfig[] columns = null; 
		if ( ! isDesignTime() ) {
			columns = new ColumnConfig[] {
				new ColumnConfig("Name", "name", 250, true, null, "name"),
				new ColumnConfig("Size", "size", 70, true, new Renderer() {

					public String render(Object value,
							CellMetadata cellMetadata, Record record,
							int rowIndex, int colNum, Store store) {

						String string = (String) value;

						long size = 0;
						try {
							size = Long.parseLong(string);
						} catch (Exception e) {
							return string;
						}

						String sizeString = size + " B";
						if (size > 1024 * 1024)
							sizeString = size / (1024 * 1024) + " MB";
						else if (size > 1024)
							sizeString = size / 1024 + " KB";

						return sizeString;
					}

				}, "size") };
		} else {
			columns = new ColumnConfig[] {
					new ColumnConfig("Name", "name", 250, true, null, "name"),
					new ColumnConfig("Size", "size", 70, true, null, "size")
			};
		}
		columnModel = new ColumnModel(columns);

		Object[][] temp = new Object[][] { { "", "" } };
		final MemoryProxy proxy = new MemoryProxy(temp);
		final ArrayReader reader = new ArrayReader(recordDef);
		store = new Store(proxy, reader);
		store.load();

		// add(getGridPanel());
		add(getHolderPanel());
		if ( loadSites && !isDesignTime()) {
			getAllMountPoints();
		}
		doLayout();

	}
	
	public void setForcedRootDirectory(String forcedRootDirectory) {
		
		this.forcedRootDirectory = forcedRootDirectory;
		
		this.level = 1000;
		displayUrlChildren(this.forcedRootDirectory);
		
	}

	public Panel getHolderPanel() {
		if (holderPanel == null) {
			holderPanel = new Panel();
			holderPanel.setLayout(new FitLayout());
			holderPanel.setWidth("100%");
			holderPanel.setHeight("100%");
			holderPanel.add(getGridPanel());
		}
		return holderPanel;
	}

	public void setLoading(boolean loading) {

		if (!isDesignTime()) {

			if (Ext.get(randomId) != null) {

				if (loading) {
					Ext.get(randomId).mask("Loading...");
					System.out.println("File panel masked");
				} else {
					Ext.get(randomId).unmask();
					System.out.println("File panel unmasked");
				}
			}
		}
	}

	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<GwtGrisuRemoteFile> handler) {

		// This is really all we need to do to add a new Handler
		// addHandler is defined in Widget, and registers the Handler
		// with our HandlerManager
		return addHandler(handler, ValueChangeEvent.getType());
	}

	private void getAllMountPoints() {

		setLoading(true);
		service.getAllMountPoints(new AsyncCallback<GwtMountPointWrapper[]>() {

			public void onFailure(Throwable arg0) {
				setLoading(false);
				Window.alert("Couldn't get mountpoints");
			}

			public void onSuccess(GwtMountPointWrapper[] arg0) {

				setLoading(false);
				mountPointCache = new TreeMap<String, Set<GwtMountPointWrapper>>();
				mountPointMap = new TreeMap<String, GwtMountPointWrapper>();
				for (GwtMountPointWrapper mp : arg0) {
					Set<GwtMountPointWrapper> tempSet = mountPointCache.get(mp
							.getSite());
					if (tempSet == null) {
						tempSet = new HashSet<GwtMountPointWrapper>();
						mountPointCache.put(mp.getSite(), tempSet);
					}
					tempSet.add(mp);
					mountPointMap.put(mp.getMountpointName(), mp);
					
				}

				displaySiteRoots();

			}
		});
	}

	private void displaySiteRoots() {

		Object[][] siteObjects = new Object[mountPointCache.size()][];
		String[] keys = mountPointCache.keySet().toArray(new String[] {});

		for (int i = 0; i < siteObjects.length; i++) {
			Object[] temp = new Object[] { keys[i], "n/a" };
			siteObjects[i] = temp;
		}

		final MemoryProxy proxy = new MemoryProxy(siteObjects);
		final ArrayReader reader = new ArrayReader(recordDef);
		final Store temp = new Store(proxy, reader);
		temp.load();
		final Record rs[] = temp.getRecords();

		store.removeAll();
		store.add(rs);
		store.commitChanges();

		level = 1;
		currentSite = null;
		currentMountPoint = null;
		currentFolderMap.clear();

	}

	private void displaySiteFileSystems(String siteName) {

		Set<GwtMountPointWrapper> mps = mountPointCache.get(siteName);
		if (mps == null) {
			return;
		}

		Object[][] fsObjects = new Object[mps.size() + 1][];
		Object[] tempParent = new Object[] { "..", "" };
		fsObjects[0] = tempParent;
		GwtMountPointWrapper[] fileSystems = mps
				.toArray(new GwtMountPointWrapper[] {});

		for (int i = 1; i < fsObjects.length; i++) {
			Object[] temp = new Object[] { fileSystems[i - 1], "n/a" };
			fsObjects[i] = temp;
		}

		final MemoryProxy proxy = new MemoryProxy(fsObjects);
		final ArrayReader reader = new ArrayReader(recordDef);
		final Store temp = new Store(proxy, reader);
		temp.load();
		final Record rs[] = temp.getRecords();

		store.removeAll();
		store.add(rs);
		store.commitChanges();

		level = 2;
		currentPath = null;
		currentMountPoint = null;
		currentSite = siteName;
		currentFolderMap.clear();
	}

	private void displayFileSystemChildren(final String mountPointName) {

		setLoading(true);
		service.getChildren(mountPointMap.get(mountPointName).getRootUrl(),
				new AsyncCallback<GwtGrisuRemoteFile[]>() {

					public void onFailure(Throwable arg0) {
						setLoading(false);
						Window.alert("Couldn't get folder listing: "
								+ arg0.getLocalizedMessage());
						level = 2;
					}

					public void onSuccess(GwtGrisuRemoteFile[] arg0) {

						setLoading(false);
						Object[][] childrenObjects = new Object[arg0.length + 1][];
						Object[] tempParent = new Object[] { "..", "" };
						childrenObjects[0] = tempParent;

						currentFolderMap.clear();

						for (int i = 1; i < childrenObjects.length; i++) {
							Object[] temp = null;
							if (arg0[i - 1].isFolder()) {
								temp = new Object[] { arg0[i - 1].getName(),
										"n/a" };
							} else {
								temp = new Object[] {
										arg0[i - 1].getName(),
										new Long(arg0[i - 1].getSize())
												.toString() };
							}
							childrenObjects[i] = temp;
							currentFolderMap.put(arg0[i - 1].getName(),
									arg0[i - 1]);
						}

						final MemoryProxy proxy = new MemoryProxy(
								childrenObjects);
						final ArrayReader reader = new ArrayReader(recordDef);
						final Store temp = new Store(proxy, reader);
						temp.load();
						final Record rs[] = temp.getRecords();

						store.removeAll();
						store.add(rs);
						store.commitChanges();

						level = 3;

						currentMountPoint = mountPointName;
						currentPath = mountPointMap.get(mountPointName)
								.getRootUrl();
					}

				});

	}

	private void displayUrlChildren(final String url) {

		setLoading(true);
		if ( url.equals(forcedRootDirectory) ) {
			currentlyOnForcedRootDirectory = true;
		} else {
			currentlyOnForcedRootDirectory = false;
		}
		service.getChildren(url, new AsyncCallback<GwtGrisuRemoteFile[]>() {

			public void onFailure(Throwable arg0) {
				setLoading(false);
				Window.alert("Couldn't get folder listing: "
						+ arg0.getLocalizedMessage());

			}

			public void onSuccess(GwtGrisuRemoteFile[] arg0) {

				setLoading(false);
				Object[][] childrenObjects;
				int indexShift;
				if ( ! currentlyOnForcedRootDirectory ) {
					indexShift = 1;
					childrenObjects = new Object[arg0.length + 1][];
					Object[] tempParent = new Object[] { "..", "" };
					childrenObjects[0] = tempParent;
				} else {
					indexShift = 0;
					childrenObjects = new Object[arg0.length][];
				}

				currentFolderMap.clear();

				for (int i = indexShift; i < childrenObjects.length; i++) {
					Object[] temp = null;
					if (arg0[i - indexShift].isFolder()) {
						temp = new Object[] { arg0[i - indexShift].getName(), "n/a" };
					} else {
						temp = new Object[] { arg0[i - indexShift].getName(),
								new Long(arg0[i - indexShift].getSize()).toString() };
					}
					childrenObjects[i] = temp;
					currentFolderMap.put(arg0[i - indexShift].getName(), arg0[i - indexShift]);
				}

				final MemoryProxy proxy = new MemoryProxy(childrenObjects);
				final ArrayReader reader = new ArrayReader(recordDef);
				final Store temp = new Store(proxy, reader);
				temp.load();
				final Record rs[] = temp.getRecords();

				store.removeAll();
				store.add(rs);
				store.commitChanges();

				if (currentPath.length() > url.length()) {
					level = level - 1;
				} else {
					level = level + 1;
				}

				currentPath = url;
			}

		});

	}

	private GridPanel getGridPanel() {
		if (gridPanel == null) {
			gridPanel = new GridPanel(store, columnModel);
			gridPanel.setId(randomId);
			gridPanel.addGridCellListener(this);
			gridPanel.setAutoScroll(true);
			gridPanel.setAutoHeight(false);
			gridPanel.setAutoExpandColumn("name");
		}
		return gridPanel;
	}

	public void onCellClick(GridPanel grid, int rowIndex, int colIndex,
			EventObject e) {
		// TODO Auto-generated method stub

	}

	public void onCellContextMenu(GridPanel grid, int rowIndex, int cellIndex,
			EventObject e) {
		// TODO Auto-generated method stub

	}

	public void onCellDblClick(GridPanel grid, int rowIndex, int colIndex,
			EventObject e) {

		System.out.println("Double clicked...");

		if (level == 1) {
			// change to site filesystems
			Record selected = grid.getStore().getAt(rowIndex);
			displaySiteFileSystems(selected.getAsString("name"));
			return;

		} else if (level == 2) {

			if (rowIndex == 0) {
				displaySiteRoots();
				return;
			}

			Record selected = grid.getStore().getAt(rowIndex);
			displayFileSystemChildren(selected.getAsString("name"));
			return;

		} else if (level == 3) {

			if (rowIndex == 0) {
				displaySiteFileSystems(currentSite);
				return;
			}

			Record selected = grid.getStore().getAt(rowIndex);
			String name = selected.getAsString("name");
			GwtGrisuRemoteFile child = currentFolderMap.get(name);

			if (child.isFolder()) {
				displayUrlChildren(child.getPath());
			} else {
				ValueChangeEvent.fire(this, child);
			}
			return;

		} else {
			// means inside filesystem
			if (rowIndex == 0) {
				displayUrlChildren(currentPath.substring(0, currentPath
						.lastIndexOf("/")));
				return;
			}

			Record selected = grid.getStore().getAt(rowIndex);
			String name = selected.getAsString("name");
			GwtGrisuRemoteFile child = currentFolderMap.get(name);

			if (child.isFolder()) {
				displayUrlChildren(child.getPath());
			} else {
				ValueChangeEvent.fire(this, child);
			}

			return;
		}

	}

	public GwtGrisuRemoteFile getCurrentlySelectedFile() {

		Record selected = getGridPanel().getSelectionModel().getSelected();
		if (selected == null) {
			return null;
		}
		String name = selected.getAsString("name");
		if (name == null || "".equals(name)) {
			return null;
		}
		GwtGrisuRemoteFile child = currentFolderMap.get(name);
		return child;
	}
	
	public GwtGrisuRemoteFile[] getCurrentlySelectedFiles() {
		
		Record[] selected = getGridPanel().getSelectionModel().getSelections();
		if ( selected == null || selected.length == 0 ) {
			return null;
		}
		
		//TODO check whether parent folder was selected
		List<GwtGrisuRemoteFile> result = new LinkedList<GwtGrisuRemoteFile>();
		for ( int i=0; i<selected.length; i++ ) {
			String name = selected[i].getAsString("name");
			if ( name != null && ! "".equals(name) && ! "..".equals(name) ) {
				GwtGrisuRemoteFile child = currentFolderMap.get(name);
				if ( child != null ) {
					result.add(child);
				}
			}
		}
		return result.toArray(new GwtGrisuRemoteFile[]{});
	}

	public String getCurrentSite() {
		return currentSite;
	}

	public GwtMountPointWrapper getCurrentMountPoint() {
		return mountPointMap.get(currentMountPoint);
	}

	public String getCurrentPath() {
		return currentPath;
	}

	// public void disablePanel(boolean disable) {
	//		
	// if ( disable ) {
	// this.getEl().mask("Loading...");
	// } else {
	// this.getEl().unmask();
	// }
	//		
	// }

	private static final boolean isDesignTime() {
		return false;
	}

	public void setEnableDragDrop(boolean b) {
		getGridPanel().setEnableDragDrop(b);
	}

	public void setDdGroup(String string) {
		getGridPanel().setDdGroup(string);
	}

	public void setDragDropText(String string) {
		getGridPanel().setDragDropText(string);
	}
	
	public GwtGrisuRemoteFile getRemoteFileObject(String name) {
		if ( currentFolderMap == null ) {
			return null;
		} else {
			return currentFolderMap.get(name);
		}
	}

}
