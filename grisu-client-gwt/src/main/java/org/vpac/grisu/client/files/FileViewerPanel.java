package org.vpac.grisu.client.files;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.client.GwtServiceInterfaceWrapperAsync;
import org.vpac.grisu.client.model.GwtGrisuCacheFile;
import org.vpac.grisu.client.model.GwtGrisuRemoteFile;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

public class FileViewerPanel extends Panel {
	
	private Label label;
	private final GwtServiceInterfaceWrapperAsync service;
	FileTypeViewerPanel temp;
	
	private Map<String, FileTypeViewerPanel> panels = new HashMap<String, FileTypeViewerPanel>();

	public FileViewerPanel(GwtServiceInterfaceWrapperAsync service) {
		this.service = service;
		setLayout(new AnchorLayout());
	
	}
	
	public void displayFile(final GwtGrisuRemoteFile file) {
		
		service.downloadFile(file, new AsyncCallback<GwtGrisuCacheFile>(){

			public void onFailure(Throwable arg1) {

				Window.alert("Couldn't download file: "+arg1.getLocalizedMessage());
			}

			public void onSuccess(GwtGrisuCacheFile file) {
				


//				String html = "<div>no viewer plugin found</div";
//				if ( url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif") || url.endsWith(".png") ) {
//					html = generateImageHtmlCode(url);				
//				} else if ( url.endsWith(".txt") ) {
//					html = generateObjectHtmlCode(url);
//				}
				
				String html = generateObjectHtmlCode(file.getPublicUrl(), file.getMimeType());
				
				Panel temp = new Panel("File preview", html);
				
				removeAll(true);
				add(temp, new AnchorLayoutData("100% 100%"));
				doLayout();
				System.out.println("Layout done");
			}

		});

	}
	
	private String generateImageHtmlCode(String url) {
		
		String html = "<div align=\"center\" width=\"100%\" height=\"100%\"><img src=\""+url+"\" alt=\"Test\" /></div>";
		return html;
		
	}
	
	private String generateObjectHtmlCode(String url, String mimeType) {
//		String html = "<object width=\"100%\" height=\"100%\" data=\""+url+"\" type=\"text/plain\"></object>";
		String html = "<object width=\"100%\" height=\"100%\" data=\""+url+"\" type=\""+mimeType+"\"></object>";
		return html;
	}

		
}
