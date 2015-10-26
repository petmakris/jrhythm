package gr.bytewise.jrhythm.api;

import java.util.ArrayList;
import java.util.List;

public enum Scale {

	HITZAZ("Hitzaz"),
	MAJOR("Major"),
	MINOR("Minor"),
	OUSAK("Ousak");

	Scale(String name) {
		setName(name);
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static List<Chord> getChords(int ordinal, Tone baseTone) {
		List<Chord> list = new ArrayList<Chord>();
		switch (ordinal) {
		case 0:
			list.add(new Chord(new Tone(baseTone, 00), ChordType.MAJOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 01), ChordType.MAJOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 05), ChordType.MINOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 10), ChordType.MINOR, Octave.CUR));
			break;
		case 1:
			list.add(new Chord(new Tone(baseTone, 00), ChordType.MAJOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 05), ChordType.MAJOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 07), ChordType.MAJOR, Octave.CUR));
			break;
		case 2:
			list.add(new Chord(new Tone(baseTone, 00), ChordType.MINOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 05), ChordType.MINOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 07), ChordType.MAJOR, Octave.CUR));
			break;
		case 3:
			list.add(new Chord(new Tone(baseTone, 00), ChordType.MINOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 05), ChordType.MINOR, Octave.CUR));
			list.add(new Chord(new Tone(baseTone, 10), ChordType.MINOR, Octave.CUR));
			break;
		}
		return list;

	}

}
