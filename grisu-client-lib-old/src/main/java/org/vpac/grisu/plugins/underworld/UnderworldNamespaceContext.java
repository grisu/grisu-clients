

package org.vpac.grisu.plugins.underworld;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;



public class UnderworldNamespaceContext implements NamespaceContext {
	  
	  public UnderworldNamespaceContext () {

	  }
	  
	  public String getNamespaceURI (String prefix) {
	    if (prefix.equals("uw")) {
	      return "http://www.vpac.org/StGermain/XML_IO_Handler/Jun2003";
	    }
	    else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
	      return XMLConstants.XML_NS_URI;
	    }
	    else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
	      return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
	    }
	    else {
	      return XMLConstants.NULL_NS_URI;
	    }
	  }
	  
	  public String getPrefix (String namespaceURI) {
	    if (namespaceURI.equals("http://www.vpac.org/StGermain/XML_IO_Handler/Jun2003")) {
	      return "uw";
	    }
	    else if (namespaceURI.equals(XMLConstants.XML_NS_URI)) {
	      return XMLConstants.XML_NS_PREFIX;
	    }
	    else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
	      return XMLConstants.XMLNS_ATTRIBUTE;
	    }
	    else {
	      return null;
	    }
	  }
	  
	  public Iterator getPrefixes (String namespaceURI) {
	    // not implemented for the example
	    return null;
	  }
	  
	}

