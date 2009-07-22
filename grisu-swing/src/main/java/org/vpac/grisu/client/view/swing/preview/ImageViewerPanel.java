

package org.vpac.grisu.client.view.swing.preview;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXImageView;
import org.vpac.grisu.client.model.files.GrisuFileObject;

public class ImageViewerPanel extends PreviewPanelInsert {
	
	static final Logger myLogger = Logger.getLogger(ImageViewerPanel.class.getName());

	private final String[] extensions = new String[]{"jpg", "jpeg", "png", "gif", "bmp"};
	private final String[] mimeTypes = new String[]{};
	
	private double currentScale = 1;
	
	private JXImageView imageView;
	private File imageFile = null;
	
	/**
	 * Create the panel
	 */
	public ImageViewerPanel() {
		super();
		setLayout(new BorderLayout());
		add(getImageView());
		//
	}

	@Override
	public String[] getHandledExtensions() {
		return extensions;
	}

	@Override
	public String[] getHandledMimeTypes() {
		return mimeTypes;
	}

	@Override
	public void refresh(GrisuFileObject file) {
		setFileToPreview(file);
	}

	@Override
	public void setDataToPreview(byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFileToPreview(GrisuFileObject file) {
		this.imageFile = file.getLocalRepresentation(true);
		if ( imageFile != null ) {
//			ImageIcon image = null;
			try {
//				image = new ImageIcon(ImageIO.read(imageFile), imageFile.getName());
				getImageView().setImage(imageFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	protected JXImageView getImageView() {
		if (imageView == null) {
			imageView = new JXImageView();
			imageView.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					if ( e.getClickCount() == 2 ) {
						currentScale = 1;
						getImageView().setScale(currentScale);
					}
				}
			});
			imageView.addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(final MouseWheelEvent e) {
					double amount = e.getWheelRotation();
					myLogger.debug("Mouse wheel rotation amount: "+amount);
					if ( amount < 0 ) {
						currentScale = currentScale + currentScale*0.10;
					} else if ( amount > 0 ) {
						currentScale = currentScale - currentScale/10;
					}
					imageView.setScale(currentScale);
				}
			});
			imageView.setDragEnabled(false);
		}
		return imageView;
	}

}
