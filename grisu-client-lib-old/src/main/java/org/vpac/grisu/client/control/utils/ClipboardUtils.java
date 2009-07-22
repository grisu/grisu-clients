package org.vpac.grisu.client.control.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardUtils implements ClipboardOwner {
	
	final static public ClipboardUtils defaultClipboard = new ClipboardUtils(); 
	
	  public void setClipboardContents( String aString ){
		    StringSelection stringSelection = new StringSelection( aString );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, this );
		  }

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// nothing to do here
	}

}
