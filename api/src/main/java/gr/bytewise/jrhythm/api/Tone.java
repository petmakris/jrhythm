package gr.bytewise.jrhythm.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;

@XmlRootElement(name = "Tone")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tone {
	public final static int MAX = 12;

	@XmlAttribute(name = "Base")
	private int base;

	@XmlAttribute(name = "Alter")
	private int alter;

	public Tone() {
		setOrder(0);
		setTone(0);
	}

	public Tone(int order) {
		this();
		setOrder(order);
	}

	public Tone(int tone, int alter) {
		setTone(tone);
		setAlter(alter);
	}

	public Tone(Tone t) {
		setTone(t.getTone());
		setAlter(t.getAlter());
	}

	public Tone(Tone t, int moveUp) {
		setTone(t.getTone());
		setAlter(t.getAlter());
		moveUp(moveUp);
	}

	public int getTone() {
		return base;
	}

	public void setTone(int tone) {
		this.base = tone;
	}

	public int getAlter() {
		return alter;
	}

	public void setAlter(int alter) {
		this.alter = alter;
	}

	public void setToneAlter(int tone, int alter) {
		setTone(tone);
		setAlter(alter);
	}

	/**
	 * Document
	 * 
	 * 
	 */

	public void setOrder(int order) {
		switch (order) {
		case 0:
			setToneAlter(0, 0);
			break;
		case 1:
			setToneAlter(0, 1);
			break;
		case 2:
			setToneAlter(2, 0);
			break;
		case 3:
			setToneAlter(3, 0);
			break;
		case 4:
			setToneAlter(3, 1);
			break;
		case 5:
			setToneAlter(5, 0);
			break;
		case 6:
			setToneAlter(5, 1);
			break;
		case 7:
			setToneAlter(7, 0);
			break;
		case 8:
			setToneAlter(8, 0);
			break;
		case 9:
			setToneAlter(8, 1);
			break;
		case 10:
			setToneAlter(10, 0);
			break;
		case 11:
			setToneAlter(10, 1);
			break;
		}
	}

	public int getOrder() {
		return getTone() + getAlter();
	}

	private int getNext() {

		if (getOrder() == 11)
			return 0;
		else
			return getOrder() + 1;
	}

	private int getPrevious() {

		if (getOrder() == 0)
			return 11;
		else
			return getOrder() - 1;
	}

	public void move(boolean up) {
		if (up)
			moveUp();
		else
			moveDown();
	}

	public void moveUp() {
		setOrder(getNext());
	}

	public void moveDown() {
		setOrder(getPrevious());
	}

	public void moveUp(int idx) {
		for (int i = 0; i < idx; i++)
			moveUp();
	}

	public void moveDown(int idx) {
		for (int i = 0; i < idx; i++)
			moveDown();
	}

	@Override
	public String toString() {
		String s = "";
		switch (getTone()) {
		case 0:
			s = "A";
			break;
		case 2:
			s = "B";
			break;
		case 3:
			s = "C";
			break;
		case 5:
			s = "D";
			break;
		case 7:
			s = "E";
			break;
		case 8:
			s = "F";
			break;
		case 10:
			s = "G";
			break;
		}

		if (getAlter() == -1)
			s += "b";
		else if (getAlter() == 1)
			s += "#";
		return s;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public static Tone[] getListOfTones() {
		Tone[] tones = new Tone[Tone.MAX];
		for (int i = 0; i < Tone.MAX; i++)
			tones[i] = new Tone(i);
		return tones;
	}

}
