

package org.vpac.grisu.plugins.namd;

import javax.swing.JPanel;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class NamdPanel extends JPanel {
	
	private NamdInfoPanel namdInfoPanel;
	private NamdJob namdJob = null;

	private NamdIterationChartPanel namdIterationChartPanel;
	

	/**
	 * Create the panel
	 */
	public NamdPanel() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("340px:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("152px"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("306px:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getNamdIterationChartPanel(), new CellConstraints("2, 2, 1, 1, fill, fill"));
		add(getNamdInfoPanel(), new CellConstraints("4, 2, 1, 1, fill, fill"));
		//
		
	}
	
	public void setNamdJob(GrisuJobMonitoringObject job) {

		namdJob = new NamdJob(job);
		getNamdInfoPanel().setNamdJob(namdJob);
		getNamdIterationChartPanel().setNamdJob(namdJob);
		namdJob.refresh();
	}
	
	
	/**
	 * @return
	 */
	protected NamdIterationChartPanel getNamdIterationChartPanel() {
		if (namdIterationChartPanel == null) {
			namdIterationChartPanel = new NamdIterationChartPanel();
		}
		return namdIterationChartPanel;
	}
	
	public void switchTo(String lineChart) {
		namdIterationChartPanel.switchToChart(lineChart);
	}

	/**
	 * @return
	 */
	protected NamdInfoPanel getNamdInfoPanel() {
		if (namdInfoPanel == null) {
			namdInfoPanel = new NamdInfoPanel();
		}
		return namdInfoPanel;
	}


}
