package cct.interfaces;

public interface FileChooserInterface {
	
	   String getDirectory();

	   String[] getDirectories();

	   String getFile();

	   String[] getFiles();

	   String pwd();

	   void setFileChooserVisible(boolean enable) throws Exception ;

}
