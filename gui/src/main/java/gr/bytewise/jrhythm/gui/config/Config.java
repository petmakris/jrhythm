package gr.bytewise.jrhythm.gui.config;

import gr.bytewise.jrhythm.api.RhythmComboModel;
import gr.bytewise.jrhythm.api.RhythmDefaults;
import gr.bytewise.jrhythm.api.enums.OSType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "Config")
@XmlAccessorType(XmlAccessType.FIELD)
public class Config {

	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	@XmlElement(name = "MifiConfiguration")
	private MidiConfig midiConfig;

	@XmlElementWrapper(name = "Rhythms")
	@XmlElement(name = "Rhythm")
	private RhythmComboModel rhythms;

	@XmlElement(name = "SavedRhythmIndex")
	private Integer rhythmIndex;

	@XmlElement(name = "Directory")
	private String directory;

	@XmlElement(name = "LookAndFeel")
	private Integer lookAndFeel;

	@XmlElement(name = "Language")
	private String language;

	private Config() {
	}

	private static class SINGLETON_HOLDER {
		protected static final Config INSTANCE = new Config(null);
	}

	public static Config getInstance() {
		return SINGLETON_HOLDER.INSTANCE;
	}

	private Config(Void ignore) {
		reload(false);
	}

	public void reload(boolean reset) {
		if (reset) {
			copyfrom(defaultConfig());
			return;
		}
		try {
			copyfrom(Statics.dataHandler.loadObject(FileUtils.readFileToString(new File(Statics.CONFIG), "UTF-8"), Config.class));
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Error while trying to load application configuration: {}."
					+ "Creating/Loading default configuration.", e.getMessage());
			copyfrom(defaultConfig());
		}
	}

	public boolean save() {
		try {
			PrintWriter pw = new PrintWriter(new File(Statics.CONFIG), "UTF-8");
			pw.println(Statics.dataHandler.saveObject(getInstance(), Config.class));
			pw.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Config defaultConfig() {
		Config df = new Config();
		df.setMidiConfig(new MidiConfig("Gervill", 0, 0));
		df.setRhythms(RhythmDefaults.createRhythms());
		df.setRhythmIndex(0);
		df.setDirectory(Statics.USERHOME);
		df.setLookAndFeel(0);
		df.setLanguage("English");
		return df;
	}

	private void copyfrom(Config c) {
		setMidiConfig(c.getMidiConfig());
		setRhythmIndex(c.getRhythmIndex());
		setRhythms(c.getRhythms());
		setDirectory(c.getDirectory());
		setLookAndFeel(c.getLookAndFeel());
		setLanguage(c.getLanguage());
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String lang) {
		language = lang;
	}

	public MidiConfig getMidiConfig() {
		return midiConfig;
	}

	public void setMidiConfig(MidiConfig midiConfig) {
		this.midiConfig = midiConfig;
	}

	public RhythmComboModel getRhythms() {
		return rhythms;
	}

	public void setRhythms(RhythmComboModel rhythms) {
		this.rhythms = rhythms;
	}

	public Integer getRhythmIndex() {
		return rhythmIndex;
	}

	public void setRhythmIndex(Integer rhythmIndex) {
		this.rhythmIndex = rhythmIndex;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String path) {
		directory = path;
	}

	public Integer getLookAndFeel() {
		return lookAndFeel;
	}

	public void setLookAndFeel(int looks) {
		lookAndFeel = looks;
	}

	public static OSType getOSType() {
		String os = System.getProperty("os.name");
		boolean x64 = System.getProperty("os.arch").contains("64");
		if (os.contains("Mac"))
			return OSType.Macosx;
		if (os.contains("Windows"))
			return x64 ? OSType.Windows64 : OSType.Windows32;
		if (os.contains("Linux"))
			return x64 ? OSType.Linux64 : OSType.Linux32;
		return OSType.Unknown;
	}

	public static boolean clearPreferences() {
		try {
			new File(Statics.CONFIG).delete();
			logger.info("Preferences file " + Statics.CONFIG + " deleted.");
			return true;
		} catch (Exception e) {
			logger.warn("Could not delete preferences");
			return false;
		}
	}

	public static String get(String name) {

		Locale locale = null;
		if (Config.getInstance().getLanguage().equalsIgnoreCase("en")) {
			locale = new Locale("en", "US");
		}
		if (Config.getInstance().getLanguage().equalsIgnoreCase("el")) {
			locale = new Locale("el", "GR");
		}
		if (locale == null)
			return "Unconfigured";
		return ResourceBundle.getBundle("resources.MessageBundle", locale).getString(name);
	}

}
