package org.vpac.grisu.clients.blender.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.vpac.grisu.clients.blender.BlendFile;
import org.vpac.grisu.clients.blender.GrisuBlenderJob;
import org.vpac.grisu.settings.ClientPropertiesManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import com.jidesoft.swing.FolderChooser;
import com.jidesoft.swing.RangeSlider;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BlenderBasicJobPropertiesPanel extends JPanel {

	public static final String LAST_BLENDER_FILE_DIR = "lastBlenderFileDir";

	public static final FileFilter BLEND_FILE_FILTER = new FileFilter() {

		@Override
		public String getDescription() {
			return "*.blend";
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory() || f.getName().endsWith(".blend")) {
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
	private JLabel endLabel;
	private JLabel firstLabel;
	private JTextField textField;
	private JLabel lastLabel;
	private JTextField textField_1;
	private JButton button;
	private JButton infoButton;
	private JCheckBox chckbxSpecifyFrameRange;
	private JLabel lblFormat;
	private JComboBox comboBox;
	private JLabel lblWalltimePerFrame;
	private JComboBox hoursCombobox;
	private JLabel lblHours;
	private JComboBox minutesCombobox;
	private JLabel lblMinutes;
	private JLabel lblJobname;
	private JTextField textField_2;
	private JLabel lblGroup;
	private JComboBox comboBox_3;
//	private final ServiceInterface si;
//	private final UserEnvironmentManager em;

	private GrisuBlenderJob job = null;
	private final BlenderJobCreationPanel parent;

	public BlenderBasicJobPropertiesPanel(BlenderJobCreationPanel parent) {
		
		this.parent = parent;

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("50dlu", true), Sizes.constant("50dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(15dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default)"),
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
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getLblBlendFile(), "2, 2, 13, 1");
		add(getBlendFileTextField(), "2, 4, 11, 1, fill, default");
		add(getInfoButton(), "14, 4");
		add(getBlendFileBrowseButton(), "16, 4");
		add(getLblFluidsFolder(), "2, 6, 13, 1");
		add(getFluidsfolderTextField(), "2, 8, 11, 1, fill, default");
		add(getUnsetFluidFolderButton(), "14, 8");
		add(getBtnBrowse(), "16, 8");
		add(getChckbxSpecifyFrameRange(), "2, 10, 15, 1");
		add(getSlider(), "2, 12, 15, 1");
		add(getStartLabel(), "2, 14, left, default");
		add(getEndLabel(), "16, 14, right, default");
		add(getFirstLabel(), "2, 16, right, default");
		add(getFirstField(), "4, 16, fill, default");
		add(getLastField(), "12, 16, 3, 1, fill, default");
		add(getLastLabel(), "16, 16, left, default");
		add(getLblFormat(), "2, 18, left, default");
		add(getFormatCombobox(), "4, 18, 3, 1");
		add(getLblHours(), "12, 18, 3, 1, right, bottom");
		add(getLblMinutes(), "16, 18, right, bottom");
		add(getLblWalltimePerFrame(), "2, 20, 7, 1, left, default");
		add(getHoursCombobox(), "12, 20, 3, 1, fill, default");
		add(getMinutesCombobox(), "16, 20, fill, default");
		add(getLblJobname(), "2, 22, right, default");
		add(getJobNameTextField(), "4, 22, 3, 1, fill, default");
		add(getLblGroup(), "8, 22, 3, 1, right, default");
		add(getVOComboBox(), "12, 22, 5, 1, fill, default");
//		add(getStatusTextArea(), "2, 26, 15, 1, fill, fill");
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

		if (dotBlendFile == null) {
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
		getStartLabel().setText("Min: " + blendFileObject.getStartFrame());
		getEndLabel().setText("Max: " + blendFileObject.getEndFrame());
		getInfoButton().setEnabled(true);

		getJobNameTextField().setText(parent.setBlendFile(blendFileObject));
	}

	private void setDotBlendFile(File blendFile) {
		this.dotBlendFile = blendFile;
		createBlendFile();

	}

	private void setFluidsFolder(File folder) {
		this.fluidsFolder = folder;
		if (dotBlendFile != null && dotBlendFile.exists()) {
			createBlendFile();
		}
	}
	

	public void lockUI(final boolean lock) {
		
		SwingUtilities.invokeLater(new Thread() {
			
			public void run() {
				
				getBlendFileBrowseButton().setEnabled(!lock);
				getBtnBrowse().setEnabled(!lock);
				getChckbxSpecifyFrameRange().setEnabled(!lock);
				getHoursCombobox().setEnabled(!lock);
				getMinutesCombobox().setEnabled(!lock);
				getBlendFileTextField().setEnabled(!lock);
				getFluidsfolderTextField().setEnabled(!lock);
				getFormatCombobox().setEnabled(!lock);
				getJobNameTextField().setEnabled(!lock);
				getInfoButton().setEnabled(!lock);
				getUnsetFluidFolderButton().setEnabled(!lock);
				getVOComboBox().setEnabled(!lock);
				if ( lock ) {
					getSlider().setEnabled(false);
				} else {
					if ( getChckbxSpecifyFrameRange().isSelected() ) {
						getSlider().setEnabled(true);
					} else {
						getSlider().setEnabled(false);
					}
				}

			}
			
		});
	}
	
	private JButton getBlendFileBrowseButton() {
		if (blendFileBrowseButton == null) {
			blendFileBrowseButton = new JButton("Browse");
			blendFileBrowseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					String lastDir = ClientPropertiesManager
							.getProperty(LAST_BLENDER_FILE_DIR);
					if (StringUtils.isBlank(lastDir)
							|| !new File(lastDir).exists()) {
						lastDir = System.getProperty("user.home");
					}

					final JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Open .blend file");

					// Choose only files, not directories
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setCurrentDirectory(new File(lastDir));
					// Set filter for Java source files.
					fc.setFileFilter(BLEND_FILE_FILTER);

					int result = fc
							.showOpenDialog(BlenderBasicJobPropertiesPanel.this);

					if (result == JFileChooser.APPROVE_OPTION) {
						new Thread() {
							public void run() {
								parent.lockUI(true);
								ClientPropertiesManager.setProperty(
										LAST_BLENDER_FILE_DIR, fc.getCurrentDirectory()
												.toString());
								parent.addMessage("Parsing blend file: "+fc.getSelectedFile().toString()+"\n");
								
								setDotBlendFile(fc.getSelectedFile());
								parent.addMessage(blendFileObject.getParseMessage());
								getBlendFileTextField().setText(
										blendFileObject.getFile().toString());
								getChckbxSpecifyFrameRange().setEnabled(true);
								parent.lockUI(false);
							}
						}.start();
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
					if (StringUtils.isBlank(lastDir)
							|| !new File(lastDir).exists()) {
						lastDir = System.getProperty("user.home");
					}
					fc.setCurrentDirectory(new File(lastDir));

					int result = fc.showOpenDialog(BlenderBasicJobPropertiesPanel.this
							.getTopLevelAncestor());
					if (result == FolderChooser.APPROVE_OPTION) {
						
						File temp = fc.getSelectedFolder();
						if (temp != null && temp.exists()) {
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
			slider.setPaintTicks(false);
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					getFirstField().setText("" + slider.getLowValue());
					getLastField().setText("" + slider.getHighValue());
				}
			});
		}
		return slider;
	}

	private JLabel getStartLabel() {
		if (startLabel == null) {
			startLabel = new JLabel("Min: n/a");
			startLabel.setEnabled(false);
		}
		return startLabel;
	}

	private JLabel getEndLabel() {
		if (endLabel == null) {
			endLabel = new JLabel("Max: n/a");
			endLabel.setEnabled(false);
		}
		return endLabel;
	}

	private JLabel getFirstLabel() {
		if (firstLabel == null) {
			firstLabel = new JLabel("First frame");
			firstLabel.setEnabled(false);
		}
		return firstLabel;
	}

	private JTextField getFirstField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setEnabled(false);
			textField.setEditable(false);
			textField.setColumns(10);
		}
		return textField;
	}

	private JLabel getLastLabel() {
		if (lastLabel == null) {
			lastLabel = new JLabel("Last frame");
			lastLabel.setEnabled(false);
		}
		return lastLabel;
	}

	private JTextField getLastField() {
		if (textField_1 == null) {
			textField_1 = new JTextField();
			textField_1.setEnabled(false);
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

	private JCheckBox getChckbxSpecifyFrameRange() {
		if (chckbxSpecifyFrameRange == null) {
			chckbxSpecifyFrameRange = new JCheckBox(
					"Specify frame range to render");
			chckbxSpecifyFrameRange.setEnabled(false);
			chckbxSpecifyFrameRange.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					boolean checked = chckbxSpecifyFrameRange.isSelected();
					getStartLabel().setEnabled(checked);
					getEndLabel().setEnabled(checked);
					getFirstField().setEnabled(checked);
					getLastLabel().setEnabled(checked);
					getSlider().setEnabled(checked);
					getFirstField().setEnabled(checked);
					getLastField().setEnabled(checked);

				}
			});
		}
		return chckbxSpecifyFrameRange;
	}

	private JLabel getLblFormat() {
		if (lblFormat == null) {
			lblFormat = new JLabel("Format");
		}
		return lblFormat;
	}

	private JComboBox getFormatCombobox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.setModel(new DefaultComboBoxModel(new String[] { "TGA",
					"IRIS", "HAMX", "JPEG", "MOVIE", "IRIZ", "RAWTGA",
					"AVIRAW", "AVIJPEG", "PNG", "BMP", "FRAMESERVER" }));
			comboBox.setSelectedItem("PNG");
		}
		return comboBox;
	}

	private JLabel getLblWalltimePerFrame() {
		if (lblWalltimePerFrame == null) {
			lblWalltimePerFrame = new JLabel(
					"Walltime per frame (for most complex frame)");
		}
		return lblWalltimePerFrame;
	}

	private JComboBox getHoursCombobox() {
		if (hoursCombobox == null) {
			hoursCombobox = new JComboBox();
			hoursCombobox.setModel(new DefaultComboBoxModel(new String[] { "0",
					"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
					"12", "24", "36" }));
		}
		return hoursCombobox;
	}

	private JLabel getLblHours() {
		if (lblHours == null) {
			lblHours = new JLabel("Hours");
		}
		return lblHours;
	}

	private JComboBox getMinutesCombobox() {
		if (minutesCombobox == null) {
			minutesCombobox = new JComboBox();
			minutesCombobox.setModel(new DefaultComboBoxModel(new String[] {
					"10", "20", "30", "40", "50" }));
		}
		return minutesCombobox;
	}

	private JLabel getLblMinutes() {
		if (lblMinutes == null) {
			lblMinutes = new JLabel("Minutes");
		}
		return lblMinutes;
	}

	private JLabel getLblJobname() {
		if (lblJobname == null) {
			lblJobname = new JLabel("Jobname");
		}
		return lblJobname;
	}

	private JTextField getJobNameTextField() {
		if (textField_2 == null) {
			textField_2 = new JTextField();
			textField_2.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					
					parent.setJobname(textField_2.getText());
					
				}
			});
			textField_2.setColumns(10);
		}
		return textField_2;
	}

	private JLabel getLblGroup() {
		if (lblGroup == null) {
			lblGroup = new JLabel("Group");
		}
		return lblGroup;
	}

	private JComboBox getVOComboBox() {
		if (comboBox_3 == null) {
			comboBox_3 = new JComboBox(parent.getAllFqans());
		}
		return comboBox_3;
	}

	public int getCurrentWalltimeInSeconds() {

		int hours = Integer.parseInt((String) getHoursCombobox()
				.getSelectedItem());
		int minutes = Integer.parseInt((String) getMinutesCombobox()
				.getSelectedItem());

		return hours * 3600 + minutes * 60;
	}

	public String getSelectedFqan() {

		return (String)getVOComboBox().getSelectedItem();
	}
	
	public int getFirstFrame() {
		if (getChckbxSpecifyFrameRange().isSelected()) {
			return getSlider().getLowValue();
		} else {
			return blendFileObject.getStartFrame();
		}	
	}
	
	public int getLastFrame() {
		if (getChckbxSpecifyFrameRange().isSelected()) {
			return getSlider().getHighValue();
		} else {
			return blendFileObject.getEndFrame();
		}
	}

}
