package org.vpac.grisu.client.control.files;

import java.util.Comparator;

public class FileTransferComparator implements Comparator {

	public int compare(Object o1, Object o2) {

		FileTransfer ft1 = (FileTransfer) o1;
		FileTransfer ft2 = (FileTransfer) o2;

		// TODO this is just for now...
		return ft2.getStatus() - ft1.getStatus();

	}

}
