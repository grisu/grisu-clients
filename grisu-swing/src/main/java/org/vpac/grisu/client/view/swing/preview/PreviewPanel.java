package org.vpac.grisu.client.view.swing.preview;

import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.view.swing.utils.Utils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class PreviewPanel extends JPanel {
	
	private JLabel label;
	private final static Class[] previewPanelClasses = new Class[] {
		TextViewerPanel.class, ImageViewerPanel.class
	};
	
	private GrisuFileObject file = null;
	private PreviewPanelInsert insert = null;

	/**
	 * Create the panel
	 */
	public PreviewPanel() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("163px:grow(1.0)")},
			new RowSpec[] {
				RowSpec.decode("165px:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC}));
		//
		add(getLabel(), new CellConstraints(1, 3, CellConstraints.FILL, CellConstraints.CENTER));

	}
	
	public void previewFile(GrisuFileObject file) {
		
		this.file = file;
		
		if ( this.insert != null ) {
			remove(this.insert);
		}
		
		if ( file == null) {
			this.insert = null;
			setFileInfo();
			return;
		}
		

		this.insert = findHandlerPanel(file);
		if ( this.insert == null ) {
			this.insert = new DefaultViewerPanel();
		}
		try {
			this.insert.setFileToPreview(file);
		} catch (Exception e) {
			Utils.showErrorMessage(file.getFileSystemBackend().getEnvironmentManager(), this, "couldNotGetOrCacheFile", e);
		}
		add(insert, new CellConstraints("1, 1, 1, 1, fill, fill"));
		setFileInfo();
	}
	
	public void refresh() {
		if ( insert != null ) {
			try {
				insert.refresh(file);
				setFileInfo();
			} catch (Exception e) {
				Utils.showErrorMessage(file.getFileSystemBackend().getEnvironmentManager(), this, "couldNotGetOrCacheFile", e);
			}
		}
	}
	
	/**
	 * Finds the appropriate viewer panel for this file
	 * @param file the file
	 * @return the viewer panel
	 */
	private PreviewPanelInsert findHandlerPanel(GrisuFileObject file) {
		
		String extension = file.getName().substring(file.getName().lastIndexOf(".")+1);

		for ( Class panelClass : previewPanelClasses ) {
			PreviewPanelInsert panel;
			try {
				panel = (PreviewPanelInsert)panelClass.newInstance();
			} catch (Exception e) {
				// this means the above specified class is not available or does not implement the right interface
				e.printStackTrace();
				continue;
			}
			if ( panel.handlesExtension(extension) )
				return panel;
		}
		
		return null;
	}
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
		}
		return label;
	}
	
	private void setFileInfo() {
		if ( file != null ) {
			long size = file.getSize(false);
			String sizeString = size+" B";
			if ( size > 1024*1024 ) 
				sizeString = size/(1024*1024) + "MB";
			else if ( size > 1024 )
				sizeString = size/1024 + " KB";
			
			long lastModifiedDate = file.getLastModified(false);
			String lastModified = new Date(file.getLastModified(false)).toString();
			getLabel().setText("<html><body><b>Size:</b> " + sizeString +", <b>Last modified:</b> " + lastModified + "</body></html>");
		} else {
			getLabel().setText("No file selected.");
		}
	}

}
