package org.vpac.grisu.client.view.swing.preview;

import javax.swing.JPanel;

import org.vpac.grisu.client.model.files.GrisuFileObject;

/**
 * This interface specifies the capabilties of a panel that is able to preview
 * certain files.
 * 
 * @author Markus Binsteiner
 * 
 */
abstract class PreviewPanelInsert extends JPanel {

	/**
	 * Returns a list of all handled extensions of this panel.
	 * 
	 * @return all extensions
	 */
	abstract public String[] getHandledExtensions();

	/**
	 * Returns a list of all handled mime-types of this panel.
	 * 
	 * @return all mime types
	 */
	abstract public String[] getHandledMimeTypes();

	/**
	 * Checks whether the provided extension is supported
	 * 
	 * @param extension
	 *            the extension
	 * @return if this panel is able to preview a file with this extension
	 */
	public boolean handlesExtension(String extensionInQuestion) {

		for (String extension : getHandledExtensions()) {
			// if (
			// extensionInQuestion.toLowerCase().equals(extension.toLowerCase())
			// ) {
			// return true;
			// }
			// maybe it's a regular expression?
			if (extensionInQuestion.matches(extension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the provided mimetype is supported
	 * 
	 * @param mimeType
	 *            the mimeType
	 * @return if this panel is able to preview a file with this mime type
	 */
	public boolean handlesMimeType(String mimeTypeInQuestion) {
		for (String mimeType : getHandledExtensions()) {
			if (mimeTypeInQuestion.toLowerCase().equals(mimeType.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	// --------------------------------------------------------------------------
	// these are not supported yet, but are planned to

	/**
	 * Refreshes the view.
	 */
	abstract public void refresh(GrisuFileObject file);

	/**
	 * Sets the date to be displayed
	 * 
	 * @param data
	 *            the data
	 */
	abstract public void setDataToPreview(byte[] data);

	/**
	 * Sets the file that should be previed.
	 * 
	 * @param file
	 *            the file
	 */
	abstract public void setFileToPreview(GrisuFileObject file);

}
