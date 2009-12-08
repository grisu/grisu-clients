package org.vpac.grisu.clients.blender.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.clients.blender.BlendFile;
import org.vpac.grisu.settings.ClientPropertiesManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import com.jidesoft.swing.FolderChooser;
import com.jidesoft.swing.RangeSlider;

public class BlenderJobCreationPanel extends JPanel {

	public static final String LAST_BLENDER_FILE_DIR = "lastBlenderFileDir";
	
	public static final FileFilter BLEND_FILE_FILTER = new FileFilter() {
		
		@Override
		public String getDescription() {
			return "*.blend";
		}
		
		@Override
		public boolean accept(File f) {
			if ( f.isDirectory() || f.getName().endsWith(".blend") ) {
				return true;
			} else {
				return false;
			}
		}
	};

	private JTextField blendFileTextField;
	private JButton blendFileBrowseButton;
	private JLabel lblBlendFile;
	
	private BlendFile blendFileObject;
	private File dotBlendFile;
	private File fluidsFolder;
	
	private JLabel lblFluidsFolder;
	private JTextField fluidsfolderTextField;
	private JButton btnBrowse;
	private RangeSlider slider;
	private JLabel startLabel;
	private JLabel lblMax;
	private JLabel firstLabel;
	private JTextField textField;
	private JLabel lastLabel;
	private JTextField textField_1;
	private JButton button;
	private JButton infoButton;
	

