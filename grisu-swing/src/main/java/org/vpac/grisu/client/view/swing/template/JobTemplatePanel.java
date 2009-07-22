

package org.vpac.grisu.client.view.swing.template;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.JsdlTemplateEvent;
import org.vpac.grisu.client.model.template.JsdlTemplateListener;
import org.vpac.grisu.client.model.template.modules.TemplateModule;

/**
 * Creates a panel that renders all necessary fields to enable the user to create a
 * job for a specific jsdl template.
 * 
 * It sepereates all fields into tabs according to the modules that are in a {@link JsdlTemplate}.
 * 
 * @author Markus Binsteiner
 *
 */
public class JobTemplatePanel extends JPanel implements JsdlTemplateListener {
	
	static final Logger myLogger = Logger.getLogger(JobTemplatePanel.class.getName());

	private JTabbedPane tabbedPane;
	private JsdlTemplate template = null;
	
	/**
	 * Create the panel
	 */
	public JobTemplatePanel() {
		super();
		setLayout(new BorderLayout());
		add(getTabbedPane(), BorderLayout.CENTER);
		//
	}
	
	public JsdlTemplate getTemplate() {
		return template;
	}
	
	public void setTemplate(JsdlTemplate template) throws ModuleException {
		this.template = template;
		this.template.addJsdlTemplateListener(this);
		
		for ( String moduleName : template.getModules().keySet() ) {
			
			TemplateModule module = template.getModules().get(moduleName);

			//TODO is that good enough. I reckon. this method will always return something like: package org.vpac....
//			String packageNameModule = module.getClass().getPackage().toString().substring(8);
			String packageNameModule = module.getClass().toString().substring(6);
//			String classNamePanel = packageNameModule.replace(".model.", ".view.swing.")+"."+moduleName;
			String classNamePanel = packageNameModule.replace(".model.", ".view.swing.");
			myLogger.debug("Trying to create module panel: "+classNamePanel);

			Class moduleClass = null;

			try {
				moduleClass = Class.forName(classNamePanel);
			} catch (ClassNotFoundException e) {
				throw new ModuleException(template.getModule(moduleName), e);
			}
			
			ModulePanel mp = null;
			try {
				mp = (ModulePanel)moduleClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ModuleException(template.getModule(moduleName), e);
			}
			
			mp.setTemplateModule(template.getModule(moduleName));
			
			getTabbedPane().addTab(module.getModuleName(), mp.getPanel());
			
			
		}
	}
	/**
	 * @return
	 */
	protected JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
		}
		return tabbedPane;
	}

	public void submissionExceptionOccured(JsdlTemplateEvent event,
			JobSubmissionException exception) {

		// not important. The panel get's notified via the TemplateNode if something is wrong.
		
	}

	public void templateStatusChanged(JsdlTemplateEvent event) {

//		if ( template.getStatus() == JsdlTemplate.STATUS_JSDL_CREATED ) {
//			// clear old values
//			for ( TemplateModule module : template.getModules().values() ) {
//				module.reset();
//			}
//		}
		
	}

}
