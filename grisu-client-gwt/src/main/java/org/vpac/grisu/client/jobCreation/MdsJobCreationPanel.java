package org.vpac.grisu.client.jobCreation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vpac.grisu.client.GwtServiceInterfaceWrapperAsync;
import org.vpac.grisu.client.files.FileListPanel;
import org.vpac.grisu.client.model.GwtGrisuRemoteFile;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.dd.DragData;
import com.gwtext.client.dd.DragSource;
import com.gwtext.client.dd.DropTarget;
import com.gwtext.client.dd.DropTargetConfig;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.form.Radio;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.CheckboxListenerAdapter;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridDragData;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

public class MdsJobCreationPanel extends Panel {
	
	public static final String JOBNAME = "jobname";
	public static final String APPLICATIONNAME = "application";
	public static final String APPLICATIONVERSION = "version";
	public static final String NO_CPUS = "cpus";
	public static final String FORCE_SINGLE = "force_single";
	public static final String FORCE_MPI = "force_mpi";
	public static final String MEMORY_IN_B = "memory";
	public static final String EMAIL_ADDRESS = "email_address";
	public static final String EMAIL_ON_START = "email_on_start";
	public static final String EMAIL_ON_FINISH = "email_on_finish";
	public static final String WALLTIME_IN_MINUTES = "walltime";
	public static final String COMMANDLINE = "commandline";
	public static final String STDOUT = "stdout";
	public static final String STDERR = "stderr";
	public static final String STDIN = "stdin";
	public static final String SUBMISSIONLOCATION = "submissionlocation";
	public static final String INPUT_FILE_URLS = "input_files";

	

	private final GwtServiceInterfaceWrapperAsync service;

	private Set<GwtGrisuRemoteFile> selectedFiles = new HashSet<GwtGrisuRemoteFile>();

	private final Store applicationStore;
	private final Store versionStore;
	private final Store voStore;
	private RecordDef applicationRecordDef = new RecordDef(
			new FieldDef[] { new StringFieldDef("name") });
	private RecordDef versionRecordDef = new RecordDef(
			new FieldDef[] { new StringFieldDef("version") });
	private RecordDef voRecordDef = new RecordDef(
			new FieldDef[] { new StringFieldDef("vo") });

	private String[] currentlyCalculatedApplicationNames = null;
	private String lastCalculatedExecutable = null;
	
	
	final ComboBox versionCombobox;
	final ComboBox applicationCombobox;
	final ComboBox voCombobox;
	final Checkbox forceVersionCheckbox;
	final Button submitButton;

