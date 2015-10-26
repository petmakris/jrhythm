package gr.bytewise.jrhythm.gui.tools;

import gr.bytewise.jrhythm.gui.config.Statics;

import java.nio.charset.Charset;

import javax.xml.bind.DatatypeConverter;

public class Codecs {
	public static String encode(String message) {
		return DatatypeConverter.printBase64Binary(message.getBytes(Charset.forName(Statics.LOCALE)));
	}

	public static String decode(String message) {
		return new String(DatatypeConverter.parseBase64Binary(message), Charset.forName(Statics.LOCALE));
	}
}
