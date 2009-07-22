

package org.vpac.grisu.client.view.swing.preview;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;

import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.view.swing.utils.Messages;
import org.vpac.grisu.settings.ClientPropertiesManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class DefaultViewerPanel extends  PreviewPanelInsert {

	private JLabel viewAsTextLabel;
	private JButton viewButton;
	private JSeparator separator_1;
	private JSeparator separator;
	private JScrollPane scrollPane;
	private JEditorPane editorPane;
	private JTextField applicationPathTextField;
	private JLabel label;
	private JButton openButton;
	private JButton browseButton;
	
	GrisuFileObject file = null;
	
	/**
	 * Create the panel
	 */
	public DefaultViewerPanel() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("50dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("68px:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getBrowseButton(), new CellConstraints(4, 10, CellConstraints.LEFT, CellConstraints.TOP));
		add(getOpenButton(), new CellConstraints(6, 10));
		add(getLabel(), new CellConstraints(2, 11, 3, 1, CellConstraints.LEFT, CellConstraints.TOP));
		add(getApplicationPathTextField(), new CellConstraints(2, 10));
		add(getScrollPane(), new CellConstraints("2, 2, 5, 1, fill, fill"));
		add(getSeparator(), new CellConstraints(2, 4, 5, 1));
		add(getSeparator_1(), new CellConstraints(2, 8, 5, 1));
		add(getViewButton(), new CellConstraints(6, 6));
		add(getViewAsTextLabel(), new CellConstraints(2, 6, 3, 1));
		//
	}

	@Override
	public String[] getHandledExtensions() {
		return new String[]{};
	}

	@Override
	public String[] getHandledMimeTypes() {
		return new String[]{};
	}

	@Override
	public void refresh(GrisuFileObject file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataToPreview(byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFileToPreview(GrisuFileObject file) {
		this.file = file;
		StringBuffer text = new StringBuffer();
		text.append("<html><body>");
		text.append(Messages.getMessage("loadWithExternalApp"));
		text.append("<ul>");
		text.append("<li><b>Name</b>: "+file.getName()+"</li>");
		text.append("<li><b>URL</b>:"+file.getURI().toString());
		
		text.append("</ul>");
		text.append("</body>></html>");
		
		getEditorPane().setText(text.toString());
		
		int lastIndex = file.getName().lastIndexOf(".");
		if ( lastIndex != -1 ) {
			String extension = file.getName().substring(lastIndex);
			String defaultApplicationPath = ClientPropertiesManager.getDefaultExternalApplication(extension);
			getApplicationPathTextField().setText(defaultApplicationPath);
		}
		
	}
	protected JButton getBrowseButton() {
		if (browseButton == null) {
			browseButton = new JButton();
			browseButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("Please choose an application to open the file");
					jfc.setDialogType(JFileChooser.OPEN_DIALOG);
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					jfc.setMultiSelectionEnabled(false);
					int option = jfc.showOpenDialog(DefaultViewerPanel.this);
					
					if ( option != JFileChooser.CANCEL_OPTION ) {
						File selected = jfc.getSelectedFile();
						getApplicationPathTextField().setText(selected.getPath());
					}
				}
			});
			browseButton.setText("Browse");
		}
		return browseButton;
	}
	protected JButton getOpenButton() {
		if (openButton == null) {
			openButton = new JButton();
			openButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					String path = file.getLocalRepresentation(true).getPath();
					if ( getApplicationPathTextField().getText().length() > 0 ) {
					try {
						Runtime.getRuntime().exec( new String[] {getApplicationPathTextField().getText(), path } );
						int lastIndex = path.lastIndexOf(".");
						if ( lastIndex != -1 ) {
							String extension = path.substring(lastIndex);
							ClientPropertiesManager.setDefaultExternalApplication(extension, getApplicationPathTextField().getText());
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					}
					else {
//						try {
//							Desktop.open(file.getLocalRepresentation(true));
//						} catch (DesktopException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
					}
					
				}
			});
			openButton.setText("Open");
		}
		return openButton;
	}
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("Please specify the path to the executable");
		}
		return label;
	}
	protected JTextField getApplicationPathTextField() {
		if (applicationPathTextField == null) {
			applicationPathTextField = new JTextField();
		}
		return applicationPathTextField;
	}
	protected JEditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			editorPane.setEditable(false);
			editorPane.setContentType("text/html");
			editorPane.setEditorKit(new HTMLEditorKit());
			editorPane.setBackground(Color.WHITE);
		}
		return editorPane;
	}
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getEditorPane());
		}
		return scrollPane;
	}
	/**
	 * @return
	 */
	protected JSeparator getSeparator() {
		if (separator == null) {
			separator = new JSeparator();
		}
		return separator;
	}
	/**
	 * @return
	 */
	protected JSeparator getSeparator_1() {
		if (separator_1 == null) {
			separator_1 = new JSeparator();
		}
		return separator_1;
	}
	
	protected void setBusy(boolean busy) {
		
		if ( busy ) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			getViewButton().setEnabled(false);
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			getViewButton().setEnabled(true);
		}
		
		
		
	}
	
	/**
	 * @return
	 */
	protected JButton getViewButton() {
		if (viewButton == null) {
			viewButton = new JButton();
			viewButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					new Thread() {
						public void run() {
					setBusy(true);
					try {
					TextPreviewDialog pd = new TextPreviewDialog();
					pd.setFile(file);
					setBusy(false);
					pd.setVisible(true);
					} finally {
						setBusy(false);
					}
						}
					}.start();
					
				}
			});
			viewButton.setText("View");
		}
		return viewButton;
	}
	/**
	 * @return
	 */
	protected JLabel getViewAsTextLabel() {
		if (viewAsTextLabel == null) {
			viewAsTextLabel = new JLabel();
			viewAsTextLabel.setText("View as text file");
		}
		return viewAsTextLabel;
	}

}
