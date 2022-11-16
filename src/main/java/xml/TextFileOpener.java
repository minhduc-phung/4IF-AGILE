package xml;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

public class TextFileOpener extends FileFilter {// Singleton
	
	private static TextFileOpener instance = null;
	private TextFileOpener(){}
	protected static TextFileOpener getInstance(){
		if (instance == null) instance = new TextFileOpener();
		return instance;
	}

 	public File open(boolean read) throws ExceptionXML{
 		int returnVal;
 		JFileChooser jFileChooserText = new JFileChooser();
        jFileChooserText.setFileFilter(this);
        jFileChooserText.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooserText.setCurrentDirectory(new File("."));
        if (read)
         	returnVal = jFileChooserText.showOpenDialog(null);
        else
         	returnVal = jFileChooserText.showSaveDialog(null);
		if (returnVal == JFileChooser.CANCEL_OPTION) throw new ExceptionXML("Save or open cancelled.");
		if (returnVal != JFileChooser.APPROVE_OPTION)
			throw new ExceptionXML("Problem when opening file.");
        return new File(jFileChooserText.getSelectedFile().getAbsolutePath());
 	}
 	
 	@Override
    public boolean accept(File f) {
    	if (f == null) return false;
    	if (f.isDirectory()) return true;
    	String extension = getExtension(f);
    	if (extension == null) return false;
    	return extension.contentEquals("txt");
    }

	@Override
	public String getDescription() {
		return "TXT File";
	}

    private String getExtension(File f) {
	    String filename = f.getName();
	    int i = filename.lastIndexOf('.');
	    if (i>0 && i<filename.length()-1) 
	    	return filename.substring(i+1).toLowerCase();
	    return null;
   }
}
