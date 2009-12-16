package org.vpac.grisu.plugins.namd;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.JobStatusEvent;
import org.vpac.grisu.client.model.jobs.JobStatusListener;
import org.vpac.grisu.client.view.swing.utils.Utils;
import org.vpac.grisu.plugins.ApplicationPathDialog;
import org.vpac.grisu.settings.ClientPropertiesManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class NamdInfoPanel extends JPanel implements JobStatusListener {

	private JButton vmdButton;
	static final Logger myLogger = Logger.getLogger(NamdInfoPanel.class
			.getName());

	private NamdJob namdJob = null;

	private EnvironmentManager em = null;

	private DefaultComboBoxModel lineCharts = new DefaultComboBoxModel();

	private JButton refreshButton;
	private JLabel label_2;
	private JComboBox comboBox;
	private JLabel label_1;
	private JEditorPane editorPane;
	private JLabel timestepLabel;
	private JProgressBar progressBar;
	private JLabel timestepsLabel;

	/**
	 * Create the panel
	 */
	public NamdInfoPanel() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("87px:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				new RowSpec("top:17dlu"), FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, new RowSpec("20px"),
				new RowSpec("19dlu"), FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getTimestepsLabel(), new CellConstraints("2, 8, left, fill"));
		add(getProgressBar(), new CellConstraints("2, 10, 1, 1, fill, fill"));
		add(getTimestepLabel(), new CellConstraints(2, 11,
				CellConstraints.FILL, CellConstraints.DEFAULT));
		add(getEditorPane(), new CellConstraints(2, 14, CellConstraints.FILL,
				CellConstraints.FILL));
		add(getLabel_1(), new CellConstraints(2, 4));
		add(getComboBox(), new CellConstraints(2, 6, CellConstraints.FILL,
				CellConstraints.DEFAULT));
		add(getLabel_2(), new CellConstraints(2, 12));
		add(getRefreshButton(), new CellConstraints(2, 2));
		add(getVmdButton(), new CellConstraints(2, 16, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		//
	}

	private String createInformationHTML() {

		StringBuffer info = new StringBuffer();
		info.append("<html><body>");
		if (namdJob.getLastIteration() <= namdJob.getMinimize()) {
			info.append("Still minimizing...");
		} else {
			info.append("<b>Averages:</b>");
			info.append("<dl>");
			String sstep = new Double(namdJob.getS_SecondsAvg()).toString();
			if (sstep.length() > 8) {
				sstep = sstep.substring(0, 7);
			}
			info.append("<dt>s/step</dt><dd>" + sstep + "</dd>");
			String days_Ns = new Double(namdJob.getDays_nsAvg()).toString();
			if (days_Ns.length() > 8) {
				days_Ns = days_Ns.substring(0, 7);
			}
			info.append("<dt>days/ns</dt><dd>" + days_Ns + "</dd>");
			String memory = new Double(namdJob.getMemoryAvg()).toString();
			if (memory.length() > 8) {
				memory = memory.substring(0, 7);
			}
			info.append("<dt>Memory</dt><dd>" + memory + "</dd>");

			info.append("<b>Estimated time of completion:</b>");
			info.append("<p>" + namdJob.getEstimatedTimeString() + "</p>");

		}

		info.append("</body></html>");

		return info.toString();
	}

	/**
	 * @return
	 */
	protected JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(lineCharts);
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					((NamdPanel) getParent()).switchTo((String) getComboBox()
							.getSelectedItem());
				}
			});
		}
		return comboBox;
	}

	/**
	 * @return
	 */
	/**
	 * @return
	 */
	protected JEditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			editorPane.setContentType("text/html");
			editorPane.setBackground(Color.WHITE);
		}
		return editorPane;
	}

	/**
	 * @return
	 */
	protected JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel();
			label_1.setText("Display:");
		}
		return label_1;
	}

	/**
	 * @return
	 */
	protected JLabel getLabel_2() {
		if (label_2 == null) {
			label_2 = new JLabel();
			label_2.setText("Information:");
		}
		return label_2;
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
	protected JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					namdJob.refresh();
				}
			});
			refreshButton.setText("Refresh");
		}
		return refreshButton;
	}

	/**
	 * @return
	 */
	protected JLabel getTimestepLabel() {
		if (timestepLabel == null) {
			timestepLabel = new JLabel();
			timestepLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return timestepLabel;
	}

	/**
	 * @return
	 */
	protected JLabel getTimestepsLabel() {
		if (timestepsLabel == null) {
			timestepsLabel = new JLabel();
			timestepsLabel.setText("Timesteps:");
		}
		return timestepsLabel;
	}

	/**
	 * @return
	 */
	protected JButton getVmdButton() {
		if (vmdButton == null) {
			vmdButton = new JButton();
			vmdButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					String vmdPath = null;
					try {
						vmdPath = ClientPropertiesManager
								.getClientConfiguration().getString("vmdPath");
					} catch (ConfigurationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (vmdPath == null || "".equals(vmdPath)
							|| !new File(vmdPath).exists()) {
						ApplicationPathDialog apd = new ApplicationPathDialog();
						apd
								.setApplication(
										"Please specify path to vmd",
										"<html><body>Please specify the path to your local vmd executable in the text box below.</body></html>");
						apd.setVisible(true);
						if (apd.cancelled()) {
							return;
						}

						if (apd.getPath() == null) {
							Utils.showErrorMessage(em, NamdInfoPanel.this,
									"applicationCouldNotBeFound", null);
							return;
						}

						vmdPath = apd.getPath();

					}

					GrisuFileObject jobDir = namdJob.getJob()
							.getJobDirectoryObject();

					if (jobDir == null) {
						Utils.showErrorMessage(em, NamdInfoPanel.this,
								"jobNotReady", null);
						return;
					}

					GrisuFileObject psfFile = null;
					GrisuFileObject dcdFile = null;

					GrisuFileObject[] children = jobDir.getChildren();
					for (GrisuFileObject child : children) {
						if (child.getName().endsWith(".psf")) {
							psfFile = child;
							if (dcdFile != null)
								break;
							continue;
						} else if (child.getName().endsWith(".dcd")) {
							dcdFile = child;
							if (psfFile != null)
								break;
							continue;
						}
					}
					final String dcdArgument = dcdFile.getLocalRepresentation(
							true).getAbsolutePath();
					final String dcdFileName = dcdFile.getLocalRepresentation(
							false).getName();
					final String psfArgument = psfFile.getLocalRepresentation(
							true).getAbsolutePath();
					final String psfFileName = psfFile.getLocalRepresentation(
							false).getName();

					final File workingDir = psfFile.getLocalRepresentation(
							false).getParentFile();

					final String vmdPathForThread = vmdPath;

					// SwingUtilities.invokeLater(
					// new Thread() {
					// public void run() {
					// myLogger.debug("Starting vmd like: \"" + vmdPathForThread
					// + "\" -dcd \"" + dcdArgument + "\" -psf \""
					// + psfArgument);
					myLogger.debug("Starting vmd like: " + vmdPathForThread
							+ psfFileName + "-dcd" + dcdFileName + ". Using: "
							+ workingDir.toString() + " as working directory.");
					try {
						// List<String> args = new LinkedList<String>();
						// args.add(vmdPathForThread);
						// args.add("-dcd");
						// args.add("\"" + dcdArgument + "\"");
						// args.add("-psf");
						// args.add("\"" + psfArgument + "\"");
						// Process process = new
						// ProcessBuilder(vmdPathForThread,
						// "-dcd", "\"" + dcdArgument + "\"", "-psf", "\"" +
						// psfArgument + "\"").start();

						ProcessBuilder processBuilder = new ProcessBuilder(
								vmdPathForThread, psfFileName, "-dcd",
								dcdFileName);

						processBuilder.directory(workingDir);
						processBuilder.redirectErrorStream(true);

						final Process p = processBuilder.start();

						// Spawn thread to read output of spawned program
						new Thread() {

							public void run() {
								// hook into output from spawned program
								final InputStream is = p.getInputStream();
								final InputStreamReader isr = new InputStreamReader(
										is);
								final BufferedReader br = new BufferedReader(
										isr, 100/* buffsize in chars */);
								String line;
								try {
									try {
										// C/C++ can write this with putc
										while ((line = br.readLine()) != null) {
											System.out.println(line);

										}
									} catch (EOFException e) {
									}
									br.close();
								} catch (IOException e) {
									System.err
											.println("problem reading spawn output"
													+ e.getMessage());
									// System.exit( 1 );
								}
								// returning from run kills the thread.
							}
						}.start();

						// Runtime.getRuntime().exec(
						// new String[] {vmdPathForThread,
						// // "-psf",
						// psfArgument,
						// "-dcd",
						// dcdArgument});
						try {
							ClientPropertiesManager.getClientConfiguration()
									.setProperty("vmdPath", vmdPathForThread);
							ClientPropertiesManager.getClientConfiguration()
									.save();
						} catch (ConfigurationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

			// }
			// });
			vmdButton.setText("VMD");
		}
		return vmdButton;
	}
	/**
	 * @return
	 */

	public void jobStatusChanged(JobStatusEvent event) {

		if (event.getEventType() == JobStatusEvent.NO_CHANGE) {
			// do nothing
			myLogger.debug("No change for namdJob: "
					+ namdJob.getJob().getName());
		} else {
			int current = namdJob.getLastIteration();
			int percent = current / namdJob.getNumsteps();
			getProgressBar().setValue(current);

			getTimestepLabel().setText(current + "/" + namdJob.getTotalSteps());

			getEditorPane().setText(createInformationHTML());
		}

	}

	public void setNamdJob(NamdJob job) {
		this.namdJob = job;
		this.em = job.getJob().getEnvironmentManager();
		this.namdJob.addJobStatusListener(this);
		lineCharts.addElement(NamdJob.TOTAL_SERIES);
		lineCharts.addElement(NamdJob.TEMP_SERIES);
		lineCharts.addElement(NamdJob.PRESSURE_SERIES);

		int current = namdJob.getLastIteration();
		int percent = current / namdJob.getTotalSteps();
		getProgressBar().setIndeterminate(true);
		getProgressBar().setMaximum(namdJob.getTotalSteps());
		getProgressBar().setValue(current);
		getProgressBar().setIndeterminate(false);

		getTimestepLabel().setText(current + "/" + namdJob.getTotalSteps());

		getEditorPane().setText(createInformationHTML());

	}

}
