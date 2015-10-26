package gr.bytewise.jrhythm.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;

@XmlRootElement(name = "Rhythm")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rhythm {

	@XmlAttribute(name = "Name")
	private String name;

	@XmlElement(name = "Duration")
	private int[] duration;

	@XmlElement(name = "Notation")
	private int[] notation;

	protected Rhythm() {}

	public Rhythm(String name, int[] duration, int[] notation) {
		setName(name);
		setDuration(duration);
		setNotation(notation);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getNotation() {
		return notation;
	}

	public void setNotation(int[] n) {
		this.notation = n;
	}

	public int[] getDuration() {
		return duration;
	}

	public void setDuration(int[] d) {
		this.duration = d;
	}

	public int getLength() {
		return duration.length;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, true);
	}
}
