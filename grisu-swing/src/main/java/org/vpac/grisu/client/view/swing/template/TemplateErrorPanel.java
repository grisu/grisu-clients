package org.vpac.grisu.client.view.swing.template;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vpac.helpDesk.control.Utils;

public class TemplateErrorPanel extends JPanel {

	private JScrollPane scrollPane;
	private JTextArea textArea;
	/**
	 * Create the panel
	 */
	public TemplateErrorPanel() {
		super();
		setLayout(new BorderLayout());
		add(getScrollPane(), BorderLayout.CENTER);
		//
	}
	/**
	 * @return
	 */
	protected JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setMargin(new Insets(10, 10, 10, 10));
			textArea.setEditable(false);
			textArea.setBackground(Color.ORANGE);
			textArea.setLineWrap(true);
		}
		return textArea;
	}

	
	public void setErrorMessage(String errorMessage, Exception e) {
		
		StringBuffer temp = new StringBuffer(errorMessage);
		temp.append("\n\n");
		for ( String line : Utils.fromException(e) ) {
			temp.append(line+"\n");
		}
		getTextArea().setText(temp.toString());
		
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

}
