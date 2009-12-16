package org.vpac.grisu.client.view.swing.preview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.utils.FileHelpers;

public class TextViewerPanel extends PreviewPanelInsert {

	private JScrollPane scrollPane;
	private JEditorPane textContentPane;
	static final Logger myLogger = Logger.getLogger(TextViewerPanel.class
			.getName());

	private final String[] extensions = new String[] { "txt", "dat", "log",
			"xml", "o\\d+", "e\\d+" };
	private final String[] mimeTypes = new String[] {};

	private File file = null;
	private String text = null;

	/**
	 * Create the panel
	 */
	public TextViewerPanel() {
		super();
		setLayout(new BorderLayout());
		add(getScrollPane(), BorderLayout.CENTER);
		//
	}

	public String[] getHandledExtensions() {
		return extensions;
	}

	public String[] getHandledMimeTypes() {
		return mimeTypes;
	}

	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextContentPane());
		}
		return scrollPane;
	}

	protected JEditorPane getTextContentPane() {
		if (textContentPane == null) {
			textContentPane = new JEditorPane();
			textContentPane.setBackground(Color.WHITE);
			textContentPane.setOpaque(false);
			textContentPane.setEditable(false);
		}
		return textContentPane;
	}

	public void refresh(GrisuFileObject file) {

		setFileToPreview(file);
	}

	public void setDataToPreview(byte[] data) {
		// TODO Auto-generated method stub
		myLogger.error("This is not supported yet.");
	}

	public void setFileToPreview(GrisuFileObject file) {

		this.file = file.getLocalRepresentation(true);
		if (file != null) {
			this.text = FileHelpers.readFromFile(this.file);
			getTextContentPane().setText(this.text);
		} else {
			getTextContentPane().setText(
					"Can't display content/No content to display.");
		}
	}

}
