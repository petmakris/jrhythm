package gr.bytewise.jrhythm.gui.dialogs;

import gr.bytewise.jrhythm.api.Chord;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChordsAddEvent {

	private static final Logger logger = LoggerFactory.getLogger(ChordsAddEvent.class);

	private List<Chord> configuredChords;

	public ChordsAddEvent(List<Chord> chords) {

		if (chords == null) {
			logger.error("ChordsAddEvent cannot be initialiased with an null List of Chords");
			throw new IllegalArgumentException();
		}

		setConfiguredChords(chords);
	}

	public ChordsAddEvent(Chord chord) {

		if (chord == null)
			throw new IllegalArgumentException();

		setConfiguredChords(new ArrayList<Chord>());
		getConfiguredChords().add(chord);
	}

	public List<Chord> getConfiguredChords() {
		return configuredChords;
	}

	public void setConfiguredChords(List<Chord> configuredChords) {
		this.configuredChords = configuredChords;
	}
}
