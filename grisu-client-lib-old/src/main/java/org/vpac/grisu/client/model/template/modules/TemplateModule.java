

package org.vpac.grisu.client.model.template.modules;

import java.util.ArrayList;
import java.util.Map;

import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

/**
 * 
 * A TemplateModule is a logical grouping of {@link TemplateNode}. That's for example useful
 * if you want to display/render a JsdlTemplate in Swing or HTML.
 * 
 * @author Markus Binsteiner
 *
 */
public interface TemplateModule {
	

	/**
	 * Returns a reference to the "parent" (jsdl)template.
	 * @return the template
	 */
	public JsdlTemplate getTemplate();
	
	/**
	 * Returns all the {@link TemplateNode} of this module. 
	 * @return all TemplateNodes
	 */
	public Map<String, TemplateNode> getTemplateNodes();
	
//	/**
//	 * Returns all the {@link TemplateNodes} of thsi module with the specified type.
//	 * @param type the type
//	 * @return all TemplateNodes or null if there are no TemplateNodes of this type
//	 */
//	public ArrayList<TemplateNode> getTemplateNodes(String type);
//	
	/**
	 * This method returns a list of TemplateNode types this module needs to be created successfully.
	 * @return all needed TemplateNode types for this module
	 */
	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor();
	
	/**
	 * Fills this module with the TemplateNodes. The provided list of TemplateNodes has to contain
	 * all the needed TemplateNodes that are needed for this module (see {@link #getTemplateNodeNamesThisModuleClaimsResponsibleFor()}).
	 * If some template nodes are missing, this module is 
	 * @param templateNodes
	 */
	public void setTemplateNodes(ArrayList<TemplateNode> templateNodes);
	
	/**
	 * Returns the module name.
	 * @return the name
	 */
	public String getModuleName();
	
	/**
	 * This method gets executed after the template input is read and preprocessed and
	 * before. Most of the times this would be an empty method. It's useful to store
	 * job properties of input that is not required to submit a job but that's
	 * necessary to display the results for example.
	 */
	public void process() throws TemplateModuleProcessingException;	
	
//	/**
//	 * Resets the TemplateNodes of this module to their original state
//	 */
//	public void reset();
	
	
	/**
	 * This sets some (optional) module-wide and TemplateNode independent options.
	 * In most cases you won't need to implement anything there. Except you expect your module to be called something like:
	 * grisu_modules="Common[useMds=false]". In this case the map would have one key/value pair (useMDS => false). The 
	 * configuration happens before the {@link #initializeTemplateNodes(Map)} method is called, right after the constructor.
	 * 
	 * @param moduleConfig a key/value pair with configution options
	 */
	public void configureModule(Map<String, String> moduleConfig);

	
	/**
	 * Returns the configuration of this module.
	 * @return the configuation
	 */
	public Map<String, String> getConfiguration();	

	
	/**
	 * Within this methods you can initialize possible module internas.
	 * You won't need to implement anything here in most cases, I assume.
	 * @param templateNodes all the templateNodes that are assigned to this module
	 */
	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes);


}
