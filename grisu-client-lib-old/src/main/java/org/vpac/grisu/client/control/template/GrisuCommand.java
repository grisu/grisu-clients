

package org.vpac.grisu.client.control.template;

import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.ServiceInterface;



/**
 * A GrisuCommand is a command a Grisu client can execute. At the moment I can 
 * imagine 3 different kinds of commands:
 * <li><ul>a single job submission</ul>
 * <ul>an execution of one method of the grisu {@link ServiceInterface} (mostly the filesystem related ones</ul>
 * <ul>a (client) local command</ul>
 * </li>
 * 
 * @author Markus Binsteiner
 *
 */
public interface GrisuCommand {
	
//	public Options getOptions();

//	public void setOption(String keyPart, String valuePart) throws TemplateValidateException;

	/**
	 * Executes the command
	 * @param fqan the fqan to use to execute this command
	 * @throws JobSubmissionException 
	 */
	public void execute(String fqan) throws JobSubmissionException;

}
