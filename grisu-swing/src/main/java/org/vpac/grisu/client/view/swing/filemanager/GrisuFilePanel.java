package org.vpac.grisu.client.view.swing.filemanager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.files.FileTransfer;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.view.swing.files.FileChooserEvent;
import org.vpac.grisu.client.view.swing.files.FileChooserParent;
import org.vpac.grisu.client.view.swing.files.SiteFileChooserPanel;
import org.vpac.grisu.client.view.swing.preview.PreviewPanel;
import org.vpac.grisu.client.view.swing.utils.Utils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GrisuFilePanel extends JPanel implements FileChooserParent {

	static final Logger myLogger = Logger.getLogger(GrisuFilePanel.class
			.getName());

	public static final String PREVIEW_PANEL = "Preview";
	public static final String FILE_PANEL = "Files";

	private EnvironmentManager em = null;

	private JComboBox rightSideTypeComboBox;
	private JButton rightCopyButton;
	private JButton leftCopyButton;
	private PreviewPanel previewPanel;
	private SiteFileChooserPanel rightSiteFileChooserPanel;
	private JPanel holderPanel;
	private SiteFileChooserPanel leftSiteFileChooserPanel;
	private JPanel panel;
	private JPanel righPanel;
	private JPanel leftPanel;
	private JSplitPane splitPane;

	/**
	 * Create the panel
	 */
	public GrisuFilePanel(EnvironmentManager em) {
		super();
		this.em = em;
		setLayout(new BorderLayout());
		add(getSplitPane());
		//
		GrisuFileObject defaultFile = em.getFileManager().getFileObject(
				new File(System.getProperty("user.home")).toURI());
		getLeftSiteFileChooserPanel().changeCurrentDirectory(defaultFile);
		getRightSiteFileChooserPanel().changeCurrentDirectory(defaultFile);
	}

	protected JPanel getHolderPanel() {
		if (holderPanel == null) {
			holderPanel = new JPanel();
			holderPanel.setLayout(new CardLayout());
			holderPanel.add(getRightSiteFileChooserPanel(),
					getRightSiteFileChooserPanel().getName());
			holderPanel.add(getPreviewPanel(), getPreviewPanel().getName());
		}
		return holderPanel;
	}

	protected JButton getLeftCopyButton() {
		if (leftCopyButton == null) {
			leftCopyButton = new JButton();
			leftCopyButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					new Thread() {
						public void run() {
							GrisuFileObject targetDirectory = getRightCurrentDirectory();
							GrisuFileObject[] sourceFiles = getLeftSiteFileChooserPanel()
									.getSelectedFiles();
							try {
								getLeftSiteFileChooserPanel().setBusy(true);
								getRightSiteFileChooserPanel().setBusy(true);
								GrisuFilePanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.WAIT_CURSOR));
								em
										.getFileTransferManager()
										.addTransfer(
												sourceFiles,
												targetDirectory,
												FileTransfer.OVERWRITE_EVERYTHING,
												true);
								// FileManagerTransferHelpers
								// .transferFiles(em.getServiceInterface(),
								// sourceFiles, targetDirectory, true);
								getRightSiteFileChooserPanel()
										.refreshCurrentDirectory();
							} catch (RuntimeException fte) {
								// this means that the user cancelled an
								// upload/download
								myLogger
										.debug("File transfer exception. Probably because user cancelled it.");
								// doing nothing here
							} catch (Exception e1) {
								Utils.showErrorMessage(em, GrisuFilePanel.this,
										"couldNotTransfer", e1);
							} finally {
								getLeftSiteFileChooserPanel().setBusy(false);
								getRightSiteFileChooserPanel().setBusy(false);
								GrisuFilePanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}

						}
					}.start();

				}
			});
			leftCopyButton.setText("copy ->");
		}
		return leftCopyButton;
	}

	public GrisuFileObject getLeftCurrentDirectory() {
		return getLeftSiteFileChooserPanel().getCurrentDirectory();
	}

	protected JPanel getLeftPanel() {
		if (leftPanel == null) {
			leftPanel = new JPanel();
			leftPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("18dlu"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("94dlu:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("default:grow(1.0)"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC }));
			leftPanel.add(getLeftSiteFileChooserPanel(), new CellConstraints(2,
					2, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
			leftPanel.add(getLeftCopyButton(), new CellConstraints(4, 4,
					CellConstraints.RIGHT, CellConstraints.DEFAULT));
		}
		return leftPanel;
	}

	protected SiteFileChooserPanel getLeftSiteFileChooserPanel() {
		if (leftSiteFileChooserPanel == null) {
			leftSiteFileChooserPanel = new SiteFileChooserPanel(em);
			leftSiteFileChooserPanel.addUserInputListener(this);
		}
		return leftSiteFileChooserPanel;
	}

	protected PreviewPanel getPreviewPanel() {
		if (previewPanel == null) {
			previewPanel = new PreviewPanel();
			previewPanel.setName(PREVIEW_PANEL);
		}
		return previewPanel;
	}

	protected JPanel getRighPanel() {
		if (righPanel == null) {
			righPanel = new JPanel();
			righPanel.setLayout(new FormLayout(
					new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
							ColumnSpec.decode("40dlu:grow(1.0)"),
							FormFactory.RELATED_GAP_COLSPEC,
							ColumnSpec.decode("75dlu"),
							FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
							FormFactory.RELATED_GAP_ROWSPEC,
							RowSpec.decode("default:grow(1.0)"),
							FormFactory.RELATED_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.RELATED_GAP_ROWSPEC }));
			righPanel.add(getHolderPanel(), new CellConstraints(2, 2, 3, 1,
					CellConstraints.FILL, CellConstraints.FILL));
			righPanel.add(getRightCopyButton(), new CellConstraints(2, 4,
					CellConstraints.LEFT, CellConstraints.DEFAULT));
			righPanel.add(getRightSideTypeComboBox(), new CellConstraints(4, 4,
					CellConstraints.FILL, CellConstraints.DEFAULT));
		}
		return righPanel;
	}

	protected JButton getRightCopyButton() {
		if (rightCopyButton == null) {
			rightCopyButton = new JButton();
			rightCopyButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					new Thread() {
						public void run() {
							GrisuFileObject targetDirectory = getLeftCurrentDirectory();
							GrisuFileObject[] sourceFiles = getRightSiteFileChooserPanel()
									.getSelectedFiles();
							try {
								getLeftSiteFileChooserPanel().setBusy(true);
								getRightSiteFileChooserPanel().setBusy(true);
								GrisuFilePanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.WAIT_CURSOR));
								em
										.getFileTransferManager()
										.addTransfer(
												sourceFiles,
												targetDirectory,
												FileTransfer.OVERWRITE_EVERYTHING,
												true);
								// FileManagerTransferHelpers.transferFiles(
								// em.getServiceInterface(),
								// sourceFiles, targetDirectory, true);
								getLeftSiteFileChooserPanel()
										.refreshCurrentDirectory();
							} catch (RuntimeException fte) {
								// this means that the user cancelled an
								// upload/download
								myLogger
										.debug("File transfer exception. Probably because user cancelled it.");
								// doing nothing here
							} catch (Exception e1) {
								Utils.showErrorMessage(em, GrisuFilePanel.this,
										"couldNotTransfer", e1);
							} finally {
								getLeftSiteFileChooserPanel().setBusy(false);
								getRightSiteFileChooserPanel().setBusy(false);
								GrisuFilePanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}
						}
					}.start();

				}
			});
			rightCopyButton.setText("<- copy");
		}
		return rightCopyButton;
	}

	public GrisuFileObject getRightCurrentDirectory() {
		return getRightSiteFileChooserPanel().getCurrentDirectory();
	}

	protected JComboBox getRightSideTypeComboBox() {
		if (rightSideTypeComboBox == null) {
			rightSideTypeComboBox = new JComboBox(new String[] { FILE_PANEL,
					PREVIEW_PANEL });
			rightSideTypeComboBox.setMinimumSize(new Dimension(100, 0));
			rightSideTypeComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					switchPanels((String) getRightSideTypeComboBox()
							.getSelectedItem());
				}
			});
		}
		return rightSideTypeComboBox;
	}

	protected SiteFileChooserPanel getRightSiteFileChooserPanel() {
		if (rightSiteFileChooserPanel == null) {
			rightSiteFileChooserPanel = new SiteFileChooserPanel(em);
			rightSiteFileChooserPanel.setName(FILE_PANEL);
			rightSiteFileChooserPanel.changeToSite(FileConstants.LOCAL_NAME);
			// File file = new File(System.getProperty("user.home"));
			// rightSiteFileChooserPanel.changeCurrentDirectory(EnvironmentManager
			// .getDefaultManager().getFileManager().getFileObject(file.toURI()));
		}
		return rightSiteFileChooserPanel;
	}

	protected JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getLeftPanel());
			splitPane.setRightComponent(getRighPanel());
			splitPane.setDividerLocation(380);
		}
		return splitPane;
	}

	public void switchPanels(String panel) {
		CardLayout cl = (CardLayout) (getHolderPanel().getLayout());
		cl.show(getHolderPanel(), panel);

		if (PREVIEW_PANEL.equals(panel)) {
			// clear panel
			getPreviewPanel().previewFile(null);
			getRightCopyButton().setEnabled(false);
			getLeftCopyButton().setEnabled(false);
		} else {
			getRightCopyButton().setEnabled(true);
			getLeftCopyButton().setEnabled(true);
		}

	}

	public void userInput(FileChooserEvent event) {

		if (FileChooserEvent.SELECTED_FILE == event.getType()) {
			getRightSideTypeComboBox().setSelectedItem(PREVIEW_PANEL);
			getPreviewPanel().previewFile(event.getSelectedFile());
		}

	}

}
