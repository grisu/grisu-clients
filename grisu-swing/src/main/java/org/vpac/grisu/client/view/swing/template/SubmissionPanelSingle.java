package org.vpac.grisu.client.view.swing.template;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.control.exceptions.TemplateException;
import org.vpac.grisu.client.control.template.TemplateManager;
import org.vpac.grisu.client.model.template.JobCreationInterface;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.view.swing.utils.Utils;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.frontend.control.clientexceptions.NoSuchTemplateExceptionClient;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;

import au.org.arcs.jcommons.constants.Constants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The root panel for job submission. Displays a selection of templates (local &
 * remote).
 * 
 * @author Markus Binsteiner
 * 
 */
public class SubmissionPanelSingle extends JPanel implements JobCreationInterface,
FqanListener, SubmissionPanelInterface {

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	private JTextField currentVOField;
	private JComboBox fqanComboBox;
	private JButton button;

	static final Logger myLogger = Logger.getLogger(SubmissionPanelSingle.class
			.getName());
	private JPanel jsdlTemplatePanel;
	private TemplateManager templateManager = null;
	private final DefaultListModel remoteListModel = new DefaultListModel();
	private final LocalTemplateListModel localListModel = new LocalTemplateListModel();
	private String currentTemplate = null;

	private final Map<String, JobPanel> allJobPanels = new HashMap<String, JobPanel>();

	private FormLayout layout = null;

	private EnvironmentManager em = null;

	// private String currentFqan = null;

	// Create a file chooser
	final JFileChooser fc = new JFileChooser();

	/**
	 * Create the panel
	 */
	public SubmissionPanelSingle(EnvironmentManager em) {
		super();

		FileFilter filter1 = new ExtensionFileFilter("XML", new String[] {
				"xml", "XML" });
		fc.setFileFilter(filter1);

		this.em = em;
		layout = new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(55dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
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
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,});
		setLayout(layout);
		add(getJsdlTemplatePanel(), "2, 2, 5, 17, fill, fill");
		add(getCurrentVOField(), "2, 20");
		add(getButton(), "6, 20");
		//

		em.addFqanListener(this);

		setFqanLabelText(em.getDefaultFqan());
		// EnvironmentManager.getDefaultManager().addFqanListener(this);

	}



	public void addLocalTemplate() {
		// TODO Auto-generated method stub

	}

	public void fqansChanged(FqanEvent event) {

		if (FqanEvent.DEFAULT_FQAN_CHANGED == event.getEvent_type()) {
			setFqanLabelText(event.getFqan());
		} else if (FqanEvent.FQAN_ADDED == event.getEvent_type()) {
			// nothing to do here
		} else if (FqanEvent.FQAN_REMOVED == event.getEvent_type()) {
			// nothing to do here
		} else if (FqanEvent.FQANS_REFRESHED == event.getEvent_type()) {
			setFqanLabelText(em.getDefaultFqan());
		}
	}

	/**
	 * @return
	 */
	protected JButton getButton() {
		if (button == null) {
			button = new JButton();
			button.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					try {
						JsdlTemplate template = allJobPanels.get(
								SubmissionPanelSingle.this.currentTemplate)
								.getTemplate();
						template.startSubmission(em.getDefaultFqan());

					} catch (JobSubmissionException e1) {

						Utils.showErrorMessage(em, SubmissionPanelSingle.this,
								"jobSubmissionError", e1);

					}

				}
			});
			button.setText("Submit");
		}
		return button;
	}

	/**
	 * @return
	 */
	protected JTextField getCurrentVOField() {
		if (currentVOField == null) {
			currentVOField = new JTextField();
			currentVOField.setEditable(false);
			currentVOField.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return currentVOField;
	}

	/**
	 * @return
	 */
	protected JPanel getJsdlTemplatePanel() {
		if (jsdlTemplatePanel == null) {
			jsdlTemplatePanel = new JPanel();
			jsdlTemplatePanel.setLayout(new CardLayout());
		}
		return jsdlTemplatePanel;
	}

	public JPanel getPanel() {
		return this;
	}

	public JsdlTemplate getTemplate() {

		return allJobPanels.get(currentTemplate).getTemplate();
	}

	private void refreshTemplate(int templateLocation, String templateName)
	throws NoSuchTemplateException, TemplateException {

		// final String old_template = currentTemplate;
		// currentTemplate = templateLocation + "_" + templateName;

		JobPanel tempPanel = allJobPanels.get(templateLocation + "_"
				+ templateName);

		getJsdlTemplatePanel().remove(tempPanel);
		allJobPanels.remove(templateLocation + "_" + templateName);

		if (templateLocation == TemplateManager.REMOTE_TEMPLATE_LOCATION) {
			templateManager.refreshServerTemplate(templateName);
			showJobTemplatePanel(TemplateManager.REMOTE_TEMPLATE_LOCATION,
					templateName);
		} else if (templateLocation == TemplateManager.LOCAL_TEMPLATE_LOCATION) {
			templateManager.refreshLocalTemplates();
			showJobTemplatePanel(TemplateManager.LOCAL_TEMPLATE_LOCATION,
					templateName);
		}

	}

	private void setFqanLabelText(String fqan) {
		String fqan_short = null;
		if ((fqan == null) || Constants.NON_VO_FQAN.equals(fqan)) {
			fqan_short = Constants.NON_VO_FQAN;
		} else {
			fqan_short = fqan.substring(fqan.lastIndexOf("/") + 1);
		}
		getCurrentVOField().setText(fqan_short);
		getCurrentVOField().setToolTipText(fqan);
	}

	public void setRemoteApplication(String application) {

		SubmissionPanelSingle.this.setCursor(Cursor
				.getPredefinedCursor(Cursor.WAIT_CURSOR));

		if ( remoteListModel.contains(application) ) {
			try {
				showJobTemplatePanel(
						TemplateManager.REMOTE_TEMPLATE_LOCATION,
						application);
			} catch (NoSuchTemplateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TemplateException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} finally {
				SubmissionPanelSingle.this
				.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

	}

	public void setSubmissionFQAN(String fqan) {
		// TODO Auto-generated method stub

	}



	public void setTemplateManager(TemplateManager manager) {
		this.templateManager = manager;

		for (String templateName : this.templateManager.getServerTemplates()
				.keySet()) {
			remoteListModel.addElement(templateName);
		}
		for (String templateName : this.templateManager.getLocalTemplates()
				.keySet()) {
			localListModel.addElement(templateName);
		}

	}



	private void showJobTemplatePanel(final int templateLocation,
			final String templateName) throws NoSuchTemplateException,
			TemplateException {

		if (templateName != null) {

			final String old_template = currentTemplate;
			currentTemplate = templateLocation + "_" + templateName;

			// if ( old_template != null && old_template.equals(currentTemplate)
			// ) {
			// allJobPanels.get(currentTemplate).toggleView();
			// return;
			// }

			JobPanel tempPanel = allJobPanels.get(templateLocation + "_"
					+ templateName);

			if (tempPanel == null) {
				tempPanel = new JobPanel();
				allJobPanels.put(templateLocation + "_" + templateName,
						tempPanel);
				getJsdlTemplatePanel().add(tempPanel,
						templateLocation + "_" + templateName);
				CardLayout cl = (CardLayout) (getJsdlTemplatePanel()
						.getLayout());
				cl.show(getJsdlTemplatePanel(), templateLocation + "_"
						+ templateName);

				tempPanel.revalidate();
				final JobPanel newPanel = tempPanel;
				new Thread() {
					@Override
					public void run() {
						try {
							JsdlTemplate tempTemplate = templateManager
							.getTemplate(templateLocation, templateName);
							if (tempTemplate == null) {
								myLogger.warn("No template found for name: "
										+ templateName);
								currentTemplate = old_template;
								throw new NoSuchTemplateExceptionClient(
										"No template found for name: "
										+ templateName);
							}

							newPanel.setTemplate(tempTemplate);
							myLogger
							.debug("Template set for new templatePanel.");
						} catch (Exception e) {
							e.printStackTrace();
							myLogger
							.warn("Could not create panel for template \""
									+ templateName
									+ "\": "
									+ e.getLocalizedMessage());
							currentTemplate = old_template;

							// Utils.showErrorMessage(em, SubmissionPanel.this,
							// "cantCreateJobPanel", e);

							// throw new TemplateException(
							// "Cound not create panel for template \""
							// + templateName + "\": "
							// + e.getLocalizedMessage(), e);
						}
					}
				}.start();

			} else {
				CardLayout cl = (CardLayout) (getJsdlTemplatePanel()
						.getLayout());
				cl.show(getJsdlTemplatePanel(), templateLocation + "_"
						+ templateName);
			}
			// allJobPanels.get(templateLocation + "_" + templateName);
		}

		// currentTemplate = templateLocation+"_"+templateName;
	}

}
