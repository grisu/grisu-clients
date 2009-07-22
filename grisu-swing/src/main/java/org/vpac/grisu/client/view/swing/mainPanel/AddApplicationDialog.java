

package org.vpac.grisu.client.view.swing.mainPanel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.vpac.grisu.control.ServiceInterface;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class AddApplicationDialog extends JDialog {

	private JScrollPane scrollPane;
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jPanel = null;

	private JList jList = null;

	private JButton jButton = null;

	private JButton jButton1 = null;

	private ServiceInterface serviceInterface = null;
	
	private boolean cancel_button = true;
	
	/**
	 * @param owner
	 */
	public AddApplicationDialog(Frame owner, ServiceInterface serviceInterface) {
		super(owner, true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				getJList().clearSelection();
			}
		});
		setTitle("Add application");
		this.serviceInterface = serviceInterface;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(273, 288);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					new ColumnSpec("79px"),
					FormFactory.UNRELATED_GAP_COLSPEC,
					new ColumnSpec("62px:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC},
				new RowSpec[] {
					new RowSpec("10dlu"),
					new RowSpec("25px:grow(1.0)"),
					FormFactory.PARAGRAPH_GAP_ROWSPEC,
					new RowSpec("29px"),
					FormFactory.RELATED_GAP_ROWSPEC}));
			jPanel.add(getJButton(), new CellConstraints("4, 4, 1, 1, right, bottom"));
			jPanel.add(getScrollPane(), new CellConstraints("2, 2, 4, 1, fill, fill"));
		}
		return jPanel;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
//			Document apps = serviceInterface.listHostedApplications();
//			
//			NodeList app_nodes = apps.getFirstChild().getChildNodes();
//			int size_apps = app_nodes.getLength();
//			String[] app_names = new String[size_apps];
//			
//			for ( int i=0; i< size_apps; i++ ) {
//				app_names[i] = app_nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
//			}
			
			String[] app_names = serviceInterface.listHostedApplicationTemplates();
			
			jList = new JList(app_names);
		}
		return jList;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Add");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancel_button = false;
					AddApplicationDialog.this.setVisible(false);
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Cancel");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancel_button = true;
					AddApplicationDialog.this.setVisible(false);
				}
			});
		}
		return jButton1;
	}

	public boolean cancelButtonPressed() {
		return cancel_button;
	}
	
	public void resetCancelButton() {
		cancel_button = true;
	}
	
	public String[] getApplicationName() {
		Object[] objects = getJList().getSelectedValues();
		String[] strings = new String[objects.length];
		for ( int i=0; i<objects.length; i++ ) {
			strings[i] = (String)objects[i];
		}
		return strings;
	}
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getJList());
		}
		return scrollPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
