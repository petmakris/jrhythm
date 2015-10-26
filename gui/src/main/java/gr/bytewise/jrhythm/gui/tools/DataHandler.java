package gr.bytewise.jrhythm.gui.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface DataHandler {
	
	public  <T> T loadObject(File f, Class<T> classtype) throws FileNotFoundException;

	public <T> void saveObject(File file, Object obj, Class<T> classtype) throws IOException;

	public <T> T loadObject(String msg, Class<T> classtype);

	public <T> String saveObject(Object obj, Class<T> classtype);

}