	public BlenderJobCreationPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("50dlu", true), Sizes.constant("50dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("23dlu", true), Sizes.constant("50dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("23dlu", true), Sizes.constant("50dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		add(getLblBlendFile(), "2, 2, 11, 1");
		add(getBlendFileTextField(), "2, 4, 9, 1, fill, default");
		add(getInfoButton(), "12, 4");
		add(getBlendFileBrowseButton(), "14, 4");
		add(getLblFluidsFolder(), "2, 6, 11, 1");
		add(getFluidsfolderTextField(), "2, 8, 9, 1, fill, default");
		add(getUnsetFluidFolderButton(), "12, 8");
		add(getBtnBrowse(), "14, 8");
		add(getSlider(), "2, 10, 13, 1");
		add(getStartLabel(), "2, 12, left, default");
		add(getLblMax(), "14, 12, right, default");
		add(getFirstLabel(), "2, 14, right, default");
		add(getFirstField(), "4, 14, fill, default");
		add(getLastField(), "10, 14, 3, 1, fill, default");
		add(getLastLabel(), "14, 14, left, default");
	}

	private JTextField getBlendFileTextField() {
		if (blendFileTextField == null) {
			blendFileTextField = new JTextField();
			blendFileTextField.setEditable(false);
			blendFileTextField.setColumns(10);
		}
		return blendFileTextField;
	}
	
	public void createBlendFile() {
		
		if ( dotBlendFile == null ) {
			return;
		}
		try {
			blendFileObject = new BlendFile(dotBlendFile, fluidsFolder);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		getSlider().setEnabled(true);
		getSlider().setMinimum(blendFileObject.getStartFrame());
		getSlider().setMaximum(blendFileObject.getEndFrame());
		getSlider().setLowValue(blendFileObject.getStartFrame());
		getSlider().setHighValue(blendFileObject.getEndFrame());
		getStartLabel().setText("Min: "+blendFileObject.getStartFrame());
		getLblMax().setText("Max: "+blendFileObject.getEndFrame());
		getInfoButton().setEnabled(true);
		
	}
	
	private void setDotBlendFile(File blendFile) {
		this.dotBlendFile = blendFile;
		createBlendFile();
	}
	
	private void setFluidsFolder(File folder) {
		this.fluidsFolder = folder;
		if ( dotBlendFile != null && dotBlendFile.exists() ) {
			createBlendFile();
		}
	}

	private JButton getBlendFileBrowseButton() {
		if (blendFileBrowseButton == null) {
			blendFileBrowseButton = new JButton("Browse");
			blendFileBrowseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					String lastDir = ClientPropertiesManager
							.getProperty(LAST_BLENDER_FILE_DIR);
					if (StringUtils.isBlank(lastDir) || ! new File(lastDir).exists() ) {
						lastDir = System.getProperty("user.home");
					}

					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Open .blend file");

					// Choose only files, not directories
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setCurrentDirectory(new File(lastDir));
					// Set filter for Java source files.
					fc.setFileFilter(BLEND_FILE_FILTER);

					int result = fc
							.showOpenDialog(BlenderJobCreationPanel.this);
					
					if ( result == JFileChooser.APPROVE_OPTION ) {
						ClientPropertiesManager.setProperty(LAST_BLENDER_FILE_DIR, fc.getCurrentDirectory().toString());
						
						setDotBlendFile(fc.getSelectedFile());
						getBlendFileTextField().setText(blendFileObject.getFile().toString());
						
					}

					
					
				}
			});
		}
		return blendFileBrowseButton;
	}

	private JLabel getLblBlendFile() {
		if (lblBlendFile == null) {
			lblBlendFile = new JLabel("Blend file");
		}
		return lblBlendFile;
	}
	private JLabel getLblFluidsFolder() {
		if (lblFluidsFolder == null) {
			lblFluidsFolder = new JLabel("Fluids folder (optional)");
		}
		return lblFluidsFolder;
	}
	private JTextField getFluidsfolderTextField() {
		if (fluidsfolderTextField == null) {
			fluidsfolderTextField = new JTextField();
			fluidsfolderTextField.setEditable(false);
			fluidsfolderTextField.setColumns(10);
		}
		return fluidsfolderTextField;
	}
	private JButton getBtnBrowse() {
		if (btnBrowse == null) {
			btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					FolderChooser fc = new FolderChooser();
					
					String lastDir = ClientPropertiesManager
					.getProperty(LAST_BLENDER_FILE_DIR);
					if (StringUtils.isBlank(lastDir) || ! new File(lastDir).exists() ) {
						lastDir = System.getProperty("user.home");
					}
					fc.setCurrentDirectory(new File(lastDir));
					
					int result = fc.showOpenDialog(BlenderJobCreationPanel.this.getTopLevelAncestor());
					if (result == FolderChooser.APPROVE_OPTION) {
						File temp = fc.getSelectedFolder();
						if ( temp != null && temp.exists() ) {
							getFluidsfolderTextField().setText(temp.toString());
							setFluidsFolder(temp);
							button.setEnabled(true);
						}
					}

					
				}
			});
		}
		return btnBrowse;
	}


	

	private RangeSlider getSlider() {
		if (slider == null) {
			slider = new RangeSlider(0, 100, 0, 0);
			slider.setEnabled(false);
			slider.setPaintTicks(true);
			slider.setMajorTickSpacing(10);
			slider.addChangeListener(new ChangeListener() {
	            public void stateChanged(ChangeEvent e) {
	                getFirstField().setText(""+slider.getLowValue());
	                getLastField().setText("" + slider.getHighValue());
	            }
	        });
		}
		return slider;
	}
	private JLabel getStartLabel() {
		if (startLabel == null) {
			startLabel = new JLabel("Min: n/a");
		}
		return startLabel;
	}
	private JLabel getLblMax() {
		if (lblMax == null) {
			lblMax = new JLabel("Max: n/a");
		}
		return lblMax;
	}
	private JLabel getFirstLabel() {
		if (firstLabel == null) {
			firstLabel = new JLabel("First frame");
		}
		return firstLabel;
	}
	private JTextField getFirstField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setEditable(false);
			textField.setColumns(10);
		}
		return textField;
	}
	private JLabel getLastLabel() {
		if (lastLabel == null) {
			lastLabel = new JLabel("Last frame");
		}
		return lastLabel;
	}
	private JTextField getLastField() {
		if (textField_1 == null) {
			textField_1 = new JTextField();
			textField_1.setEditable(false);
			textField_1.setColumns(10);
		}
		return textField_1;
	}
	private JButton getUnsetFluidFolderButton() {
		if (button == null) {
			button = new JButton("X");
			button.setEnabled(false);
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					getFluidsfolderTextField().setText(null);
					setFluidsFolder(null);
					button.setEnabled(false);
				}
			});
		}
		return button;
	}
	private JButton getInfoButton() {
		if (infoButton == null) {
			infoButton = new JButton("Info");
			infoButton.setEnabled(false);
		}
		return infoButton;
	}
}
