package org.vpac.grisu.client.view.swing.mainPanel;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.vpac.grisu.settings.ClientPropertiesManager;

public class WindowSaver implements AWTEventListener {
	
	static final Logger myLogger = Logger.getLogger(WindowSaver.class
			.getName());
	
	static PropertiesConfiguration propertiesConfig;
	
	private static WindowSaver saver;
	private Map frameMap;

	private WindowSaver() {
		frameMap = new HashMap();
		try {
			propertiesConfig = ClientPropertiesManager.getClientConfiguration();
		} catch (ConfigurationException e) {
			myLogger.error("Could not init properties configuration to save window position...");
		}
	}
	
	public static WindowSaver getInstance() {
		if ( saver == null ) {
			saver = new WindowSaver();
		}
		return saver;
	}
	
	public void eventDispatched(AWTEvent evt) {

        try {
            if(evt.getID( ) == WindowEvent.WINDOW_OPENED) {
                ComponentEvent cev = (ComponentEvent)evt;
                if(cev.getComponent( ) instanceof JFrame) {
                    JFrame frame = (JFrame)cev.getComponent( );
                    loadSettings(frame);
                }
            }
        }catch(Exception ex) {
            myLogger.error(ex.toString( ));
        }

		
	}
	
    public static void loadSettings(JFrame frame) throws IOException {

        String name = frame.getName( );
        int x = getInt(name+".x",100);
        int y = getInt(name+".y",100);
        int w = getInt(name+".w",780);
        int h = getInt(name+".h",680);
        frame.setLocation(x,y);
        frame.setSize(new Dimension(w,h));
        saver.frameMap.put(name,frame);
        frame.validate( );
    }

	public static int getInt(String name, int value) { 

		String v = (String)(propertiesConfig.getProperty(name)); 
		if(v == null) {
			return value;
		}
		return Integer.parseInt(v);
	}
	
    public static void saveSettings( ) {

        Iterator it = saver.frameMap.keySet( ).iterator( );
        while(it.hasNext( )) {    
            String name = (String)it.next( ); 
            JFrame frame = (JFrame)saver.frameMap.get(name);    
            propertiesConfig.setProperty(name+".x",""+frame.getX( ));    
            propertiesConfig.setProperty(name+".y",""+frame.getY( ));    
            propertiesConfig.setProperty(name+".w",""+frame.getWidth( ));    
            propertiesConfig.setProperty(name+".h",""+frame.getHeight( ));
        } 
        try {
			propertiesConfig.save();
		} catch (ConfigurationException e) {
			myLogger.error("Couldn't save window properties: "+e.getLocalizedMessage());
		}
    }


}
