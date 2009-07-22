

package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * This is only used by the "Common" module to store a description of the current job
 * on the grisu core service. It's not included in the jsdl (because it's not required
 * for a job to run). 
 * 
 * @author Markus Binsteiner
 *
 */
public class Description extends JPanel implements TemplateNodePanel {
	
	static final Logger myLogger = Logger.getLogger(Description.class
			.getName());

	private JLabel errorLabel;
	private JEditorPane editorPane;
	private JScrollPane scrollPane;
	private JCheckBox includeNotesOnCheckBox;
	
	private TemplateNode node = null;
	
	/**
	 * Create the panel
	 */
	public Description() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		add(getIncludeNotesOnCheckBox(), new CellConstraints(2, 2, CellConstraints.LEFT, CellConstraints.TOP));
		add(getScrollPane(), new CellConstraints(2, 6, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getErrorLabel(), new CellConstraints(2, 4, 3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		//
	}

	public JPanel getTemplateNodePanel() {
		// TODO Auto-generated method stub
		return null;
	}



	public void setTemplateNodeValue() {
		// TODO Auto-generated method stub
		
	}
	
	private void displayEditorPane(boolean display) {
		
		getScrollPane().setVisible(display);
		
	}
	
	/**
	 * @return
	 */
	protected JCheckBox getIncludeNotesOnCheckBox() {
		if (includeNotesOnCheckBox == null) {
			includeNotesOnCheckBox = new JCheckBox();
			includeNotesOnCheckBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					
					displayEditorPane(getIncludeNotesOnCheckBox().isSelected());
					
				}
			});
			includeNotesOnCheckBox.setText("Include notes on job");
		}
		return includeNotesOnCheckBox;
	}
	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getEditorPane());
			scrollPane.setVisible(false);
		}
		return scrollPane;
	}
	/**
	 * @return
	 */
	protected JEditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			editorPane.setBackground(Color.WHITE);
		}
		return editorPane;
	}
	/**
	 * @return
	 */

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {
		
		this.node = node;
		this.node.setTemplateNodeValueSetter(this);
		node.addTemplateNodeListener(this);
		
	}
	
	public void templateNodeUpdated(TemplateNodeEvent event) {
		
		if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_INVALID ) {
			String message = event.getMessage();
			if ( message == null ) 
				message = TemplateNodeEvent.DEFAULT_PROCESSED_INVALID_MESSAGE;
			
			errorLabel.setText(message);
			errorLabel.setVisible(true);
		} else if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_VALID ) {
			errorLabel.setVisible(false);
		} 
		
	}
	/**
	 * @return
	 */
	protected JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setForeground(Color.RED);
			errorLabel.setVisible(false);
		}
		return errorLabel;
	}

	public String getExternalSetValue() {
		
		if ( getIncludeNotesOnCheckBox().isSelected() )
			return getEditorPane().getText();
		else
			return null;
		
	}
	
	public void setExternalSetValue(String value) {
		if ( value != null ) {
			getIncludeNotesOnCheckBox().setSelected(true);
			getEditorPane().setText(value);
		} else {
			getIncludeNotesOnCheckBox().setSelected(false);
		}
		
	}
	
	public void reset() {

		getEditorPane().setText("");
		
	}
	
	
	// event stuff
	// ========================================================
	
	private Vector<ValueListener> valueChangedListeners;

	private void fireSitePanelEvent(String newValue) {
		
		myLogger.debug("Fire value changed event: new value: "+newValue);
		// if we have no mountPointsListeners, do nothing...
		if (valueChangedListeners != null && !valueChangedListeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<ValueListener> valueChangedTargets;
			synchronized (this) {
				valueChangedTargets = (Vector<ValueListener>) valueChangedListeners.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration<ValueListener> e = valueChangedTargets.elements();
			while (e.hasMoreElements()) {
				ValueListener valueChanged_l = (ValueListener) e.nextElement();
				valueChanged_l.valueChanged(this, newValue);
			}
		}
	}

	// register a listener
	synchronized public void addValueListener(ValueListener l) {
		if (valueChangedListeners == null)
			valueChangedListeners = new Vector<ValueListener>();
		valueChangedListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeValueListener(ValueListener l) {
		if (valueChangedListeners == null) {
			valueChangedListeners = new Vector<ValueListener>();
		}
		valueChangedListeners.removeElement(l);
	}


}
