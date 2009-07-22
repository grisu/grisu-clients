package org.vpac.grisu.client.view.swing.files;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.InformationError;
import org.vpac.grisu.client.control.files.FileManagerDeleteHelpers;
import org.vpac.grisu.client.control.files.FileManagerListener;
import org.vpac.grisu.client.control.files.FileTransfer;
import org.vpac.grisu.client.control.utils.ClipboardUtils;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.model.files.FileSystemBackend;
import org.vpac.grisu.client.model.files.FileSystemException;
import org.vpac.grisu.client.model.files.FileSystemListFrontend;
import org.vpac.grisu.client.model.files.events.FileSystemBackendEvent;
import org.vpac.grisu.client.view.swing.preview.PreviewDialog;
import org.vpac.grisu.client.view.swing.utils.Utils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SiteFileChooserPanel extends JPanel implements FileManagerListener {

	private static final long serialVersionUID = 1L;

	private JMenuItem createDirectoryMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem downloadMenuItem;
	private JMenuItem viewMenuItem;
	private JMenuItem copyURLMenuItem;
	private JPopupMenu popupMenu;
	private JButton refreshButton;
	private JScrollPane scrollPane;
	static final Logger myLogger = Logger.getLogger(SiteFileChooserPanel.class
			.getName());

	private JList list;
	private JLabel pathTextField;
	private JComboBox fileSystemComboBox;
	private JComboBox siteComboBox;

	private String rememberedDirectory = System.getProperty("user.home");

	// private BackendFileObject selectedFileForMenu = null;

	private FileSystemListFrontend currentFileSystem = null;

	private boolean comboboxLock = false;

	// private ArrayList<FileSystemListFrontend> filesystems = new
	// ArrayList<FileSystemListFrontend>();
	private Map<String, FileSystemListFrontend> filesystems = new TreeMap<String, FileSystemListFrontend>();

	private EnvironmentManager em = null;

	/**
	 * Constructor for WindowBuilder Pro. Don't use that one.
	 */
	public SiteFileChooserPanel() {
		super();
		comboboxLock = true;
		initialize();
		comboboxLock = false;
	}

	private void initialize() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("22dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("7dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("19dlu"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC }));
		{
			final JLabel label = new JLabel();
			label.setText("Site:");
			add(label, new CellConstraints(2, 2));
		}
		{
			siteComboBox = new JComboBox();
			siteComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent arg0) {
					if (!comboboxLock) {
						// SwingUtilities.invokeLater(new Runnable() {
						// public void run() {
						SiteFileChooserPanel.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						boolean oldComboboxLock = comboboxLock;
						comboboxLock = true;
						changeToSite(getSelectedSite());
						comboboxLock = oldComboboxLock;
						SiteFileChooserPanel.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						// }
						// });
					}
				}
			});
			add(siteComboBox, new CellConstraints(4, 2, CellConstraints.FILL,
					CellConstraints.DEFAULT));
		}
		{
			final JLabel filesystemLabel = new JLabel();
			filesystemLabel.setToolTipText("FileSystem");
			filesystemLabel.setText("Share:");
			add(filesystemLabel, new CellConstraints(2, 4));
		}
		{
			fileSystemComboBox = new JComboBox();
			fileSystemComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if (!comboboxLock) {
						// SwingUtilities.invokeLater(new Runnable() {
						// public void run() {
						changeToFileSystem(getSelectedFileSystem());
						// }
						// });
					}
				}
			});
			add(fileSystemComboBox, new CellConstraints(4, 4, 5, 1,
					CellConstraints.FILL, CellConstraints.DEFAULT));
		}
		{
			final JLabel pathLabel = new JLabel();
			pathLabel.setText("Path:");
			add(pathLabel, new CellConstraints(6, 2));
		}
		{
			pathTextField = new JLabel();
			pathTextField.setBorder(new EmptyBorder(0, 0, 0, 0));
			add(pathTextField, new CellConstraints(8, 2, 3, 1,
					CellConstraints.FILL, CellConstraints.DEFAULT));
		}
		{
			add(getScrollPane(), new CellConstraints("2, 6, 9, 1, fill, fill"));
		}
		//

		ArrayList<FileSystemBackend> allCurrentFileSystems = em
				.getFileManager().getFileSystems();
		for (FileSystemBackend fs : allCurrentFileSystems) {
			try {
				// filesystems.add(new FileSystemListFrontend(fs));
				filesystems.put(fs.getAlias(), new FileSystemListFrontend(fs));
			} catch (Exception e) {
				// necessary because of the gridftp server bug that returns
				// wrong results sometimes
				e.printStackTrace();
				Utils.showErrorMessage(em, this, "gridftpServerBug", e);
				System.exit(1);
			}
		}
		em.getFileManager().addFileManagerListener(this);

		refreshComboBoxes();
		add(getRefreshButton(), new CellConstraints(10, 4,
				CellConstraints.RIGHT, CellConstraints.CENTER));
	}

	/**
	 * Create the panel
	 */
	public SiteFileChooserPanel(EnvironmentManager em) {
		super();
		this.em = em;
		initialize();
	}

	public synchronized void fileSystemBackendsChanged(
			FileSystemBackendEvent event) {
		FileSystemBackend fs = event.getFilesystem();
		if (event.getType() == FileSystemBackendEvent.FILESYSTEM_ADDED) {
			FileSystemListFrontend temp = new FileSystemListFrontend(fs);
			if (filesystems.get(fs.getAlias()) == null) {
				// if ( filesystems.indexOf(temp) == -1) {
				// filesystems.add(new FileSystemListFrontend(fs));
				filesystems.put(fs.getAlias(), temp);
			}
		} else if (event.getType() == FileSystemBackendEvent.FILESYSTEM_REMOVED) {
			// filesystems.remove(new FileSystemListFrontend(fs));
			filesystems.remove(fs.getAlias());
		} else {
			myLogger
					.debug("Don't know what to do with event... Doing nothing.");
		}

		refreshComboBoxes();

	}

	public String getSelectedSite() {
		return (String) siteComboBox.getSelectedItem();
	}

	public FileSystemListFrontend getSelectedFileSystem() {
		return (FileSystemListFrontend) fileSystemComboBox.getSelectedItem();
	}

	public GrisuFileObject getSelectedFile() {
		return (GrisuFileObject) list.getSelectedValue();
	}

	public GrisuFileObject[] getSelectedFiles() {

		int[] indexes = list.getSelectedIndices();
		Object[] objects = list.getSelectedValues();
		GrisuFileObject[] gfos = new GrisuFileObject[objects.length];
		for (int i = 0; i < objects.length; i++) {
			gfos[i] = (GrisuFileObject) objects[i];
		}
		return gfos;
	}

	private void refreshComboBoxes() {

		myLogger.debug("Starting refreshing of comboboxes...");
		String currentlySelected = getSelectedSite();
		FileSystemListFrontend currentFileSystem = getSelectedFileSystem();
		Set<String> siteNames = em.getFileManager().getSites();
		// sites
		DefaultComboBoxModel sites = new DefaultComboBoxModel(siteNames
				.toArray());
		siteComboBox.setModel(sites);

		// filesystems
		if (currentlySelected != null && currentFileSystem != null) {
			if (siteNames.contains(currentlySelected)) {
				changeToSite(currentlySelected);
			}

			// changeToSite(getSelectedSite());
			if (currentFileSystem != null) {
				if (filesystems.get(currentFileSystem.getFileSystemBackend()
						.getAlias()) != null) {
					changeToFileSystem(currentFileSystem);
				}
				// if (filesystems.contains(currentFileSystem)) {
				// changeToFileSystem(currentFileSystem);
				// }
			}
		} else {
			changeToSite("Local");
		}

		myLogger.debug("Refreshing of comboboxes finished.");

	}

	public synchronized void changeToSite(final String site) {

		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultComboBoxModel fileLists = new DefaultComboBoxModel();
			fileSystemComboBox.setModel(fileLists);
			if (!siteComboBox.getSelectedItem().equals(site))
				siteComboBox.setSelectedItem(site);
			Vector<FileSystemListFrontend> filesystemsCopy = null;
			synchronized (SiteFileChooserPanel.this) {
				filesystemsCopy = new Vector<FileSystemListFrontend>(
						filesystems.values());
			}
			for (FileSystemListFrontend fs : filesystemsCopy) {
				try {
					if (fs.getFileSystemBackend().getSite().equals(site)) {
						fileLists.addElement(fs);
					}
				} catch (InformationError e) {
					myLogger.error("Information error: "
							+ e.getLocalizedMessage());
					continue;
				}
			}

			int size = -1;
			try {
				size = fileSystemComboBox.getModel().getSize();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (size > 0) {
				changeToFileSystem((FileSystemListFrontend) fileSystemComboBox
						.getModel().getElementAt(0));
			}
		} catch (Exception e) {
			// e.printStackTrace();
			Utils.showErrorMessage(em, SiteFileChooserPanel.this,
					"cantChangeToFilesystem", e);
			changeToSite("Local");
		} finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	private synchronized void changeToFileSystem(final FileSystemListFrontend fs) {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (getCurrentSite().equals(fs.getFileSystemBackend().getSite())) {
				myLogger.debug("Setting model for file listing...");
				list.setModel(fs);
				currentFileSystem = fs;
				String path = currentFileSystem
						.getCurrentDirectoryRelativeToRoot();
				if (path != null) {
					try {
						path = URLDecoder.decode(path, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				pathTextField.setText("./" + path);
				pathTextField.setToolTipText("./" + path);
			}
		} catch (Exception e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			Utils.showErrorMessage(em, SiteFileChooserPanel.this,
					"cantChangeToFilesystem", e);

		} finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	public void changeCurrentDirectory(GrisuFileObject file) {

		if (currentFileSystem == null) {
			myLogger.error("Current file system is null.");
			return;
		}

		if (file == null) {
			myLogger.error("File is null.");
		}
		if (!currentFileSystem.getFileSystemBackend().equals(
				file.getFileSystemBackend())) {

			String site;
			try {
				site = file.getFileSystemBackend().getSite();
			} catch (InformationError e) {
				myLogger.error(e.getLocalizedMessage());
				Utils.showErrorMessage(em, this,
						"unknownErrorChangingDirectory", e);
				return;
			}
			changeToSite(site);
		}
		try {
			currentFileSystem.setCurrentDirectory(file);
			list.clearSelection();
			// currentFileSystem.invalidate();
		} catch (FileSystemException e) {
			Utils.showErrorMessage(em, this, "couldNotChangeDirectory", e);
		}
		String path = currentFileSystem.getCurrentDirectoryRelativeToRoot();
		if (path != null)
			try {
				path = URLDecoder.decode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		pathTextField.setText("./" + path);
		pathTextField.setToolTipText("./" + path);
	}

	public String getCurrentSite() {
		return (String) siteComboBox.getSelectedItem();
	}

	public GrisuFileObject getCurrentDirectory() {
		if (currentFileSystem != null) {
			return currentFileSystem.getCurrentDirectory();
		} else {
			return null;
		}
	}

	public void refreshCurrentDirectory() {

		if (currentFileSystem != null) {
			currentFileSystem.refreshCurrentDirectory();
		}
	}

	public FileSystemListFrontend getCurrentFileSystem() {
		return currentFileSystem;
	}

	public void setSelectionMode(int selectionMode) {
		list.setSelectionMode(selectionMode);
	}

	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<FileChooserParent> actionListeners;

	private void fireUserInput(int type, GrisuFileObject[] objects) {
		// if we have no mountPointsListeners, do nothing...
		if (actionListeners != null && !actionListeners.isEmpty()) {
			// create the event object to send
			FileChooserEvent event = new FileChooserEvent(type, objects);

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<FileChooserParent> targets;
			synchronized (this) {
				targets = (Vector<FileChooserParent>) actionListeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			Enumeration<FileChooserParent> e = targets.elements();
			while (e.hasMoreElements()) {
				FileChooserParent l = (FileChooserParent) e.nextElement();
				try {
					l.userInput(event);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	// register a listener
	synchronized public void addUserInputListener(FileChooserParent l) {
		if (actionListeners == null)
			actionListeners = new Vector();
		actionListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeUserInputListener(FileChooserParent l) {
		if (actionListeners == null) {
			actionListeners = new Vector<FileChooserParent>();
		}
		actionListeners.removeElement(l);
	}

	private void userDoubleClicksFile() {
		new Thread() {
			public void run() {
				// SiteFileChooserPanel.this.setCursor(Cursor
				// .getPredefinedCursor(Cursor.WAIT_CURSOR));

				try {
					setBusy(true);

					GrisuFileObject selected = getSelectedFile();
					if (selected.getType() == FileConstants.TYPE_FOLDER) {
						changeCurrentDirectory(selected);
						// setBusy(false);
						fireUserInput(FileChooserEvent.CHANGED_FOLDER,
								new GrisuFileObject[] { selected });
					} else if (selected.getType() == FileConstants.TYPE_FILE) {
						// setBusy(false);
						fireUserInput(FileChooserEvent.SELECTED_FILE,
								new GrisuFileObject[] { selected });
					} else {
						// setBusy(false);
						throw new RuntimeException(
								"File is of no know type. That's a worry. Should never happen...");
					}

				} finally {
					setBusy(false);
				}
				// SiteFileChooserPanel.this.setCursor(Cursor
				// .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}.start();
	}

	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			addPopup(scrollPane, getPopupMenu());
			list = new JList();
			list.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {

					if (e.getKeyCode() == KeyEvent.VK_ENTER
							|| e.getKeyCode() == KeyEvent.VK_SPACE) {
						userDoubleClicksFile();
					}
				}
			});

			// $hide>>$

			list.setCellRenderer(new BackendFileObjectCellRenderer());
			// $hide<<$
			list.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					// // doubleclick
					if (e.getClickCount() == 2) {
						userDoubleClicksFile();
					}
				}

				public void mousePressed(final MouseEvent e) {
					// right click
					if (e.getButton() == MouseEvent.BUTTON3) {
						// int index = list.locationToIndex(e.getPoint());
						// list.setSelectedIndex(index);
						showPopup(e);
					}
				}
			});
			scrollPane.setViewportView(list);
		}
		return scrollPane;
	}

	private void showPopup(MouseEvent e) {
		// if (e.isPopupTrigger()) {

		if (list.getSelectedValues().length == 1) {
			// show normal right-click menu
			GrisuFileObject selectedFileForMenu = null;

			selectedFileForMenu = (GrisuFileObject) list.getSelectedValue();
			if (selectedFileForMenu.getType() == FileConstants.TYPE_FOLDER) {
				getViewMenuItem().setEnabled(false);
			} else {
				getViewMenuItem().setEnabled(true);
			}
			try {
				if (selectedFileForMenu.getFileSystemBackend().getSite()
						.equals(FileConstants.LOCAL_NAME)) {
					getDownloadMenuItem().setEnabled(false);
				} else {
					getDownloadMenuItem().setEnabled(true);
				}
			} catch (InformationError e1) {
				myLogger.error(e1.getLocalizedMessage());
				getDownloadMenuItem().setEnabled(false);
			}
			if (selectedFileForMenu.equals(getCurrentDirectory())) {
				getDeleteMenuItem().setEnabled(false);
			} else {
				getDeleteMenuItem().setEnabled(true);
			}


		} else {
			// show multi-file-selection-menu 
			getViewMenuItem().setEnabled(false);
			getDownloadMenuItem().setEnabled(false);
			getDeleteMenuItem().setEnabled(true);
		}
		
		getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
	}

	protected JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent arg0) {
					refreshCurrentDirectory();
				}
			});
			URL picURL = getClass().getResource("/images/refresh.png");
			ImageIcon refresh = new ImageIcon(picURL);
			refreshButton.setIcon(refresh);
		}
		return refreshButton;
	}

	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getCreateDirectoryMenuItem());
			popupMenu.add(getCopyURLMenuItem());
			popupMenu.add(getViewMenuItem());
			popupMenu.add(getDownloadMenuItem());
			popupMenu.add(getDeleteMenuItem());
		}
		return popupMenu;
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	protected JMenuItem getCopyURLMenuItem() {

		if (copyURLMenuItem == null) {
			copyURLMenuItem = new JMenuItem();
			copyURLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					Object[] selectedFiles = list.getSelectedValues();
					StringBuffer fileUrls = new StringBuffer();
					for ( Object selFile : selectedFiles ) {
						GrisuFileObject file = (GrisuFileObject)selFile;
						fileUrls.append(file.getURI().toString()+" ");
					}
					ClipboardUtils.defaultClipboard.setClipboardContents(fileUrls.toString());
				}
			});
			copyURLMenuItem.setText("Copy url to clipboard");
		}
		return copyURLMenuItem;
	}

	protected JMenuItem getViewMenuItem() {
		if (viewMenuItem == null) {
			viewMenuItem = new JMenuItem();
			viewMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					new Thread() {
						public void run() {
							try {
								setBusy(true);

								PreviewDialog pd = new PreviewDialog();
								pd.setFile((GrisuFileObject)list.getSelectedValue());
								setBusy(false);
								pd.setVisible(true);

							} finally {
								setBusy(false);
							}
						}

					}.start();

				}
			});
			viewMenuItem.setText("View");
		}
		return viewMenuItem;
	}

	protected JMenuItem getDownloadMenuItem() {
		if (downloadMenuItem == null) {
			downloadMenuItem = new JMenuItem();
			downloadMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					GrisuFileObject sourceFile = getSelectedFile();

					String filename = sourceFile.getName();

					JFileChooser chooser = new JFileChooser(new File(
							rememberedDirectory, filename));
					chooser.setDialogType(JFileChooser.SAVE_DIALOG);
					chooser.setSelectedFile(new File(rememberedDirectory,
							filename));
					int returnVal = chooser
							.showSaveDialog(SiteFileChooserPanel.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();

						final GrisuFileObject targetDirectory = em
								.getFileManager().getFileObject(
										file.getParentFile().toURI());
						final GrisuFileObject sourceFileTemp = sourceFile;
						new Thread() {
							public void run() {
								try {
									// SiteFileChooserPanel.this
									// .setCursor(Cursor
									// .getPredefinedCursor(Cursor.WAIT_CURSOR));
									setBusy(true);
									em
											.getFileTransferManager()
											.addTransfer(
													new GrisuFileObject[] { sourceFileTemp },
													targetDirectory,
													FileTransfer.OVERWRITE_EVERYTHING,
													true);
									// FileManagerTransferHelpers
									// .transferFiles(
									// em.getServiceInterface(),
									// new BackendFileObject[] { sourceFileTemp
									// },
									// targetDirectory, true);
									refreshCurrentDirectory();
								} catch (Exception e1) {
									Utils.showErrorMessage(em,
											SiteFileChooserPanel.this,
											"couldNotTransfer", e1);
								} finally {
									setBusy(false);
									// SiteFileChooserPanel.this
									// .setCursor(Cursor
									// .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								}
							}
						}.start();
					}
				}
			});
			downloadMenuItem.setText("Download");
		}
		return downloadMenuItem;
	}

	protected JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem();
			deleteMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					new Thread() {
						public void run() {
							try {
								SiteFileChooserPanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.WAIT_CURSOR));
								
								final GrisuFileObject[] selectedFiles = getSelectedFiles();
								
								StringBuffer message = new StringBuffer();
								if ( selectedFiles.length == 1 ) {
									message.append("You are about to delete this file:\n\n");
								} else {
									message.append("You are about to delete these files:\n\n");
								}
								for (GrisuFileObject file : selectedFiles ) {
									message.append(file.getName() + "\n");
								}
								message
										.append("\n\nDo you really want to do that?");
								int answer = JOptionPane.showConfirmDialog(SiteFileChooserPanel.this, message.toString(),
										"Kill Jobs", JOptionPane.YES_NO_OPTION);

								if (answer != JOptionPane.YES_OPTION) {
									return;
								}
								FileManagerDeleteHelpers
										.deleteFiles(
												em.getServiceInterface(),
//												new BackendFileObject[] { getSelectedFile() },
												selectedFiles,
												true);
								list.clearSelection();
								refreshCurrentDirectory();
							} finally {
								SiteFileChooserPanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}
						}
					}.start();
				}
			});
			deleteMenuItem.setText("Delete");
		}
		return deleteMenuItem;
	}

	/**
	 * @return
	 */
	protected JMenuItem getCreateDirectoryMenuItem() {
		if (createDirectoryMenuItem == null) {
			createDirectoryMenuItem = new JMenuItem();
			createDirectoryMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					list.clearSelection();

					String newDirName = (String) JOptionPane.showInputDialog(
							SiteFileChooserPanel.this,
							"Please enter the name of the new directory:",
							"Create directory", JOptionPane.PLAIN_MESSAGE,
							null, null, null);

					if (newDirName != null && newDirName.length() != 0) {

						if (FileConstants.LOCAL_NAME.equals(getCurrentSite())) {
							File dirFile = getCurrentDirectory()
									.getLocalRepresentation(true);
							File newDirFile = new File(dirFile, newDirName);
							if (newDirFile.exists()) {
								Utils.showErrorMessage(em,
										SiteFileChooserPanel.this,
										"directoryAlreadyExists", null);
								return;
							}
							newDirFile.mkdir();
							refreshCurrentDirectory();
						} else {

							SiteFileChooserPanel.this.setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));

							GrisuFileObject newDirObject = getCurrentDirectory()
									.getChild(newDirName, true);

							if (newDirObject != null) {
								SiteFileChooserPanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								Utils.showErrorMessage(em,
										SiteFileChooserPanel.this,
										"directoryAlreadyExists", null);
								return;
							}

							try {

								try {
									em.getServiceInterface().mkdir(
											getCurrentDirectory().getURI()
													.toString()
													+ "/" + newDirName);
									refreshCurrentDirectory();
								} catch (Exception e1) {
									Utils.showErrorMessage(em,
											SiteFileChooserPanel.this,
											"cantCreateRemoteDirectory", null);
								}
							} finally {
								SiteFileChooserPanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}
						}
					}
				}
			});
			createDirectoryMenuItem.setText("Create directory");
		}
		return createDirectoryMenuItem;
	}

	public void setBusy(final boolean busy) {

		// SwingUtilities.invokeLater(
		// new Runnable() {
		// public void run() {
		if (busy) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		getScrollPane().setEnabled(!busy);
		getRefreshButton().setEnabled(!busy);
		siteComboBox.setEnabled(!busy);
		fileSystemComboBox.setEnabled(!busy);
		pathTextField.setEnabled(!busy);
		list.setEnabled(!busy);
		// }
		// }
		// );

	}
}
