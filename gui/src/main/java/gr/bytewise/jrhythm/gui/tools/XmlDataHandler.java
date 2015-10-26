package gr.bytewise.jrhythm.gui.tools;

import gr.bytewise.jrhythm.gui.config.Statics;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlDataHandler implements DataHandler {

	public <T> T loadObject(File f, Class<T> classtype) throws FileNotFoundException {
		try {
			JAXBContext context = JAXBContext.newInstance(classtype);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return classtype.cast(unmarshaller.unmarshal(new InputStreamReader(new FileInputStream(f), Statics.LOCALE)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public <T> void saveObject(File file, Object obj, Class<T> classtype) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(classtype);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, Statics.LOCALE);
			marshaller.marshal(obj, new FileWriter(file));
			marshaller.marshal(obj, new OutputStreamWriter(new FileOutputStream(file), Statics.LOCALE));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public <T> T loadObject(String msg, Class<T> classtype) {
		try {
			return classtype.cast(JAXBContext.newInstance(classtype).createUnmarshaller().unmarshal(new ByteArrayInputStream(msg.getBytes(Charset.forName(Statics.LOCALE)))));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public <T> String saveObject(Object obj, Class<T> classtype) {
		try {
			Marshaller marshaller = JAXBContext.newInstance(classtype).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter(1024);
			marshaller.marshal(obj, sw);
			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

}
