package gr.bytewise.jrhythm.gui.midi;

import gr.bytewise.jrhythm.api.Chord;
import gr.bytewise.jrhythm.api.ChordPoolModel;
import gr.bytewise.jrhythm.api.Rhythm;
import gr.bytewise.jrhythm.api.Row;
import gr.bytewise.jrhythm.api.Tone;

import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.google.common.primitives.Ints;

class MidiTool {

	protected static final int T = 1024;

	protected static void createOn(Track track, int note, int velocity, long ltick) throws InvalidMidiDataException {
		track.add(MidiTool.createEvent(ShortMessage.NOTE_ON, note, velocity, ltick));
	}

	protected static void createOff(Track track, int note, int velocity, long ltick) throws InvalidMidiDataException {
		track.add(MidiTool.createEvent(ShortMessage.NOTE_OFF, note, velocity, ltick));
	}

	protected static void changeInstrument(Track track, int instrument) throws InvalidMidiDataException {
		ShortMessage instrumentChange = new ShortMessage();
		instrumentChange.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument, 0);
		track.add(new MidiEvent(instrumentChange, 0));
	}

	protected static MidiEvent createEvent(int nCommand, int nKey, int nVelocity, long lTick) throws InvalidMidiDataException {
		ShortMessage message = new ShortMessage();
		message.setMessage(nCommand, 0, nKey, nVelocity);
		return new MidiEvent(message, lTick);
	}

	// To implement
	@SuppressWarnings("unused")
	public byte[] getByteArray(int... ints) {
		if (ints.length == 0)
			return new byte[] {};

		int length = 0;
		for (int i : ints) {
			byte[] bytes = Ints.toByteArray(i);
			length += bytes.length;
		}
		return null;
	}

	protected static MidiEvent createMetaEvent(int row, int col, int rep, long lTick) throws InvalidMidiDataException {
		MetaMessage message = new MetaMessage();
		byte[] rowbytes = Ints.toByteArray(row);
		byte[] colbytes = Ints.toByteArray(col);
		byte[] repbytes = Ints.toByteArray(rep);
		int length = rowbytes.length + colbytes.length + repbytes.length;
		byte[] ms = new byte[length];

		for (int t = 0; t < rowbytes.length; t++) {
			ms[t] = rowbytes[t];
			ms[rowbytes.length + t] = colbytes[t];
			ms[2 * rowbytes.length + t] = repbytes[t];
		}
		message.setMessage(0, ms, length);
		return new MidiEvent(message, lTick);
	}

	protected static long createTrack(Track track, ChordPoolModel pool, int grow) throws InvalidMidiDataException {
		Rhythm rhythm = pool.getRhythm();
		long t = 0;
		int note = pool.get(0).get(0).getMidiNotes().get(0);

		for (int i = 0; i < 3; i++) {
			MidiTool.createOff(track, note, 64, t);
			MidiTool.createOff(track, note, 0, t + T);
			track.add(MidiTool.createMetaEvent(0, 0, 0, t));
			t += T;
		}

		for (int i = 0; i < 3; i++) {
			MidiTool.createOn(track, note, 64, t);
			MidiTool.createOff(track, note, 0, t + T);
			track.add(MidiTool.createMetaEvent(0, 0, 0, t));
			t += T;
		}
		int repeat = pool.getRepeat();
		for (int rep = 0; rep < repeat; rep++) {
			for (int x = 0; x < pool.size(); x++) {
				Row r = pool.get(x);
				for (int j = 0; j < r.getRepeat(); j++)
					t = manipulateRow(track, rep + 1, r, x, rhythm, t); // TODO nightly addition
			}
		}

		MidiTool.createOff(track, note, 64, t);
		MidiTool.createOff(track, note, 0, t + T);
		MidiTool.createMetaEvent(-1, -1, 0, t + T);
		return t + T;
	}

	static boolean doesNotChangeChord(Row r, int pos) {
		return true;
	}

	public static long manipulateRow(Track track, int rep, Row rowdata, int x, Rhythm rhythm, long time) throws InvalidMidiDataException {
		long t = time;
		int fifthCounter = 0;
		Chord lastChord = null;
		for (int y = 0; y < rowdata.size(); y++) {
			Chord chord = rowdata.get(y);
			long n = T / rhythm.getDuration()[y];

			if (chord.getIsChord()) {
				if (rhythm.getNotation()[y] == 1) {
					fifthCounter++;
				}
				boolean shouldPlayFifth = fifthCounter % 2 == 0 && chord.equals(lastChord);

				manipulateChord(track, chord, x, y, rhythm, t, n, shouldPlayFifth);
			}
			else
				manipulateMelody(track, chord, x, y, rhythm, t, n);

			lastChord = chord;
			track.add(MidiTool.createMetaEvent(x, y, rep, t));
			t += n;
		}
		return t;
	}

	public static void manipulateChord(Track track, Chord chord, int x, int y, Rhythm rhythm, long t, long n, boolean playFifth) throws InvalidMidiDataException {

		int volume = chord.getIsMute() ? 0 : 127;

		if (rhythm.getNotation()[y] == 1) {
			if (!playFifth) {
				MidiTool.createOn(track, chord.getMidiNotes().get(0) - 12, volume, t);
				MidiTool.createOff(track, chord.getMidiNotes().get(0) - 12, 0, t + n);
			}
			else {
				MidiTool.createOn(track, chord.getMidiNotes().get(2) - 12, volume, t);
				MidiTool.createOff(track, chord.getMidiNotes().get(2) - 12, 0, t + n);
			}
		}
		else {
			for (int k : chord.getMidiNotes()) {
				MidiTool.createOn(track, k, volume, t);
				MidiTool.createOff(track, k, 0, t + n);
			}
		}
	}

	public static void manipulateMelody(Track track, Chord chord, int x, int y, Rhythm rhythm, long t, long n) throws InvalidMidiDataException {
		long td = t;
		long div = n / chord.getTones().size();

		List<Tone> tn = chord.getTones();
		//List<Integer> d = chord.getDurations();

		for (Tone tone : tn) {
			MidiTool.createOn(track, 45 + tone.getOrder(), 127, td);
			MidiTool.createOff(track, 45 + tone.getOrder(), 0, td + div);
			td += div;
		}
	}
}
