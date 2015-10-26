package gr.bytewise.jrhythm.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;

@XmlRootElement(name = "Chord")
@XmlAccessorType(XmlAccessType.FIELD)
public class Chord {

	@XmlElement(name = "Tone")
	private Tone tone;

	@XmlAttribute
	private ChordType chordType;

	@XmlAttribute
	private Octave octave;

	@XmlAttribute
	private Boolean isChord;

	@XmlAttribute
	private Boolean isMute;

	@XmlAttribute
	private String extraConfiguration;

	@XmlElement(name = "CustomName")
	private String customName;

	@XmlElementWrapper(name = "Tones")
	@XmlElement(name = "Tone")
	private List<Tone> tones;

	@XmlElementWrapper(name = "Durations")
	@XmlElement(name = "Integer")
	private List<Integer> durations;

	public Chord(boolean muted) {
		setTone(new Tone());
		setChordType(ChordType.MAJOR);
		setOctave(Octave.CUR);
		setIsChord(true);
		setIsMute(muted);
		setTones(null);
		setDurations(null);

	}

	public Chord() {
		setTone(new Tone());
		setChordType(ChordType.MAJOR);
		setOctave(Octave.CUR);
		setIsChord(true);
		setIsMute(false);
		setTones(null);
		setDurations(null);
	}

	public Chord(Tone tone, ChordType chordType, Octave octave) {
		setTone(tone);
		setChordType(chordType);
		setOctave(octave);
		setIsChord(true);
		setIsMute(false);
		setTones(null);
		setDurations(null);
	}

	public Chord(String customName, List<Tone> tones, List<Integer> durations) {
		setCustomName(customName);
		setTone(null);
		setChordType(null);
		setOctave(null);
		setIsChord(false);
		setIsMute(false);
		setTones(tones);
		setDurations(durations);
	}

	public Tone getTone() {
		return tone;
	}

	public void setTone(Tone tone) {
		this.tone = tone;
	}

	public ChordType getChordType() {
		return chordType;
	}

	public void setChordType(ChordType chordType) {
		this.chordType = chordType;
	}

	public Octave getOctave() {
		return octave;
	}

	public void setOctave(Octave octave) {
		this.octave = octave;
	}

	public List<Integer> getMidiNotes() {
		List<Integer> noteList = new ArrayList<Integer>();
		int offset = 0;

		for (int i : chordType.getKeys())
			noteList.add(i);

		offset = tone.getOrder() + 45 + 12; //TODO: base?

		switch (octave) {
		case CUR:
			break;
		case NEX:
			offset += 12;
			break;
		case PRE:
			offset -= 12;
			break;
		}

		for (int i = 0; i < noteList.size(); i++)
			noteList.set(i, noteList.get(i) + offset);

		return noteList;
	}

	@Override
	public String toString() {
		if (getIsMute())
			return "*";
		if (octave == null || tone == null || chordType == null) {
			return getCustomName();
		} else
			return octave.toString() + tone.toString() + chordType.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, true);
	}

	public void setIsChord(Boolean isChord) {
		this.isChord = isChord;
	}

	public Boolean getIsChord() {
		return isChord;
	}

	public String getExtraConfiguration() {
		return extraConfiguration;
	}

	public void setExtraConfiguration(String extraConfiguration) {
		this.extraConfiguration = extraConfiguration;
	}

	public List<Tone> getTones() {
		return tones;
	}

	public void setTones(List<Tone> tones) {
		this.tones = tones;
	}

	public List<Integer> getDurations() {
		return durations;
	}

	public void setDurations(List<Integer> durations) {
		this.durations = durations;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public Boolean getIsMute() {
		return isMute;
	}

	public void setIsMute(Boolean isMute) {
		this.isMute = isMute;
	}

	public static ChordGravity distanceOf(Chord bs, Chord ch) {
		try {
			int b = bs.getTone().getTone();

			ChordType bc = bs.getChordType();
			int c = ch.getTone().getTone();
			ChordType cc = ch.getChordType();

			switch (b - c) {
			case 0:
				return (bc == ChordType.MAJOR && cc == ChordType.MINOR) || (bc == ChordType.MINOR && cc == ChordType.MAJOR) ? ChordGravity.SYN_TONE : ChordGravity.TONE;
			}

			return ChordGravity.NONE;
		} catch (Exception e) {
			return ChordGravity.NONE;
		}
	}

}
