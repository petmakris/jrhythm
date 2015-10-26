package gr.bytewise.jrhythm.gui.midi;

import gr.bytewise.jrhythm.api.ChordPoolModel;
import gr.bytewise.jrhythm.api.Event;
import gr.bytewise.jrhythm.gui.events.MidiPlayBackProgressEvent;
import gr.bytewise.jrhythm.gui.events.PlaybackEvent;
import gr.bytewise.jrhythm.gui.events.ProgressEvent;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;

public class MidiService implements MetaEventListener {

	protected static final Logger logger = LoggerFactory.getLogger(MidiService.class);

	protected static final int END_OF_TRACK_MESSAGE = 47;

	private MidiService() {
	}

	private static class SingletonHolder {
		public final static MidiService INSTANCE = new MidiService();
	}

	public static MidiService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public Sequencer sequencer = null;
	protected MidiDevice midiDevice = null;
	protected Synthesizer synthesizer = null;
	protected Soundbank soundbank = null;

	private void openMidiSystem(int deviceIndex) throws MidiUnavailableException {
		midiDevice = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[deviceIndex]);
		Event.post(new ProgressEvent("Opening Midi System..."));
		midiDevice.open();
	}

	public List<Instrument> getInstruments(int deviceIndex) {
		List<Instrument> inst = new ArrayList<Instrument>();
		try {
			Event.post(new ProgressEvent("Requesting Instrument List..."));
			openMidiSystem(deviceIndex);
			openSequencer();
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
		} catch (MidiUnavailableException e) {
			logger.error("Midi Device Unavailable");
			return inst;
		}
		for (Instrument i : synthesizer.getAvailableInstruments())
			inst.add(i);
		stop();
		return inst;
	}

	private void closeMidiDevice() {
		if (midiDevice != null && midiDevice.isOpen()) {
			midiDevice.close();
		}
		midiDevice = null;
	}

	private void openSequencer() throws MidiUnavailableException {
		sequencer = MidiSystem.getSequencer(false);
		sequencer.open();
	}

	private void closeSequencer() {
		if (sequencer != null && sequencer.isOpen()) {
			sequencer.close();
			sequencer.setMicrosecondPosition(0);
		}
		sequencer = null;
	}

	private void stopSequencer() {
		if (sequencer != null && sequencer.isRunning())
			sequencer.stop();
	}

	private int firstRow = 0;

	public long play(int deviceIndex, int instrumentIndex, final ChordPoolModel pool, int firstRow, int grow) throws InvalidMidiDataException, MidiUnavailableException {
		openMidiSystem(deviceIndex);
		openSequencer();
		this.firstRow = firstRow;
		Event.post(new ProgressEvent(" @ " + midiDevice.getDeviceInfo().getName()));
		Sequence sequence = new Sequence(Sequence.PPQ, MidiTool.T);
		Track track = sequence.createTrack();
		MidiTool.changeInstrument(track, instrumentIndex);
		long time = MidiTool.createTrack(track, pool, grow);
		sequencer.addMetaEventListener(this);
		sequencer.addMetaEventListener(metaEventListener);
		sequencer.getTransmitter().setReceiver(midiDevice.getReceiver());
		sequencer.setSequence(sequence);
		sequencer.setTempoInBPM(pool.getTempo());
		Event.post(new ProgressEvent("Starting Sequencer @ " + pool.getTempo()));
		Event.post(new PlaybackEvent(true));
		sequencer.start();
		return time;
	}

	MetaEventListener metaEventListener = new MetaEventListener() {

		@Override
		public void meta(MetaMessage meta) {
			try {
				int length = meta.getLength() / 3 - 1;
				byte[] msg = meta.getData();

				byte[] rowbytes = new byte[length];
				byte[] colbytes = new byte[length];
				byte[] repbytes = new byte[length];

				for (int i = 0; i < length; i++) {
					rowbytes[i] = msg[i];
					colbytes[i] = msg[length + i];
					repbytes[i] = msg[2 * length + i];
				}
				int row = Ints.fromByteArray(rowbytes);
				int col = Ints.fromByteArray(colbytes);
				int rep = Ints.fromByteArray(repbytes);

				if (col == -1 && row == -1)
					Event.post(new PlaybackEvent(false));
				else
					Event.post(new MidiPlayBackProgressEvent(firstRow + row, col, rep));

			} catch (Exception e) {
				logger.error("metaEventListener->meta() " + e.getMessage());
			}
		}
	};

	@Override
	public void meta(MetaMessage event) {
		if (event.getType() == END_OF_TRACK_MESSAGE) {
			Event.post(new ProgressEvent("END_OF_TRACK_MESSAGE: closing everything."));
			stop();
		}
	}

	public void stop() {
		Event.post(new ProgressEvent("Shutting Midi Devices Down"));
		stopSequencer();
		closeSequencer();
		closeMidiDevice();
		Event.post(new PlaybackEvent(false));
		firstRow = 0;

	}

	public int getTickPosition() {
		if (sequencer == null)
			return -1;
		return (int) sequencer.getTickPosition();
	}

	public int getTickLength() {
		if (sequencer == null)
			return -1;
		return (int) sequencer.getTickLength();
	}

}
