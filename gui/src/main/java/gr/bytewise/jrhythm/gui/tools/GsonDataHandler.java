package gr.bytewise.jrhythm.gui.tools;

import gr.bytewise.jrhythm.gui.config.Statics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonDataHandler implements DataHandler {

	private static Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.create();

	public <T> T loadObject(File f, Class<T> classtype) throws FileNotFoundException {
		try {
			return gson.fromJson(new InputStreamReader(new FileInputStream(f), Statics.LOCALE), classtype);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public <T> void saveObject(File file, Object obj, Class<T> classtype) throws IOException {
		try {
			FileUtils.writeStringToFile(file, gson.toJson(obj));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public <T> T loadObject(String msg, Class<T> classtype) {
		return gson.fromJson(msg, classtype);
	}

	public <T> String saveObject(Object obj, Class<T> classtype) {
		return gson.toJson(obj);
	}

}