	public MdsJobCreationPanel(GwtServiceInterfaceWrapperAsync service2) {
		super();

		this.service = service2;

		Object[][] temp = new Object[][] { { "n/a" } };
		final MemoryProxy proxy = new MemoryProxy(temp);
		final ArrayReader applicationReader = new ArrayReader(
				applicationRecordDef);
		applicationStore = new Store(proxy, applicationReader);
		applicationStore.load();
		
		final ArrayReader versionReader = new ArrayReader(versionRecordDef);
		versionStore = new Store(proxy, versionReader);
		versionStore.load();
		
		final ArrayReader voReader = new ArrayReader(voRecordDef);
		voStore = new Store(proxy, voReader);
		voStore.load();

		Store dayStore = new SimpleStore(new String[] { "days" },
				new String[][] { { "0" }, { "1" }, { "2" }, { "3" }, { "4" },
						{ "7" }, { "14" }, { "21" } });
		dayStore.load();
		Store hourStore = new SimpleStore(new String[] { "hours" },
				new String[][] { { "0" }, { "1" }, { "2" }, { "4" }, { "8" },
						{ "12" }, { "18" } });
		hourStore.load();
		Store minutesStore = new SimpleStore(new String[] { "minutes" },
				new String[][] { { "0" }, { "15" }, { "30" }, { "45" } });
		minutesStore.load();
		Store cpuStore = new SimpleStore(new String[] { "cpus" },
				new String[][] { { "1" }, { "2" }, { "4" }, { "8" } });
		cpuStore.load();
		Store memoryStore = new SimpleStore(new String[] { "memory" },
				new String[][] { { "0" }, { "1024" }, { "2048" }, { "4096" } });
		memoryStore.load();

		final FormPanel formPanel = new FormPanel();
		formPanel.setId("JobCreationFormPanel");
		formPanel.setLabelAlign(Position.LEFT);
		formPanel.setTitle("Create job");
		formPanel.setPaddings(5);
		formPanel.setWidth(600);
		formPanel.setAutoScroll(true);

		Panel topPanel = new Panel();
		topPanel.setLayout(new ColumnLayout());
		topPanel.setBorder(false);

		Panel firstColumn = new Panel();
		firstColumn.setLayout(new FormLayout());
		firstColumn.setBorder(false);

		firstColumn.add(new TextField("Jobname", JOBNAME),
				new AnchorLayoutData("95%"));
		topPanel.add(firstColumn, new ColumnLayoutData(0.5));

		Panel secondColumn = new Panel();
		secondColumn.setLayout(new FormLayout());
		secondColumn.setBorder(false);

		voCombobox = new ComboBox("VO", "vo");
		voCombobox.setName("vo");
		voCombobox.setStore(voStore);
		voCombobox.disable();
		voCombobox.setForceSelection(true);
		voCombobox.setMinChars(1);
		voCombobox.setDisplayField("vo");
		voCombobox.setMode(ComboBox.LOCAL);
		voCombobox.setTriggerAction(ComboBox.ALL);
		voCombobox.setTypeAhead(false);
		voCombobox.setEditable(false);
		voCombobox.setSelectOnFocus(true);
		
		
		
		service.getAllFqans(new AsyncCallback<String[]>() {

			public void onFailure(Throwable caught) {
				StringBuffer temp = new StringBuffer();

				MessageBox.alert("Failed: " + caught.getMessage());

				// Window.alert("Could not load available applications: "+caught.getLocalizedMessage());
				caught.printStackTrace();

			}

			public void onSuccess(String[] result) {

				Object[][] voWrapper = new Object[result.length][1];
				String firstNonArcsRootVo = null;
				for (int i = 0; i < result.length; i++) {
					// I know, I know...
					if ( ! result[i].equals("/ARCS") ) {
						if ( firstNonArcsRootVo == null ) {
							firstNonArcsRootVo = result[i];
						}
						Object[] temp = new Object[] { result[i] };
						voWrapper[i] = temp;
					}
				}

				final MemoryProxy proxy = new MemoryProxy(voWrapper);
				final ArrayReader reader = new ArrayReader(voRecordDef);
				final Store temp = new Store(proxy, reader);
				temp.load();
				final Record rs[] = temp.getRecords();

				voStore.removeAll();
				voStore.add(rs);
				voStore.commitChanges();
				voCombobox.enable();
				
				if ( firstNonArcsRootVo != null ) {
					voCombobox.setValue(firstNonArcsRootVo);
				}
			}

		});
		
		secondColumn.add(voCombobox,
				new AnchorLayoutData("95%"));
		topPanel.add(secondColumn, new ColumnLayoutData(0.5));

		formPanel.add(topPanel);

		TabPanel tabPanel = new TabPanel();
		tabPanel.setPlain(true);
		tabPanel.setActiveTab(0);
		tabPanel.setHeight(450);

		Panel firstTabRootPanel = new Panel();
		firstTabRootPanel.setLayout(new VerticalLayout(5));
		firstTabRootPanel.setTitle("Job details");
		firstTabRootPanel.setBorder(false);

		Panel columnPanel = new Panel();
		columnPanel.setLayout(new ColumnLayout());
		columnPanel.setWidth(550);
		columnPanel.setBorder(false);

		Panel firstTabLeftSide = new Panel();
		firstTabLeftSide.setBorder(false);
		firstTabLeftSide.setLayout(new FormLayout());
		firstTabLeftSide.setPaddings(10);

		final ComboBox cpusCombobox = new ComboBox();
		cpusCombobox.setWidth(60);
		cpusCombobox.setName(NO_CPUS);
		cpusCombobox.setStore(cpuStore);
		cpusCombobox.setForceSelection(true);
		cpusCombobox.setMinChars(1);
		cpusCombobox.setFieldLabel("CPUs");
		cpusCombobox.setDisplayField("cpus");
		cpusCombobox.setMode(ComboBox.LOCAL);
		cpusCombobox.setTriggerAction(ComboBox.ALL);
		cpusCombobox.setTypeAhead(false);
		cpusCombobox.setSelectOnFocus(true);
		cpusCombobox.setValue("1");

		firstTabLeftSide.add(cpusCombobox);
		final Radio singleRadio = new Radio("Single", "jobType");
		singleRadio.setValue(FORCE_SINGLE);
		singleRadio.disable();
		final Radio mpiRadio = new Radio("MPI", "jobType");
		mpiRadio.setValue(FORCE_MPI);
		mpiRadio.disable();
		Checkbox forceJobTypeCheckbox = new Checkbox("Force job type:",
				"force_jobType");
		forceJobTypeCheckbox.addListener(new CheckboxListenerAdapter() {
			public void onCheck(Checkbox field, boolean checked) {
				if (checked) {
					singleRadio.enable();
					mpiRadio.enable();
					try {
						int cpus = Integer.parseInt(cpusCombobox
								.getValueAsString());
						if (cpus == 1) {
							singleRadio.setChecked(true);
						} else {
							mpiRadio.setChecked(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					singleRadio.disable();
					mpiRadio.disable();
					singleRadio.setChecked(false);
					mpiRadio.setChecked(false);
				}
			}
		});
		firstTabLeftSide.add(forceJobTypeCheckbox);
		firstTabLeftSide.add(singleRadio);
		firstTabLeftSide.add(mpiRadio);
		firstTabLeftSide.add(new HTML("<hr></hr>"));
		firstTabLeftSide.add(new TextField("Email", EMAIL_ADDRESS, 150));
		firstTabLeftSide.add(new Label("Send email when..."));
		firstTabLeftSide.add(new Checkbox("job starts", EMAIL_ON_START));
		firstTabLeftSide
				.add(new Checkbox("job finishes", EMAIL_ON_FINISH));

		columnPanel.add(firstTabLeftSide, new ColumnLayoutData(0.5));

		Panel firstTabRightSide = new Panel();
		firstTabRightSide.setLayout(new FormLayout());
		firstTabRightSide.setPaddings(10);
		firstTabRightSide.setBorder(false);

		ComboBox memoryCombobox = new ComboBox();
		memoryCombobox.setWidth(60);
		memoryCombobox.setLabel("Memory (in MB)");
		memoryCombobox.setStore(memoryStore);
		memoryCombobox.setForceSelection(true);
		memoryCombobox.setMinChars(1);
		memoryCombobox.setDisplayField("memory");
		memoryCombobox.setName(MEMORY_IN_B);
		memoryCombobox.setMode(ComboBox.LOCAL);
		memoryCombobox.setTriggerAction(ComboBox.ALL);
		memoryCombobox.setTypeAhead(false);
		memoryCombobox.setSelectOnFocus(true);
		memoryCombobox.setValue("0");
		firstTabRightSide.add(memoryCombobox);
		firstTabRightSide.add(new HTML("<hr></hr>"));
		firstTabRightSide.add(new Label("Walltime:"));

		ComboBox daysCombobox = new ComboBox();
		daysCombobox.setWidth(60);
		daysCombobox.setLabel("Days");
		daysCombobox.setName("days");
		daysCombobox.setMinListWidth(80);
		daysCombobox.setStore(dayStore);
		daysCombobox.setForceSelection(true);
		daysCombobox.setMinChars(1);
		daysCombobox.setDisplayField("days");
		daysCombobox.setMode(ComboBox.LOCAL);
		daysCombobox.setTriggerAction(ComboBox.ALL);
		daysCombobox.setTypeAhead(false);
		daysCombobox.setSelectOnFocus(true);
		daysCombobox.setValue("0");
		firstTabRightSide.add(daysCombobox);

		ComboBox hoursCombobox = new ComboBox();
		hoursCombobox.setWidth(60);
		hoursCombobox.setStore(hourStore);
		hoursCombobox.setForceSelection(true);
		hoursCombobox.setMinChars(1);
		hoursCombobox.setFieldLabel("Hours");
		hoursCombobox.setDisplayField("hours");
		hoursCombobox.setName("hours");
		hoursCombobox.setMode(ComboBox.LOCAL);
		hoursCombobox.setTriggerAction(ComboBox.ALL);
		hoursCombobox.setTypeAhead(false);
		hoursCombobox.setSelectOnFocus(true);
		hoursCombobox.setValue("0");
		firstTabRightSide.add(hoursCombobox);

		ComboBox minutesCombobox = new ComboBox();
		minutesCombobox.setWidth(60);
		minutesCombobox.setStore(minutesStore);
		minutesCombobox.setForceSelection(true);
		minutesCombobox.setMinChars(1);
		minutesCombobox.setFieldLabel("Minutes");
		minutesCombobox.setDisplayField("minutes");
		minutesCombobox.setName("minutes");
		minutesCombobox.setMode(ComboBox.LOCAL);
		minutesCombobox.setTriggerAction(ComboBox.ALL);
		minutesCombobox.setTypeAhead(false);
		minutesCombobox.setSelectOnFocus(true);
		minutesCombobox.setValue("15");
		firstTabRightSide.add(minutesCombobox);

		columnPanel.add(firstTabRightSide, new ColumnLayoutData(0.5));

		firstTabRootPanel.add(columnPanel);

		firstTabRootPanel.add(new HTML("<hr></hr>"));

		Panel commandlinePanel = new Panel();
		commandlinePanel.setLayout(new FormLayout());
		commandlinePanel.setBorder(false);
		commandlinePanel.setPaddings(10);
		
		applicationCombobox = new ComboBox();
		versionCombobox = new ComboBox();
		
		forceVersionCheckbox = new Checkbox("Force version", "force_version");
		final Checkbox forceApplicationCheckbox = new Checkbox("Force application", "force_application");
		forceApplicationCheckbox.addListener(new CheckboxListenerAdapter(){

			public void onCheck(Checkbox field, boolean checked) {

				if ( checked ) {
					applicationCombobox.enable();
					forceVersionCheckbox.enable();
				} else {
					applicationCombobox.disable();
					forceVersionCheckbox.setChecked(false);
					versionStore.removeAll();
					forceVersionCheckbox.disable();
				}
				
			}
			
		});
		
		forceVersionCheckbox.addListener(new CheckboxListenerAdapter(){

			public void onCheck(Checkbox field, boolean checked) {

				if ( checked ) {
					versionCombobox.enable();
					loadVersionsForApplicationAndVO();
				} else {
					versionStore.removeAll();
					versionCombobox.disable();
				}
				
			}
			
		});

		applicationCombobox.setName(APPLICATIONNAME);
		applicationCombobox.setWidth(100);
		applicationCombobox.setStore(applicationStore);
		applicationCombobox.disable();
		applicationCombobox.setForceSelection(true);
		applicationCombobox.setMinChars(1);
		applicationCombobox.setDisplayField("name");
		applicationCombobox.setMode(ComboBox.LOCAL);
		applicationCombobox.setTriggerAction(ComboBox.ALL);
		applicationCombobox.setTypeAhead(false);
		applicationCombobox.setEditable(false);
		applicationCombobox.setSelectOnFocus(true);
		
		applicationCombobox.addListener(new ComboBoxListenerAdapter() {
			public void onSelect(ComboBox comboBox, Record record, int index) {  
				loadVersionsForApplicationAndVO();
			}  
		});
		
		voCombobox.addListener(new ComboBoxListenerAdapter() {
			public void onSelect(ComboBox comboBox, Record record, int index) {  
				loadVersionsForApplicationAndVO();
			}  
		});
		
		versionCombobox.setName(APPLICATIONVERSION);
		versionCombobox.setWidth(100);
		versionCombobox.setStore(versionStore);
		versionCombobox.disable();
		versionCombobox.setForceSelection(true);
		versionCombobox.setMinChars(1);
		versionCombobox.setDisplayField("version");
		versionCombobox.setMode(ComboBox.LOCAL);
		versionCombobox.setTriggerAction(ComboBox.ALL);
		versionCombobox.setTypeAhead(false);
		versionCombobox.setEditable(false);
		versionCombobox.setSelectOnFocus(true);
		
		final TextArea commandlineTextArea = new TextArea();
		commandlineTextArea.setName(COMMANDLINE);
		commandlineTextArea.setLabel("Commandline");
		commandlineTextArea.setWidth(400);
		commandlineTextArea.addKeyListener(EventObject.SPACE,
				new KeyListener() {

					public void onKey(int key, EventObject e) {

						String text = commandlineTextArea.getValueAsString();
						final String executable;
						int firstWhitespace = text.indexOf(" ");
						if (firstWhitespace == -1) {
							executable = text;
						} else {
							executable = text.substring(0, firstWhitespace);
						}

						if (lastCalculatedExecutable != null
								&& lastCalculatedExecutable.equals(executable)) {
							return;
						}

						service.getApplicationForExecutable(executable,
								new AsyncCallback<String[]>() {

									public void onFailure(Throwable arg0) {
										Window
												.alert("Could not get application name for executable: "
														+ executable);
									}

									public void onSuccess(String[] arg0) {

										if (arg0 != null && arg0.length >= 1) {
											applicationCombobox
													.setValue(arg0[0]);
											currentlyCalculatedApplicationNames = arg0;
										} else {
											applicationCombobox.setValue("n/a");
											currentlyCalculatedApplicationNames = null;
										}

									}

								});

					}
				});

		commandlinePanel.add(commandlineTextArea);


		
		Panel applicationAndVersionPanel = new Panel();
		applicationAndVersionPanel.setLayout(new ColumnLayout());
		applicationAndVersionPanel.setBorder(false);

		Panel applicationColumn = new Panel();
		applicationColumn.setLayout(new FormLayout());
		applicationColumn.setBorder(false);
		
		applicationColumn.add(forceApplicationCheckbox, new AnchorLayoutData("95%"));
		applicationColumn.add(applicationCombobox, new AnchorLayoutData("95%"));
		
		Panel versionColumn = new Panel();
		versionColumn.setLayout(new FormLayout());
		versionColumn.setBorder(false);
		
		versionColumn.add(forceVersionCheckbox, new AnchorLayoutData("95%"));
		versionColumn.add(versionCombobox, new AnchorLayoutData("95%"));
		

		applicationAndVersionPanel.add(applicationColumn, new ColumnLayoutData(0.5));
		applicationAndVersionPanel.add(versionColumn, new ColumnLayoutData(0.5));

		service.getAllApplicationsOnTheGrid(new AsyncCallback<String[]>() {

			public void onFailure(Throwable caught) {
				StringBuffer temp = new StringBuffer();

				MessageBox.alert("Failed: " + caught.getMessage());

				// Window.alert("Could not load available applications: "+caught.getLocalizedMessage());
				caught.printStackTrace();

			}

			public void onSuccess(String[] result) {

				Object[][] appWrapper = new Object[result.length][1];
				for (int i = 0; i < result.length; i++) {
					Object[] temp = new Object[] { result[i] };
					appWrapper[i] = temp;
				}

				final MemoryProxy proxy = new MemoryProxy(appWrapper);
				final ArrayReader reader = new ArrayReader(applicationRecordDef);
				final Store temp = new Store(proxy, reader);
				temp.load();
				final Record rs[] = temp.getRecords();

				applicationStore.removeAll();
				applicationStore.add(rs);
				applicationStore.commitChanges();

			}

		});

		firstTabRootPanel.add(commandlinePanel);
		firstTabRootPanel.add(applicationAndVersionPanel);
		tabPanel.add(firstTabRootPanel);

		Panel secondTab = new Panel();
		secondTab.setLayout(new ColumnLayout());
		secondTab.setTitle("Files");
		secondTab.setBorder(false);

		Panel secondTabLeftSide = new Panel();
		secondTabLeftSide.setLayout(new FitLayout());
		secondTabLeftSide.setPaddings(10);

		RecordDef recordDef = new RecordDef(new FieldDef[] {
				new StringFieldDef("name"), new StringFieldDef("size") });

		ColumnConfig[] columns = null;
		if (!isDesignTime()) {
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
					new ColumnConfig("Size", "size", 70, true, null, "size") };
		}

		Object[][] tempStoreData = new Object[][] { { "", "" } };
		final MemoryProxy selFileproxy = new MemoryProxy(tempStoreData);
		final ArrayReader reader = new ArrayReader(recordDef);
		final Store selFileStore = new Store(selFileproxy, reader);
		selFileStore.load();
		ColumnModel columnModel = new ColumnModel(columns);
		GridPanel selectedFilesPanel = new GridPanel(selFileStore, columnModel);
		selectedFilesPanel.setSize(250, 400);
		selectedFilesPanel.setId("selectedFilesGridPanel");
		selectedFilesPanel.setAutoScroll(true);
		selectedFilesPanel.setAutoHeight(false);
		selectedFilesPanel.setAutoExpandColumn("name");
		selectedFilesPanel.setEnableDragDrop(true);
		selectedFilesPanel.setDdGroup("myDDGroup");
		// selectedFilesPanel.add

		// selectedFilesPanel.addListener(new PanelListener(){

		final FileListPanel fileListPanel = new FileListPanel(service);
		fileListPanel.setSize(250, 400);
		fileListPanel.setEnableDragDrop(true);
		fileListPanel.setDdGroup("myDDGroup");
		fileListPanel.setDragDropText("Drag to the panel on the right");
		fileListPanel
				.addValueChangeHandler(new ValueChangeHandler<GwtGrisuRemoteFile>() {

					public void onValueChange(
							ValueChangeEvent<GwtGrisuRemoteFile> arg0) {
						selectedFiles.add(arg0.getValue());
						// selectedFilesPanel.add.setText(arg0.getValue().getName());
					}

				});

		DropTargetConfig cfg = new DropTargetConfig();
		cfg.setdDdGroup("myDDGroup");

		DropTarget tg = new DropTarget(selectedFilesPanel, cfg) {
			public boolean notifyDrop(DragSource source, EventObject e,
					DragData data) {

				if (data instanceof GridDragData) {
					GridDragData gridData = (GridDragData) data;
					Record[] records = gridData.getSelections();

					for (Record record : records) {

						GwtGrisuRemoteFile file = fileListPanel
								.getRemoteFileObject(record.getAsString("name"));
						if (file != null) {
							if (!file.isFolder()) {
								selFileStore.add(record);
								selectedFiles.add(file);
							} else {
								System.out
										.println("Folders are not supported yet.");
							}
						}
					}

				}

				return true;
			}

			public String notifyOver(DragSource source, EventObject e,
					DragData data) {
				return "x-dd-drop-ok";
			}
		};

		secondTabLeftSide.add(fileListPanel);

		secondTab.add(secondTabLeftSide, new ColumnLayoutData(0.5));

		Panel secondTabRightSide = new Panel();
		secondTabRightSide.setLayout(new FitLayout());
		secondTabRightSide.setPaddings(10);

		secondTabRightSide.add(selectedFilesPanel);
		secondTab.add(secondTabRightSide, new ColumnLayoutData(0.5));

		tabPanel.add(secondTab);

		formPanel.add(tabPanel);

		submitButton = new Button("Submit");

		submitButton.addListener(new ButtonListenerAdapter() {

			public void onClick(Button button, EventObject e) {

				getJobData(formPanel.getForm());

			}

		});

		formPanel.addButton(submitButton);

		add(formPanel);
	}
	
	public void loadVersionsForApplicationAndVO() {
		
		if ( ! forceVersionCheckbox.getValue() ) {
			return;
		}
		
		final String selectedApplication = applicationCombobox.getValueAsString();
		final String fqan = voCombobox.getValueAsString();
		
		if ( fqan == null || fqan.length() == 0 || selectedApplication == null || selectedApplication.length() == 0 ) {
			return;
		}

		versionCombobox.disable();
		
		service.getVersionsOfApplicationForVO(selectedApplication, fqan, new AsyncCallback<String[]>(){

			public void onFailure(Throwable arg0) {
				Window.alert("Could not get versions for "+selectedApplication+" and "+fqan);
			}

			public void onSuccess(String[] result) {
				
				if ( result == null || result.length == 0 ) {
					Window.alert("This application can't be accessed with the currently selected VO.");
					versionStore.removeAll();
					return;
				}

				Object[][] versionWrapper = new Object[result.length][1];
				for (int i = 0; i < result.length; i++) {
					Object[] temp = new Object[] { result[i] };
					versionWrapper[i] = temp;
				}

				final MemoryProxy proxy = new MemoryProxy(versionWrapper);
				final ArrayReader reader = new ArrayReader(versionRecordDef);
				final Store temp = new Store(proxy, reader);
				temp.load();
				final Record rs[] = temp.getRecords();

				versionStore.removeAll();
				versionStore.add(rs);
				versionStore.commitChanges();
				versionCombobox.enable();
				
				if ( result.length > 0 ) {
					versionCombobox.setValue(result[0]);
				}
			}
			
			
		});
	}

	private Map<String, String> getJobData(Form form) {
		
		String formValues = form.getValues();
		
		String decodedFormValues = URL.decodeComponent(formValues);

		Map<String, String> jobData = new HashMap<String, String>();

		String[] nameValuePairs = decodedFormValues.split("&");

		for (int i = 0; i < nameValuePairs.length; i++) {

			String[] oneItem = nameValuePairs[i].split("=");
			if ( oneItem.length == 2 ) { 
				jobData.put(oneItem[0], oneItem[1]);
			}

		}
		
		
		int days = Integer.parseInt(jobData.get("days"));
		int hours = Integer.parseInt(jobData.get("hours"));
		int minutes = Integer.parseInt(jobData.get("minutes"));
		
		Integer walltime = (days * 60 * 24) + (hours * 60 ) + (minutes);
		jobData.put(WALLTIME_IN_MINUTES, walltime.toString());
		
		StringBuffer filesToStage = new StringBuffer();
		for ( GwtGrisuRemoteFile file : selectedFiles ) {
			filesToStage.append(file.getPath()+",");
		}
		if ( selectedFiles.size() > 0 ) {
			jobData.put(INPUT_FILE_URLS, filesToStage.substring(0, filesToStage.length()-1));
		}
		
		setFormPanelBusy(true);
		service.submitJob(jobData, new AsyncCallback<Void>(){

			public void onFailure(Throwable arg0) {
				setFormPanelBusy(false);
				Window.alert("Could not submit job: "+arg0.getLocalizedMessage());
			
			}

			public void onSuccess(Void arg0) {
				setFormPanelBusy(false);
				Window.alert("Job submitted successfully.");
			}
			
			
		});

		return jobData;

	}
	
	public void setFormPanelBusy(boolean loading) {

		if (!isDesignTime()) {

			if (Ext.get("JobCreationFormPanel") != null) {

				if (loading) {
					submitButton.disable();
					Ext.get("JobCreationFormPanel").mask("Submitting job...");
					System.out.println("File panel masked");
				} else {
					Ext.get("JobCreationFormPanel").unmask();
					submitButton.enable();
					System.out.println("File panel unmasked");
				}
			}
		}
	}

	private static final boolean isDesignTime() {
		return false;
	}

}
