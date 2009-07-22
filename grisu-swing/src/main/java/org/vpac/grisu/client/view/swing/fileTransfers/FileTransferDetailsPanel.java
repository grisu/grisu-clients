package org.vpac.grisu.client.view.swing.fileTransfers;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vpac.grisu.client.control.files.FileTransfer;
import org.vpac.grisu.client.control.files.FileTransferEvent;
import org.vpac.grisu.client.control.files.FileTransferListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileTransferDetailsPanel extends JPanel implements FileTransferListener {
	
	public static final String DATE_FORMAT_NOW = "HH:mm:ss";

	private JButton cancelButton;
	private JProgressBar progressBar;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	
	private FileTransfer transfer = null;
	private boolean finished = false;
	private FileTransferDetailsPanelHolder parent = null;
	
	/**
	 * Create the panel
	 */
	public FileTransferDetailsPanel(FileTransferDetailsPanelHolder parent) {
		super();
		this.parent = parent;
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("16dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getScrollPane(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		add(getProgressBar(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		add(getCancelButton(), new CellConstraints(2, 6, CellConstraints.CENTER, CellConstraints.DEFAULT));
		//
	}
	
	public void setFileTransfer(FileTransfer transfer) {
		
		this.transfer = transfer;
		
		if ( this.transfer.getStatus() >= FileTransfer.FINISHED_EITHER_WAY_STATUS ) {
			getCancelButton().setText("Ok");
			finished = true;
		}
		
		getTextArea().setText(transfer.getTransferMessages());
		getProgressBar().setValue(transfer.getStatus());
		
		this.transfer.addListener(this);
	}
	
	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}
	/**
	 * @return
	 */
	protected JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
		}
		return textArea;
	}
	/**
	 * @return
	 */
	protected JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
		}
		return progressBar;
	}
	/**
	 * @return
	 */
	/**
	 * @return
	 */

	public void fileTransferEventOccured(FileTransferEvent e) {

//	    Calendar cal = Calendar.getInstance();
//	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
//	    String time = sdf.format(cal.getTime());

		
		getTextArea().setText(transfer.getTransferMessages());
		if ( e.getType() == FileTransferEvent.TRANSFER_PROGRESS_CHANGED ) {
			getProgressBar().setValue(transfer.getStatus());
		} 
		
		if ( e.getType() == FileTransferEvent.TRANSFER_FINISHED ) {
			getProgressBar().setValue(100);
		}
		
		if ( e.getType() >= FileTransferEvent.TRANSFER_FINISHED_EITHER_WAY ) {
			this.finished = true;
			getCancelButton().setText("Ok");
		}
		
	}
	/**
	 * @return
	 */
	protected JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setPreferredSize(new Dimension(100, 24));
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					if ( ! finished ) {
						transfer.killTransfer();
					} else {
						parent.okButtonClicked();
					}
				}
			});
			cancelButton.setText("Cancel");
		}
		return cancelButton;
	}

}
