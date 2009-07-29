package org.vpac.grisu.client.jobMonitoring;

import java.util.Date;

import org.vpac.grisu.client.GwtServiceInterfaceWrapperAsync;
import org.vpac.grisu.client.model.GwtJobConstants;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.XmlReader;
import com.gwtext.client.util.DateUtil;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;

public class JobListPanel extends Panel implements GridCellListener {

	final Store jstore;
	final XmlReader jReader = new XmlReader("job",
			new RecordDef(
					new FieldDef[] {
							new StringFieldDef("jobname"),
							new IntegerFieldDef("status"),
							new StringFieldDef("host"),
							new StringFieldDef("fqan"),
							new DateFieldDef("submissionTime",
									"submissionTime", "Uu") }));

	final ColumnModel columnModel;

	private Panel joblistPanel;
	private Panel jobListControlPanel;

	private GridPanel gridPanel;

	private final GwtServiceInterfaceWrapperAsync service;
	private TabPanel tabPanel;
	private Button btnKill;
	private Button btnKillKlean;
	private Button btnRefreshList;

	public JobListPanel(GwtServiceInterfaceWrapperAsync service) {

		jReader.setId("jobname");
		jstore = new Store(jReader);

		ColumnConfig jobnameCol = new ColumnConfig("Jobname", "jobname", 100,
				true, null, "jobname");
		ColumnConfig statusCol = new ColumnConfig("Status", "status", 120,
				true, new Renderer() {

					public String render(Object value,
							CellMetadata cellMetadata, Record record,
							int rowIndex, int colNum, Store store) {
						return GwtJobConstants.translateStatus((Integer) value);
					}
				}, "status");
		ColumnConfig hostCol = new ColumnConfig("Submission host", "host", 140,
				true, null, "host");
		ColumnConfig fqanCol = new ColumnConfig("VO", "fqan", 100, true, null,
				"fqan");
		ColumnConfig subTime = new ColumnConfig("Submission time",
				"submissionTime", 130, true);
		subTime.setId("submissionTime");
		subTime.setRenderer(new Renderer() {

			public String render(Object value, CellMetadata cellMetadata,
					Record record, int rowIndex, int colNum, Store store) {

				Date date = (Date) value;
				return DateUtil.format(date, "H i - d M y P");
			}
		});

		ColumnConfig[] columnConfigs = { jobnameCol, statusCol, hostCol,
				fqanCol, subTime };

		columnModel = new ColumnModel(columnConfigs);
		columnModel.setDefaultSortable(true);

		this.service = service;
		setLayout(new FitLayout());
		add(getTabPanel());

		loadJobsStatus();
	}

	private void loadJobsStatus() {

		if (!isDesignTime()) {
			setLoading(true, "Refreshing job list...");
			service.getJobsStatus(new AsyncCallback<String>() {

				public void onFailure(Throwable arg0) {

					setLoading(false, null);
					Window.alert("Could not retrieve job status...");

				}

				public void onSuccess(String arg0) {

					setLoading(false, null);
					jstore.removeAll();
					jstore.loadXmlData(arg0, true);
					// jstore.commitChanges();

				}

			});
		}

	}

	private void setLoading(boolean loading, String message) {

		
		if (!isDesignTime()) {
			
			if ( message == null ) {
				message = "Loading...";
			}

			if (Ext.get("jobListGridPanel") != null) {

				if (loading) {
					Ext.get("jobListGridPanel").mask(message);
					getBtnKillKlean().disable();
					getBtnKill().disable();
					getBtnRefreshList().disable();
					System.out.println("Joblist panel masked");
				} else {
					Ext.get("jobListGridPanel").unmask();
					getBtnKillKlean().enable();
					getBtnKill().enable();
					getBtnRefreshList().enable();
					System.out.println("File panel unmasked");
				}
			}
		}

	}

	private GridPanel getGridPanel() {
		if (gridPanel == null) {
			gridPanel = new GridPanel();
			gridPanel.setId("jobListGridPanel");
			gridPanel.setBorder(false);
			gridPanel.setStore(jstore);
			gridPanel.setColumnModel(columnModel);
			gridPanel.stripeRows(true);
			gridPanel.setFrame(true);
			gridPanel.addGridCellListener(this);
			if (!isDesignTime()) {
				gridPanel.setAutoExpandColumn("submissionTime");
			}

		}
		return gridPanel;
	}

