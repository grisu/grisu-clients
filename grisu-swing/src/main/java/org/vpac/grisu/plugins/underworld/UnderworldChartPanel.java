

package org.vpac.grisu.plugins.underworld;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;
import org.jfree.beans.JLineChart;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.view.swing.preview.TextViewerPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class UnderworldChartPanel extends JPanel {
	
	private JComboBox yAxisComboBox;
	private JComboBox xAxisComboBox;
	private JLabel label_1;
	private JLabel label;
	private JLabel timestepLabel;
	private JProgressBar progressBar;
	static final Logger myLogger = Logger.getLogger(UnderworldChartPanel.class.getName());

	private TextViewerPanel textViewerPanel;
	private JButton refreshButton;
	private JLineChart lineChart;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private JSplitPane splitPane;

	private DefaultComboBoxModel xAxisModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel yAxisModel = new DefaultComboBoxModel();
	
	UnderworldJob underworldJob = null;

	private DefaultTableXYDataset dataset = new DefaultTableXYDataset();
	
	public void setUnderworldJob(GrisuJobMonitoringObject job) {
		
		underworldJob = new UnderworldJob(job);
		
		for ( String name : underworldJob.getRowNames() ) {
			xAxisModel.addElement(name);
			yAxisModel.addElement(name);
		}
		
//		xAxisModel.setSelectedItem(underworldJob.getRowNames().get(0));
//		yAxisModel.setSelectedItem(underworldJob.getRowNames().get(1));
		
		getProgressBar().setIndeterminate(true);
		getProgressBar().setMaximum(underworldJob.getTimesteps_total());
		getProgressBar().setValue(0);
		getProgressBar().setIndeterminate(false);
		getTimestepLabel().setText("Timestep: 0 / "+underworldJob.getTimesteps_total());

		getLineChart().setTitle("FrequentOutput.dat");
		getLineChart().setSubtitle("for job: "+job.getName());
		

		getTextViewerPanel().setFileToPreview(underworldJob.getFrequentOutput());
		
		getTimestepLabel().setText("Timestep: "+underworldJob.getCurrent_timestep()+" / "+underworldJob.getTimesteps_total());
		getProgressBar().setValue(underworldJob.getCurrent_timestep());
		
		drawChart();
	}
	
	public void refresh() {
		
		if ( xAxisModel.getSize() == 0 ) {
		for ( String name : underworldJob.getRowNames() ) {
			xAxisModel.addElement(name);
			yAxisModel.addElement(name);
		}
		}

		// textviewerpanel checks for modified remote file, so we don't have to do it again
		getTextViewerPanel().refresh(underworldJob.getFrequentOutput());
		// that's dodgy. maybe read some documentation on how to improve it.
		dataset.removeAllSeries();

		underworldJob.refresh();
//		dataset.addSeries(series);
		drawChart();
		
		getTimestepLabel().setText("Timestep: "+underworldJob.getCurrent_timestep()+" / "+underworldJob.getTimesteps_total());
		getProgressBar().setValue(underworldJob.getCurrent_timestep());
	}

	
	public UnderworldChartPanel() {
		super();
		setLayout(new BorderLayout());
		add(getSplitPane());
	}
	
	/**
	 * @return
	 */
	protected JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getLeftPanel());
			splitPane.setRightComponent(getRightPanel());
		}
		return splitPane;
	}
	/**
	 * @return
	 */
	protected JPanel getLeftPanel() {
		if (leftPanel == null) {
			leftPanel = new JPanel();
			leftPanel.setLayout(new BorderLayout());
			leftPanel.add(getLineChart());
		}
		return leftPanel;
	}
	/**
	 * @return
	 */
	protected JPanel getRightPanel() {
		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setMinimumSize(new Dimension(220, 0));
			rightPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					new ColumnSpec("default:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					new RowSpec("22dlu"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					new RowSpec("top:11dlu"),
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					new RowSpec("default:grow(1.0)"),
					FormFactory.RELATED_GAP_ROWSPEC}));
			rightPanel.add(getRefreshButton(), new CellConstraints(6, 2, 1, 4, CellConstraints.RIGHT, CellConstraints.FILL));
			rightPanel.add(getTextViewerPanel(), new CellConstraints(2, 11, 5, 1, CellConstraints.FILL, CellConstraints.FILL));
			rightPanel.add(getProgressBar(), new CellConstraints(2, 9, 5, 1));
			rightPanel.add(getTimestepLabel(), new CellConstraints(2, 7, 3, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
			rightPanel.add(getLabel(), new CellConstraints(2, 3));
			rightPanel.add(getLabel_1(), new CellConstraints(2, 5));
			rightPanel.add(getXAxisComboBox(), new CellConstraints(4, 3));
			rightPanel.add(getYAxisComboBox(), new CellConstraints(4, 5));
		}
		return rightPanel;
	}
	
	
	private void drawChart() {
		
		ArrayList<Double> xAxisData = underworldJob.getData().get(getXAxisComboBox().getSelectedItem());
		ArrayList<Double> yAxisData = underworldJob.getData().get(getYAxisComboBox().getSelectedItem());
		
		if ( xAxisData == null || yAxisData == null ) {
			return;
		}
		
		XYSeries series = new XYSeries((String)getYAxisComboBox().getSelectedItem(), true, false);
		
		for ( int i=0; i<xAxisData.size(); i++ ) {
			series.add(xAxisData.get(i), yAxisData.get(i));
		}
		
		dataset.removeAllSeries();
		dataset.addSeries(series);
		
		getLineChart().setXAxisLabel((String)getXAxisComboBox().getSelectedItem());
		getLineChart().setYAxisLabel((String)getYAxisComboBox().getSelectedItem());
		
		getLineChart().setDataset(dataset);
	}
	
	
	/**
	 * @return
	 */
	protected JLineChart getLineChart() {
		if (lineChart == null) {
			lineChart = new JLineChart();

			lineChart.setYAxisAutoRangeIncludesZero(false);
			lineChart.setMinimumSize(new Dimension(250, 0));
		}
		return lineChart;
	}
	/**
	 * @return
	 */
	protected JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			URL picURL = getClass().getResource("/images/refresh.png");
			ImageIcon refresh = new ImageIcon(picURL);
			refreshButton.setIcon(refresh);
			refreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					refresh();
				}
			});
		}
		return refreshButton;
	}
	/**
	 * @return
	 */
	protected TextViewerPanel getTextViewerPanel() {
		if (textViewerPanel == null) {
			textViewerPanel = new TextViewerPanel();
		}
		return textViewerPanel;
	}
	/**
	 * @return
	 */
	protected JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}
	/**
	 * @return
	 */
	protected JLabel getTimestepLabel() {
		if (timestepLabel == null) {
			timestepLabel = new JLabel();
			timestepLabel.setText("Timestep:");
		}
		return timestepLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("x-Axis");
		}
		return label;
	}
	/**
	 * @return
	 */
	protected JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel();
			label_1.setText("y-Axis");
		}
		return label_1;
	}
	/**
	 * @return
	 */
	protected JComboBox getXAxisComboBox() {
		if (xAxisComboBox == null) {
			xAxisComboBox = new JComboBox(xAxisModel);
			xAxisComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if ( yAxisModel.getSize() > 0 )
						drawChart();
				}
			});
		}
		return xAxisComboBox;
	}
	/**
	 * @return
	 */
	protected JComboBox getYAxisComboBox() {
		if (yAxisComboBox == null) {
			yAxisComboBox = new JComboBox(yAxisModel);
			yAxisComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if ( xAxisModel.getSize() > 0 ) 
						drawChart();
				}
			});
		}
		return yAxisComboBox;
	}
	/**
	 * @return
	 */
	/**
	 * @return
	 */

}
