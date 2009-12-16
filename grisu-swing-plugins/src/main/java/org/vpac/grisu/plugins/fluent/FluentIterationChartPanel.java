package org.vpac.grisu.plugins.fluent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.beans.JLineChart;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FluentIterationChartPanel extends JPanel {

	private JScrollPane scrollPane;
	private JButton refreshButton;
	private JLineChart lineChart;

	private GrisuJobMonitoringObject job = null;
	private FluentJob fluentJob = null;
	private GrisuFileObject stdout = null;

	DefaultTableXYDataset dataset = null;

	/**
	 * Create the panel
	 */
	public FluentIterationChartPanel() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getRefreshButton(), new CellConstraints(4, 4,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getScrollPane(), new CellConstraints(2, 2, 3, 1,
				CellConstraints.FILL, CellConstraints.FILL));
		//

		dataset = new DefaultTableXYDataset();
	}

	protected JLineChart getLineChart() {
		if (lineChart == null) {
			lineChart = new JLineChart();
			lineChart.setAutoscrolls(true);
			// lineChart.setYAxisUpperMargin(4e-04);
		}
		return lineChart;
	}

	protected JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					stdout.getLocalRepresentation(true);
					fluentJob.refresh();
					lineChart.setSubtitle("Time: "
							+ SimpleDateFormat.getDateTimeInstance().format(
									new Date()));
				}
			});
			refreshButton.setText("Refresh");
		}
		return refreshButton;
	}

	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getLineChart());
		}
		return scrollPane;
	}

	public void setFluentJob(GrisuJobMonitoringObject job) {
		this.job = job;
		try {
			stdout = job.getEnvironmentManager().getFileManager()
					.getFileObject(new URI(job.getStdout()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		fluentJob = new FluentJob(stdout.getLocalRepresentation(true));

		lineChart.setTitle("Iterations for job: " + job.getName());
		lineChart.setSubtitle("Time: "
				+ SimpleDateFormat.getDateTimeInstance().format(new Date()));
		lineChart.setXAxisLabel("Iterations");

		// dataset.addSeries(fluentJob.getContinuity());
		dataset.addSeries(fluentJob.getXVelocity());
		dataset.addSeries(fluentJob.getYVelocity());
		dataset.addSeries(fluentJob.getZVelocity());
		lineChart.setDataset(dataset);

	}

}
