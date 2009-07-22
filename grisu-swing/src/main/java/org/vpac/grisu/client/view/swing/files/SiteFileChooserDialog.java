

package org.vpac.grisu.client.view.swing.files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.view.swing.login.LoginDialog;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SiteFileChooserDialog extends JDialog implements FileChooserParent {

	private JButton cancelButton;
	private JButton selectButton;
	private SiteFileChooserPanel siteFileChooser;
	private EnvironmentManager em = null;
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			LoginDialog ld = new LoginDialog();
			ld.setVisible(true);
			
			if ( ld.userCancelledLogin() ) {
				System.exit(0);
			}
			
//			EnvironmentManager.setDefaultServiceInterface(ld.getServiceInterface());
			
			ld.dispose();
			SiteFileChooserDialog dialog = new SiteFileChooserDialog(new EnvironmentManager(ld.getServiceInterface()));
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog
	 */
	public SiteFileChooserDialog(EnvironmentManager em) {
		super();
		this.em = em;
		this.setModal(true);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		setBounds(100, 100, 552, 454);
		getContentPane().add(getSiteFileChooser(), new CellConstraints(1, 1, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		getSiteFileChooser().addUserInputListener(this);
		getContentPane().add(getSelectButton(), new CellConstraints(3, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		getContentPane().add(getCancelButton(), new CellConstraints(1, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));

//		getSiteFileChooser().changeToSite("eRSA");
//
//		try {
//			getSiteFileChooser().changeCurrentDirectory(em.getFileManager().getFileObject(new URI("gsiftp://ng2.sapac.edu.au/data/grid/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner")));
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void setSite(String site) {
		getSiteFileChooser().changeToSite(site);
	}
	
	public void setCurrentDirectory(GrisuFileObject directory) {
		getSiteFileChooser().changeCurrentDirectory(directory);
	}

	public void userInput(FileChooserEvent event) {
		fireUserInput(event.getType(), new GrisuFileObject[]{event.getSelectedFile()});
	}
	
	public GrisuFileObject getCurrentDirectory() {
		return getSiteFileChooser().getCurrentDirectory();
	}
	
	public void setSelectionMode(int selectionMode) {
		getSiteFileChooser().setSelectionMode(selectionMode);
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
	protected SiteFileChooserPanel getSiteFileChooser() {
		if (siteFileChooser == null) {
			siteFileChooser = new SiteFileChooserPanel(em);
		}
		return siteFileChooser;
	}
	protected JButton getSelectButton() {
		if (selectButton == null) {
			selectButton = new JButton();
			selectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GrisuFileObject[] files = getSiteFileChooser().getSelectedFiles();
					if ( files == null || files.length == 0 ) {
						fireUserInput(FileChooserEvent.CANCELLED, null);
					} else if ( files.length == 1) {
						fireUserInput(FileChooserEvent.SELECTED_FILE, files);
					} else {
						fireUserInput(FileChooserEvent.SELECTED_FILES, files);
					}
				}
			});
			selectButton.setText("Select");
		}
		return selectButton;
	}
	protected JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireUserInput(FileChooserEvent.CANCELLED, null);
				}
			});
			cancelButton.setText("Cancel");
		}
		return cancelButton;
	}
}