	private Panel getJoblistPanel() {
		if (joblistPanel == null) {
			joblistPanel = new Panel("Joblist");
			joblistPanel.setBorder(false);
			joblistPanel.setLayout(new RowLayout());
			// joblistPanel.setId("jobListPanel");
			joblistPanel.add(getGridPanel(), new RowLayoutData("100%"));
			joblistPanel.add(getJobListControlPanel(), new RowLayoutData(40));
		}
		return joblistPanel;
	}

	private Panel getJobListControlPanel() {
		if (jobListControlPanel == null) {
			jobListControlPanel = new Panel();
			jobListControlPanel.setPaddings(10);
			jobListControlPanel.setLayout(new HorizontalLayout(5));
			jobListControlPanel.setBorder(false);
			jobListControlPanel.add(getBtnKill());
			jobListControlPanel.add(getBtnKillKlean());
			jobListControlPanel.add(getBtnRefreshList());
			// jobListControlPanel.setHeight(40);
		}
		return jobListControlPanel;
	}

	private static final boolean isDesignTime() {
		return false;
	}

	private TabPanel getTabPanel() {
		if (tabPanel == null) {
			tabPanel = new TabPanel();
			tabPanel.setBorder(false);
			tabPanel.add(getJoblistPanel());
			// tabPanel.setActiveItemID("jobListPanel");
			tabPanel.setActiveItem(0);
		}
		return tabPanel;
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

		// create a new jobpanel and add it to the tabPanel
		Record rec = grid.getSelectionModel().getSelected();

		// check whether jobpanel is already there...
		Component comp = getTabPanel().getComponent(
				"tab" + rec.getAsString("jobname"));
		if (comp != null) {
			getTabPanel().setActiveItemID("tab" + rec.getAsString("jobname"));
		} else {
			JobDetailsPanel detailsPanel = new JobDetailsPanel(service, rec
					.getAsString("jobname"));
			detailsPanel.setTitle("Job:    " + rec.getAsString("jobname")+ "   ");
			
			detailsPanel.setId("tab" + rec.getAsString("jobname"));

			getTabPanel().add(detailsPanel);
			getTabPanel().setActiveItemID("tab" + rec.getAsString("jobname"));
		}

	}
	
	private void killSelectedJobs(boolean cleanAsWell) {
		
		Record[] recs = getGridPanel().getSelectionModel().getSelections();
		String[] selections = new String[recs.length];
		
		for ( int i=0; i<recs.length; i++ ) {
			String jobname = recs[i].getAsString("jobname");
			selections[i] = jobname;
		}
		
		setLoading(true, "Killing jobs...");
		
		service.killJob(selections, cleanAsWell, new AsyncCallback<Void>() {

			public void onFailure(Throwable arg0) {
				
				setLoading(false, null);

				Window.alert("Unknown error: "+arg0.getLocalizedMessage());
				
			}

			public void onSuccess(Void arg0) {

				setLoading(false, null);
				loadJobsStatus();
				Window.alert("Jobs successfully killed.");
				
			}
			
		});
		
	}

	private Button getBtnKill() {
		if (btnKill == null) {
			btnKill = new Button("Kill");
			btnKill.addListener(new ButtonListenerAdapter() {
				@Override
				public void onClick(Button button, EventObject e) {

					killSelectedJobs(false);

				}
			});
		}
		return btnKill;
	}

	private Button getBtnKillKlean() {
		if (btnKillKlean == null) {
			btnKillKlean = new Button("Kill & Clean");
			btnKillKlean.addListener(new ButtonListenerAdapter() {
				@Override
				public void onClick(Button button, EventObject e) {

					killSelectedJobs(true);

				}
			});
		}
		return btnKillKlean;
	}

	private Button getBtnRefreshList() {
		if (btnRefreshList == null) {
			btnRefreshList = new Button("Refresh list");
			btnRefreshList.addListener(new ButtonListenerAdapter() {
				@Override
				public void onClick(Button button, EventObject e) {

					loadJobsStatus();

				}
			});
		}
		return btnRefreshList;
	}
}
