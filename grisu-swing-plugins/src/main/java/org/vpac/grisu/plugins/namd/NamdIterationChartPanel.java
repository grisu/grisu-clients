

package org.vpac.grisu.plugins.namd;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.jfree.beans.JLineChart;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.vpac.grisu.client.model.jobs.JobStatusEvent;
import org.vpac.grisu.client.model.jobs.JobStatusListener;

public class NamdIterationChartPanel extends JPanel implements JobStatusListener {
	
	private boolean singlePanel = false;
	
	private NamdJob namdJob = null;

	private JLineChart pressureLineChart;
	private JLineChart tempLineChart;
	private JLineChart totalLineChart;
	
	DefaultTableXYDataset totalDataset = null;
	DefaultTableXYDataset tempDataset = null;
	DefaultTableXYDataset pressureDataset = null;
	
	/**
	 * Create the panel
	 */
	public NamdIterationChartPanel() {
		super();
		setLayout(new CardLayout());
		add(getTotalLineChart(), getTotalLineChart().getName());
		add(getTempLineChart(), getTempLineChart().getName());
		add(getPressureLineChart(), getPressureLineChart().getName());
		//
	}
	
	public void setNamdJob(NamdJob namdJob) {
		this.namdJob = namdJob;
		setTotalSeries(namdJob.getSeries(NamdJob.TOTAL_SERIES));
		setTempSeries(namdJob.getSeries(NamdJob.TEMP_SERIES));
		setPressureSeries(namdJob.getSeries(NamdJob.PRESSURE_SERIES));
		this.namdJob.addJobStatusListener(this);
	}

	public void setTotalSeries(XYSeries series) {
		if ( totalDataset == null ) {
			totalDataset = new DefaultTableXYDataset();
			getTotalLineChart().setDataset(totalDataset);
		} else {
			totalDataset.removeAllSeries();
		}
		totalDataset.addSeries(series);
	}
	public void setTempSeries(XYSeries series) {
		if ( tempDataset == null ) {
			tempDataset = new DefaultTableXYDataset();
			getTempLineChart().setDataset(tempDataset);
		} else {
			tempDataset.removeAllSeries();
		}
		tempDataset.addSeries(series);
	}
	public void setPressureSeries(XYSeries series) {
		if ( pressureDataset == null ) {
			pressureDataset = new DefaultTableXYDataset();
			getPressureLineChart().setDataset(pressureDataset);
		} else {
			pressureDataset.removeAllSeries();
		}
		pressureDataset.addSeries(series);
	}
	
	
	
	/**
	 * @return
	 */
	protected JLineChart getTotalLineChart() {
		if (totalLineChart == null) {
			totalLineChart = new JLineChart();
			totalLineChart.setName(NamdJob.TOTAL_SERIES);
			totalLineChart.setTitle("Total energy");
			totalLineChart.setSubtitle("(kcal/mol)");
			totalLineChart.setXAxisLabel("Timesteps");
			totalLineChart.setYAxisAutoRangeIncludesZero(false);
			totalLineChart.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					if ( e.getClickCount() == 2 ) {
						if ( singlePanel ) {
							getTempLineChart().setVisible(true);
							getPressureLineChart().setVisible(true);
							singlePanel = false;
						} else {
							getTempLineChart().setVisible(false);
							getPressureLineChart().setVisible(false);
							singlePanel = true;
						}
					}
				}
			});
		}
		return totalLineChart;
	}
	/**
	 * @return
	 */
	protected JLineChart getTempLineChart() {
		if (tempLineChart == null) {
			tempLineChart = new JLineChart();
			tempLineChart.setName(NamdJob.TEMP_SERIES);
			tempLineChart.setTitle("Temperature");
			tempLineChart.setSubtitle("(Kelvin)");
			tempLineChart.setXAxisLabel("Timesteps");
			tempLineChart.setYAxisAutoRangeIncludesZero(false);
			tempLineChart.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					if ( e.getClickCount() == 2 ) {
						if ( singlePanel ) {
							getTotalLineChart().setVisible(true);
							getPressureLineChart().setVisible(true);
							singlePanel = false;
						} else {
							getTotalLineChart().setVisible(false);
							getPressureLineChart().setVisible(false);
							singlePanel = true;
						}
					}
				}
			});
		}
		return tempLineChart;
	}
	/**
	 * @return
	 */
	protected JLineChart getPressureLineChart() {
		if (pressureLineChart == null) {
			pressureLineChart = new JLineChart();
			pressureLineChart.setName(NamdJob.PRESSURE_SERIES);
			pressureLineChart.setTitle("Pressure");
			pressureLineChart.setSubtitle("(bar)");
			pressureLineChart.setXAxisLabel("Timesteps");
			pressureLineChart.setYAxisAutoRangeIncludesZero(false);
			pressureLineChart.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					if ( e.getClickCount() == 2 ) {
						if ( singlePanel ) {
							getTotalLineChart().setVisible(true);
							getTempLineChart().setVisible(true);
							singlePanel = false;
						} else {
							getTotalLineChart().setVisible(false);
							getTempLineChart().setVisible(false);
							singlePanel = true;
						}
					}
				}
			});
		}
		return pressureLineChart;
	}

	public void switchToChart(String name) {
	    CardLayout cl = (CardLayout)(this.getLayout());
	    cl.show(this, name);
	}

	public void jobStatusChanged(JobStatusEvent event) {

		setTotalSeries(namdJob.getSeries(NamdJob.TOTAL_SERIES));
		setTempSeries(namdJob.getSeries(NamdJob.TEMP_SERIES));
		setPressureSeries(namdJob.getSeries(NamdJob.PRESSURE_SERIES));
		
	}

}
