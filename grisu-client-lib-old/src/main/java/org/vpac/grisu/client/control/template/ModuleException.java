

package org.vpac.grisu.client.control.template;

import org.vpac.grisu.client.model.template.modules.TemplateModule;

public class ModuleException extends Exception {
	
	private TemplateModule module = null;
	
	public ModuleException(TemplateModule module, Exception e) {
		super(e);
		this.module = module;
	}
	
	public TemplateModule getModule() {
		return this.module;
	}

}
