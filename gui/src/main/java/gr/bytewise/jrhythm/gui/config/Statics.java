package gr.bytewise.jrhythm.gui.config;

import gr.bytewise.jrhythm.gui.tools.DataHandler;
import gr.bytewise.jrhythm.gui.tools.GsonDataHandler;

import java.io.File;

public class Statics {

	public final static String HOMEPAGE = "http://jrhythm.bytewise.gr/";

	public final static String USERHOME = System.getProperty("user.home");

	public final static String CONFIG_FILENAME = ".jrhythm-gui.config";

	public final static String CONFIG = USERHOME + File.separator + CONFIG_FILENAME;

	public final static String LOCALE = "UTF-8";

	public final static DataHandler dataHandler = new GsonDataHandler();
}
