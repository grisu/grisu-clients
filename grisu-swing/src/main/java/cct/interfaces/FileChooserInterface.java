package cct.interfaces;

public interface FileChooserInterface {

	String[] getDirectories();

	String getDirectory();

	String getFile();

	String[] getFiles();

	String pwd();

	void setFileChooserVisible(boolean enable) throws Exception;

}
