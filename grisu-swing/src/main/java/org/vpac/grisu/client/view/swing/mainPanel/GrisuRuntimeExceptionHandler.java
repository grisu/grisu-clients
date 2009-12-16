package org.vpac.grisu.client.view.swing.mainPanel;

import java.awt.Frame;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class GrisuRuntimeExceptionHandler implements UncaughtExceptionHandler {

	static final Logger myLogger = Logger
			.getLogger(GrisuRuntimeExceptionHandler.class.getName());

	private Frame frame = null;

	public GrisuRuntimeExceptionHandler(Frame frame) {
		this.frame = frame;
	}

	private void logException(Thread t, Throwable e, String msg) {
		// todo: start a thread that sends an email, or write to a log file, or
		// send a JMS message...whatever
		myLogger.error(msg);
	}

	private void showException(Thread t, Throwable e) {
		String msg = String.format("Unexpected problem on thread %s: %s", t
				.getName(), e.getMessage());

		logException(t, e, msg);

		// note: in a real app, you should locate the currently focused frame
		// or dialog and use it as the parent. In this example, I'm just passing
		// a null owner, which means this dialog may get buried behind
		// some other screen.
		// Utils.showErrorMessage(null, frame, msg, e);
	}

	public void uncaughtException(final Thread t, final Throwable e) {

		if (SwingUtilities.isEventDispatchThread()) {
			showException(t, e);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					showException(t, e);
				}
			});
		}

	}

}
