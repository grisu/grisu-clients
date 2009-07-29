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
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * Don't use. Doesn't work
 * @author markus
 *
 */
public class FileListPanelNew extends Panel implements GridCellListener,
		HasValueChangeHandlers<GwtGrisuRemoteFile> {

	private final GwtServiceInterfaceWrapperAsync service;

	private final String randomId = "grid-panel-" + Random.nextInt();

	private Map<String, Set<GwtMountPointWrapper>> mountPointCache = null;
	private Map<String, GwtMountPointWrapper> mountPointMap = null;
	private GridPanel gridPanel;
	private final Store store;
	private ColumnModel columnModel;
	private RecordDef recordDef = new RecordDef(
			new FieldDef[] { new ObjectFieldDef("file") });
	// new StringFieldDef("size") });

	private int level = 0;
	private String currentSite = null;
	private String currentMountPoint = null;
	private GwtGrisuRemoteFile currentParentFolder = null;

	private Panel holderPanel = null;

	/**
	 * Don't use that. Just so that gwt designer doesn't complain.
	 * 
	 * @wbp.parser.constructor
	 */
	public FileListPanelNew() {
		this(null);
	}

	public FileListPanelNew(GwtServiceInterfaceWrapperAsync service) {
		setLayout(new FitLayout());
		this.service = service;

		ColumnConfig[] columns = null;
		if (!isDesignTime()) {
			columns = new ColumnConfig[] {
					new ColumnConfig("Name", "file", 250, true, new Renderer() {

						public String render(Object value,
								CellMetadata cellMetadata, Record record,
								int rowIndex, int colNum, Store store) {

							if (value instanceof GwtGrisuRemoteFile) {

								GwtGrisuRemoteFile file = (GwtGrisuRemoteFile) value;
								
								if ( file.isMarkedAsParent() ) {
									return "..";
								}
								return file.getName();

							} else {
								return "Error. Wrong object.";
							}
						}

					}, "file"),
					new ColumnConfig("Size", "file", 70, true, new Renderer() {

						public String render(Object value,
								CellMetadata cellMetadata, Record record,
								int rowIndex, int colNum, Store store) {

							if (value instanceof GwtGrisuRemoteFile) {

								long size;
								try {
									size = ((GwtGrisuRemoteFile) value)
											.getSize();
								} catch (Exception e) {
									return "n/a";
								}

								String sizeString = size + " B";
								if (size > 1024 * 1024)
									sizeString = size / (1024 * 1024) + " MB";
								else if (size > 1024)
									sizeString = size / 1024 + " KB";

								return sizeString;

							} else {
								return "Error. Wrong object2.";
							}

						}
					}, "size") };
		} else {
			columns = new ColumnConfig[] {
					new ColumnConfig("Name", "file", 250, true, null, "name"),
					new ColumnConfig("Size", "file", 70, true, null, "size") };
		}
		columnModel = new ColumnModel(columns);

		Object[][] temp = new Object[][] { {  } };
		final MemoryProxy proxy = new MemoryProxy(temp);
		final ArrayReader reader = new ArrayReader(recordDef);
		store = new Store(proxy, reader);
		store.load();

		// add(getGridPanel());
		add(getHolderPanel());
		if (!isDesignTime()) {
			getAllMountPoints();
		}
		doLayout();

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
			Object[] temp = new Object[] { new GwtGrisuRemoteFile(keys[i]) };
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

	}

	private void displaySiteFileSystems(String siteName) {

		Set<GwtMountPointWrapper> mps = mountPointCache.get(siteName);
		if (mps == null) {
			return;
		}

		Object[][] fsObjects = new Object[mps.size() + 1][];
		GwtGrisuRemoteFile parent = new GwtGrisuRemoteFile(siteName);
		parent.setMarkedAsParent(true);
		Object[] tempParent = new Object[] { parent };
		fsObjects[0] = tempParent;
		GwtMountPointWrapper[] fileSystems = mps
				.toArray(new GwtMountPointWrapper[] {});

		for (int i = 1; i < fsObjects.length; i++) {
			Object[] temp = new Object[] { new GwtGrisuRemoteFile(fileSystems[i]) };
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
		currentParentFolder = null;
		currentMountPoint = null;
		currentSite = siteName;
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
						GwtGrisuRemoteFile tempP = new GwtGrisuRemoteFile(currentMountPoint);
						tempP.setMarkedAsParent(true);
						Object[] tempParent = new Object[] { tempP };
						childrenObjects[0] = tempParent;

						for (int i = 1; i < childrenObjects.length; i++) {
							Object[] temp = null;
							temp = new Object[] { arg0[i-1] };

							childrenObjects[i] = temp;
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
						currentParentFolder = null;
					}

				});

	}

	private void displayUrlChildren(final GwtGrisuRemoteFile file) {

		setLoading(true);
		service.getChildren(file.getPath(), new AsyncCallback<GwtGrisuRemoteFile[]>() {

			public void onFailure(Throwable arg0) {
				setLoading(false);
				Window.alert("Couldn't get folder listing: "
						+ arg0.getLocalizedMessage());

			}

			public void onSuccess(GwtGrisuRemoteFile[] arg0) {

				if (file.isMarkedAsParent()) {
					level = level - 1;
				} else {
					level = level + 1;
				}
				
				setLoading(false);
				Object[][] childrenObjects = new Object[arg0.length + 1][];
				file.setMarkedAsParent(true);
				Object[] tempParent = new Object[] { file };
				childrenObjects[0] = tempParent;

				for (int i = 1; i < childrenObjects.length; i++) {
					Object[] temp = null;
					temp = new Object[] { arg0[i - 1] };
					childrenObjects[i] = temp;
				}

				final MemoryProxy proxy = new MemoryProxy(childrenObjects);
				final ArrayReader reader = new ArrayReader(recordDef);
				final Store temp = new Store(proxy, reader);
				temp.load();
				final Record rs[] = temp.getRecords();

				store.removeAll();
				store.add(rs);
				store.commitChanges();

				currentParentFolder = file;
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
//			gridPanel.setAutoExpandColumn("file");
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
		Record selected = grid.getStore().getAt(rowIndex);
		GwtGrisuRemoteFile file = (GwtGrisuRemoteFile)(selected.getAsObject("file"));

		if (level == 1) {
			// change to site filesystems
			displaySiteFileSystems(file.getName());
			return;

		} else if (level == 2) {

			if (rowIndex == 0) {
				displaySiteRoots();
				return;
			}

			displayFileSystemChildren(file.getName());
			return;

		} else if (level == 3) {

			if (rowIndex == 0) {
				displaySiteFileSystems(currentSite);
				return;
			}

			if (file.isFolder()) {
				displayUrlChildren(file);
			} else {
				ValueChangeEvent.fire(this, file);
			}
			return;

		} else {
			// means inside filesystem

			if (file.isFolder()) {
				displayUrlChildren(file);
			} else {
				ValueChangeEvent.fire(this, file);
			}

			return;
		}

	}

	public GwtGrisuRemoteFile getCurrentlySelectedFile() {

		Record selected = getGridPanel().getSelectionModel().getSelected();
		if (selected == null) {
			return null;
		}
		GwtGrisuRemoteFile file = (GwtGrisuRemoteFile)(selected.getAsObject("file"));
		if (file == null ) {
			return null;
		}
		
		return file;
	}

	public GwtGrisuRemoteFile[] getCurrentlySelectedFiles() {

		Record[] selected = getGridPanel().getSelectionModel().getSelections();
		if (selected == null || selected.length == 0) {
			return null;
		}

		// TODO check whether parent folder was selected
		List<GwtGrisuRemoteFile> result = new LinkedList<GwtGrisuRemoteFile>();
		for (int i = 0; i < selected.length; i++) {
			GwtGrisuRemoteFile file = (GwtGrisuRemoteFile)(selected[i].getAsObject("file"));
			if (file != null ) {
				result.add(file);
			}
		}
		return result.toArray(new GwtGrisuRemoteFile[] {});
	}

	public String getCurrentSite() {
		return currentSite;
	}

	public GwtMountPointWrapper getCurrentMountPoint() {
		return mountPointMap.get(currentMountPoint);
	}

	public GwtGrisuRemoteFile getCurrentPath() {
		return currentParentFolder;
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


}
