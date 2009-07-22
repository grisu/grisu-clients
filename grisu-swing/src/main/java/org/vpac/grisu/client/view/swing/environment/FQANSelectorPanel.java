package org.vpac.grisu.client.view.swing.environment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.status.StatusEvent;
import org.vpac.grisu.client.control.status.StatusListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FQANSelectorPanel extends JPanel {
	
	public static final String OK_ACTION = "OK";
	public static final String CANCEL_ACTION = "Cancel";

	private JList list;
	private JButton cancelButton;
	private JButton okButton;
	private JScrollPane scrollPane;
	
	private EnvironmentManager em = null;
	private StatusListener listener = null;
	
	private DefaultListModel fqanModel = new DefaultListModel();
	
	/**
	 * Create the panel
	 */
	public FQANSelectorPanel() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getScrollPane(), new CellConstraints(2, 2, 5, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getOkButton(), new CellConstraints(6, 4));
		add(getCancelButton(), new CellConstraints(4, 4));
		//
	}
	
	public void setEnvironmentManager(EnvironmentManager em) {
		
		this.em = em;
		
		for ( String fqan : em.getAvailableFqans() ) {
			fqanModel.addElement(fqan);
		}
		
	}
	
	public void setStatusListener(StatusListener l) {
		this.listener = l;
	}
	
	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getList());
		}
		return scrollPane;
	}

	public String getSelectedFqan() {
		return (String)getList().getSelectedValue();
	}
	
	/**
	 * @return
	 */
	protected JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					FQANSelectorPanel.this.listener.setNewStatus(new StatusEvent(FQANSelectorPanel.this, OK_ACTION));
				}
			});
			okButton.setText(OK_ACTION);
		}
		return okButton;
	}
	/**
	 * @return
	 */
	protected JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					FQANSelectorPanel.this.listener.setNewStatus(new StatusEvent(FQANSelectorPanel.this, CANCEL_ACTION));
				}
			});
			cancelButton.setText(CANCEL_ACTION);
		}
		return cancelButton;
	}
	/**
	 * @return
	 */
	protected JList getList() {
		if (list == null) {
			list = new JList(fqanModel);
			list.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					
					// double click
					if (e.getClickCount() == 2 ) {
						FQANSelectorPanel.this.listener.setNewStatus(new StatusEvent(FQANSelectorPanel.this, OK_ACTION));
					}
					
				}
			});
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return list;
	}

}
