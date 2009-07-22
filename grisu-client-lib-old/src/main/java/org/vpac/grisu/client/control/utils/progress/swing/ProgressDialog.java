

package org.vpac.grisu.client.control.utils.progress.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This displays a swing dialog with a progress bar and a description of the action that is currently done.
 * 
 * @author Markus Binsteiner
 *
 */
public class ProgressDialog extends JDialog implements ChangeListener{

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jPanel = null;

	private JProgressBar jProgressBar = null;

	private JLabel statusLabel = null;
	
	private ProgressMonitor monitor; 

	/**
	 * @param owner
	 */
	public ProgressDialog(Frame owner, ProgressMonitor monitor) {
		super(owner, "Progress", true);
		this.monitor = monitor;
		initialize();
	}
	
	public ProgressDialog(Dialog owner, ProgressMonitor monitor) {
		super(owner);
		this.monitor = monitor;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(455, 144);
		this.setContentPane(getJContentPane());
	       if(monitor.isIndeterminate()) 
	           getJProgressBar().setIndeterminate(true); 
	       else 
	           getJProgressBar().setValue(monitor.getCurrent()); 
	       statusLabel.setText(monitor.getStatus()); 
	       
	       setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
	       monitor.addChangeListener(this); 
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(0, 15, 15, 0);
			gridBagConstraints1.gridy = 1;
			statusLabel = new JLabel();
			statusLabel.setText("n/a");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(15, 15, 15, 15);
			gridBagConstraints.gridy = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJProgressBar(), gridBagConstraints);
			jPanel.add(statusLabel, gridBagConstraints1);
		}
		return jPanel;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar(0, monitor.getTotal());
		}
		return jProgressBar;
	}
	
	   public void stateChanged(final ChangeEvent ce){ 
	       // to ensure EDT thread 
	       if(!SwingUtilities.isEventDispatchThread()){ 
	           SwingUtilities.invokeLater(new Runnable(){ 
	               public void run(){ 
	                   stateChanged(ce); 
	               } 
	           }); 
	           return; 
	       } 

	       if(monitor.getCurrent()!=monitor.getTotal()){ 
	           statusLabel.setText(monitor.getStatus()); 
	           if(!monitor.isIndeterminate()) 
	               getJProgressBar().setValue(monitor.getCurrent()); 
	       }else 
	           dispose(); 
	   } 

}  //  @jve:decl-index=0:visual-constraint="10,10"
