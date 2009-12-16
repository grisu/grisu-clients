package org.vpac.grisu.client.view.swing.template.panels;

import java.io.File;
import java.net.URI;
import java.util.Date;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.view.swing.files.FileChooserEvent;
import org.vpac.grisu.client.view.swing.files.FileChooserParent;
import org.vpac.grisu.client.view.swing.files.SiteFileChooserDialog;

public class InputFile extends AbstractInputPanel implements FileChooserParent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3419432754281441114L;
	public static final String DEFAULT_LAST_DIRECTORY_VALUE = "globalLastDirectory";
	public static final String LAST_DIRECTORY_KEY = null;
	private static final String GLOBAL_TEMP_DIRECTORY_KEY = "globalTempLastDirectory";

	private static SiteFileChooserDialog getSiteFileChooserDialog() {
		if (sfcd == null) {
			if (em == null) {
				return null;
			}
			sfcd = new SiteFileChooserDialog(em);
			sfcd.setSite(FileConstants.LOCAL_NAME);
		}
		return sfcd;
	}

	private String renderMode = null;
	private static SiteFileChooserDialog sfcd = null;

	// private BackendFileObject currentDirectory = null;

	private static EnvironmentManager em = null;

	private String lastDirectoryKey = DEFAULT_LAST_DIRECTORY_VALUE;

	/**
	 * Create the panel
	 */
	public InputFile() {
		super();
		//
	}

	protected void buttonPressed() {
		try {
			String changeToDirectory = null;

			// change to appropriate directory
			try {

				URI uri = null;
				if (lastDirectoryKey == null) {
					uri = new File(System.getProperty("user.home")).toURI();
				} else {

					changeToDirectory = historyManager.getEntries(
							lastDirectoryKey).get(0);

					try {
						uri = new URI(changeToDirectory);

						if (!new File(uri).exists() || !new File(uri).canRead()) {
							uri = new File(System.getProperty("user.home"))
									.toURI();
						}

					} catch (Exception uriE) {
						uri = new File(System.getProperty("user.home")).toURI();
					}
				}

				GrisuFileObject dir = templateNode.getTemplate()
						.getEnvironmentManager().getFileManager()
						.getFileObject(uri);
				getSiteFileChooserDialog().setCurrentDirectory(dir);
			} catch (Exception e) {
				// e.printStackTrace();
				myLogger.error("Could not change to directory: "
						+ e.getLocalizedMessage());
			}

			getSiteFileChooserDialog().addUserInputListener(InputFile.this);
			getSiteFileChooserDialog().setVisible(true);

			// save last directory
			try {
				GrisuFileObject dir = getSiteFileChooserDialog()
						.getCurrentDirectory();
				if (lastDirectoryKey == null) {
					// to store until button gets pressed again
					lastDirectoryKey = GLOBAL_TEMP_DIRECTORY_KEY;
				}
				historyManager.addHistoryEntry(lastDirectoryKey, dir.getURI()
						.toString(), new Date(), 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} finally {
			getSiteFileChooserDialog().removeUserInputListener(InputFile.this);

		}
	}

	public String genericButtonText() {
		return "Browse";
	}

	protected ComponentHolder getComponentHolder() {

		if (TEXTFIELD_PANEL.equals(renderMode)) {
			return new TextFieldHolder(this);
		} else {
			return new ComboBoxHolder(this);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.vpac.grisu.client.view.swing.template.panels.AbstractInputPanel#
	 * preparePanel() this method gets called right after the call of
	 * setTemplateNode() of the parent class.
	 */
	protected void preparePanel() {

		em = this.templateNode.getTemplate().getEnvironmentManager();

		if (this.templateNode.hasProperty(LAST_DIRECTORY_KEY)) {
			if (TemplateNode.NON_MAP_PARAMETER.equals(this.templateNode
					.getOtherProperty(LAST_DIRECTORY_KEY))) {
				lastDirectoryKey = DEFAULT_LAST_DIRECTORY_VALUE;
			} else {
				lastDirectoryKey = this.templateNode
						.getOtherProperty(LAST_DIRECTORY_KEY)
						+ "_dirKey";
			}
		} else {
			lastDirectoryKey = null;
		}

		try {
			renderMode = this.templateNode.getOtherProperties().get("render");
		} catch (RuntimeException e1) {
			// fallback
			renderMode = COMBOBOX_PANEL;
		}
		if (renderMode == null)
			renderMode = COMBOBOX_PANEL;

	}

	public void reset() {

		String value = getExternalSetValue();

		if (useHistory) {
			if (value != null && !"".equals(value.trim())) {
				historyManager.addHistoryEntry(this.templateNode.getName(),
						value, new Date());
			}
		}
		if (COMBOBOX_PANEL.equals(renderMode)) {
			fillComboBox();
		}

		setDefaultValue();
	}

	@Override
	protected void setupComponent() {
		// nothing to do here
	}

	public void userInput(FileChooserEvent event) {

		if (FileChooserEvent.SELECTED_FILE == event.getType()) {
			String value = null;
			if (event.getSelectedFile() != null) {
				URI uri = event.getSelectedFile().getURI();
				value = uri.toString();
				// if (uri.getScheme().startsWith("file")) {
				// value = "local://" + uri.getPath();
				// } else {
				// value = uri.toString();
				// }
			}
			holder.setComponentField(value);
		}
		if (event.getType() != FileChooserEvent.CHANGED_FOLDER) {
			sfcd.setVisible(false);
		}

	}

}
