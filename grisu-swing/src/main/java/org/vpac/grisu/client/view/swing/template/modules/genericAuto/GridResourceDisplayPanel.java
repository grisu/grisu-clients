package org.vpac.grisu.client.view.swing.template.modules.genericAuto;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vpac.grisu.client.view.swing.template.panels.GridResourceSuggestionPanel;

import au.org.arcs.jcommons.interfaces.GridResource;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GridResourceDisplayPanel extends JPanel {

	
	private JLabel rankingLabel;
	private JLabel jobslotLabel;
	private JLabel queueLabel;
	private JLabel siteLabel;
	private final GridResource resource;
	private GridResourceSuggestionPanel parent;
	
	/**
	 * Create the panel
	 */
	public GridResourceDisplayPanel(GridResourceSuggestionPanel parentpanel, GridResource resource) {
		super();
		this.parent = parentpanel;
		this.resource = resource;

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent arg0) {
				GridResourceDisplayPanel.this.parent.setCurrentlySelectedResourcePanel(GridResourceDisplayPanel.this);
			}
		});

		setPreferredSize(new Dimension(100, 100));
		setBackground(Color.WHITE);
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getSiteLabel(), new CellConstraints(2, 2));
		add(getQueueLabel(), new CellConstraints(2, 3));
		
		//
		add(getJobslotLabel(), new CellConstraints(2, 5));
		add(getRankingLabel(), new CellConstraints(2, 7));

		getSiteLabel().setText(resource.getSiteName());
		getQueueLabel().setText(resource.getQueueName());
		getJobslotLabel().setText("Free slots: "+resource.getFreeJobSlots());
		getRankingLabel().setText("Rank: "+resource.getRank());
	}
	
	public GridResource getResource() {
		return resource;
	}
	/**
	 * @return
	 */
	protected JLabel getSiteLabel() {
		if (siteLabel == null) {
			siteLabel = new JLabel();
		}
		return siteLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getQueueLabel() {
		if (queueLabel == null) {
			queueLabel = new JLabel();
		}
		return queueLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getJobslotLabel() {
		if (jobslotLabel == null) {
			jobslotLabel = new JLabel();
		}
		return jobslotLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getRankingLabel() {
		if (rankingLabel == null) {
			rankingLabel = new JLabel();
		}
		return rankingLabel;
	}

}
