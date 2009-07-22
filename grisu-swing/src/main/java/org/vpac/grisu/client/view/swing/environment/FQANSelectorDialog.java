

package org.vpac.grisu.client.view.swing.environment;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.status.StatusEvent;
import org.vpac.grisu.client.control.status.StatusListener;

/**
 * Pops up a dialog which asks for the new "default" VO. Use it like:
 * <pre>
		FQANSelectorDialog dialog = new FQANSelectorDialog();
  		dialog.setEnvironmentManager(EnvironmentManager.getDefaultManager());
		dialog.setVisible(true);
	</pre>
 * It will automatically change the default VO in the {@link EnvironmentManager} using {@link EnvironmentManager#setDefaultFqan(String)}.
 * @author Markus Binsteiner
 *
 */
public class FQANSelectorDialog extends JDialog implements StatusListener {

	private FQANSelectorPanel selectorPanel;
	private EnvironmentManager em = null;
	
	private String selectedFqan = null;
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			FQANSelectorDialog dialog = new FQANSelectorDialog();
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog
	 */
	public FQANSelectorDialog() {
		super();
		setBounds(100, 100, 500, 375);
		getContentPane().add(getSelectorPanel(), BorderLayout.CENTER);
		//
	}
	/**
	 * @return
	 */
	protected FQANSelectorPanel getSelectorPanel() {
		if (selectorPanel == null) {
			selectorPanel = new FQANSelectorPanel();
			selectorPanel.setStatusListener(this);
		}
		return selectorPanel;
	}

	public void setEnvironmentManager(EnvironmentManager em) {
		this.em = em;
		selectorPanel.setEnvironmentManager(em);
	}
	
	public void setNewStatus(StatusEvent e) {
		
		if ( e.getStatus().equals(FQANSelectorPanel.OK_ACTION) ) {
			selectedFqan = getSelectorPanel().getSelectedFqan();
			em.setDefaultFqan(selectedFqan);
			this.setVisible(false);

		} else {
			selectedFqan = null;
			this.setVisible(false);
		}
		
	}

}
