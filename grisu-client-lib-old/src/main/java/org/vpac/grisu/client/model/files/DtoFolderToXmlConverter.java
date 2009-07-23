package org.vpac.grisu.client.model.files;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.model.dto.DtoFolder;
import org.vpac.grisu.model.dto.DtoRemoteObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DtoFolderToXmlConverter {

	static final Logger myLogger = Logger
			.getLogger(DtoFolderToXmlConverter.class.getName());

	private static DocumentBuilder docBuilder = null;

	public static DocumentBuilder getDocBuilder() {

		if (docBuilder == null) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				docBuilder = docFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		}
		return docBuilder;
	}

	public static Document convert(final DtoFolder folder, final int recursion_level) {

		Document output = getDocBuilder().newDocument();
		Element root = output.createElement("Files");

		root.setAttribute("absolutePath", "true");

		root.setAttribute("name", "fs_root");

		output.appendChild(root);

		Element root_element = null;

		root_element = createElement(output, folder);

		buildDirectoryStructure(output, root_element, folder, 1, recursion_level);
		// }
		root.appendChild(root_element);

		return output;

	}

	private static void buildDirectoryStructure(final Document output,
			final Element parentElement, final DtoRemoteObject parent,
			final int currentRecursion, final int maxRecursion) {

		List<DtoRemoteObject> filesAndDirs;
		String parentFolder = parentElement.getAttribute("path");
		if (parentFolder == null || "".equals(parentFolder)) {
			// means no folder
			return;
		}

		try {
			myLogger.debug("Accessing folder: " + parentFolder);
			filesAndDirs = parent.getChildren();
		} catch (FileSystemException e) {
			// TODO improve that
			e.printStackTrace();
			myLogger.error("Can't access folder: " + parentFolder);
			return;
		}

			for (DtoRemoteObject fo : filesAndDirs) {
				if (fo.isFolder()) {
					if (currentRecursion < maxRecursion) {
						Element element = createElement(output, fo);
						parentElement.appendChild(element);
						buildDirectoryStructure(output, element, fo,
								currentRecursion + 1, maxRecursion);
					} else {
						Element element = createElement(output, fo);
						parentElement.appendChild(element);
					}
				} else {
					Element element = createElement(output, fo);
					parentElement.appendChild(element);
				}
			}

	}

	private static Element createElement(final Document output,
			final DtoRemoteObject fo) throws FileSystemException {

		Element element = null;

		if (fo.isFolder()) {
			element = output.createElement("Directory");
			element.setAttribute("path", fo.getRootUrl());
		} else {
			element = output.createElement("File");
			element.setAttribute("path", fo.getRootUrl());
		}

		element.setAttribute("name", fo.getName());
		if (!fo.isFolder()) {
			element.setAttribute("size", new Long(((DtoFile) fo).getSize())
					.toString());
		}

		return element;
	}

}
