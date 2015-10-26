package gr.bytewise.jrhythm.gui.tools;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class RGFileFilter extends FileFilter {

	public static final String EXTENSION = "abc";

	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		return getExtension(f) != null && getExtension(f).equalsIgnoreCase(EXTENSION);
	}

	//The description of this filter
	@Override
	public String getDescription() {
		return "RhythmGuitar files (.abc)";
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static boolean hasIncorectExtension(File file) {
		return RGFileFilter.getExtension(file) == null || !RGFileFilter.getExtension(file).equalsIgnoreCase(EXTENSION);
	}
}
