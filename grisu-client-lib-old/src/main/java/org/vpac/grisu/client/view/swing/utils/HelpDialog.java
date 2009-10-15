package org.vpac.grisu.client.view.swing.utils;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class HelpDialog extends JDialog {

	private JScrollPane scrollPane;
	private JTextArea textArea;
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HelpDialog dialog = new HelpDialog();
					dialog.setVisible(true);
					dialog.setText("Usage: cat [OPTION] [FILE]...\n"+
"Concatenate FILE(s), or standard input, to standard output."+

  "-A, --show-all           equivalent to -vET\n"+
  "-b, --number-nonblank    number nonempty output lines\n"+
  "-e                       equivalent to -vE\n"+
  "-E, --show-ends          display $ at end of each line\n"+
  "-n, --number             number all output lines\n"+
  "-s, --squeeze-blank      suppress repeated empty output lines\n"+
  "-t equivalent to -vT\n"+
  "-T, --show-tabs display TAB characters as ^I\n"+
  "-u (ignored)\n"+
  "-v, --show-nonprinting use ^ and M- notation, except for LFD and TAB\n"+
      "--help display this help and exit\n"+
     " --version output version information and exit\n"+

"With no FILE, or when FILE is -, read standard input.\n"+

"Examples:\n"+
  "cat f - g Output f's contents, then standard input, then g's contents.\n"+
  "cat Copy standard input to standard output.\n"+

"Report bugs to <bug-coreutils@gnu.org>.\n"
);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog
	 */
	public HelpDialog() {
		super();
		setModal(false);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				HelpDialog.this.dispose();
			}
		});
		setBounds(100, 100, 500, 375);
		getContentPane().add(getScrollPane(), BorderLayout.CENTER);
		//
	}
	
	public HelpDialog(String text) {
		this();
		setText(text);
	}
	
	/**
	 * @return
	 */
	protected JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setLineWrap(true);
		}
		return textArea;
	}
	
	public void setText(String text) {
		
		getTextArea().setText(text);
		
	}
	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}

}
