package gr.bytewise.jrhythm.gui.dialogs;

import static gr.bytewise.jrhythm.guitk.UIConstraints.B;
import static gr.bytewise.jrhythm.guitk.UIConstraints.CR;
import static gr.bytewise.jrhythm.guitk.UIConstraints.F;
import static gr.bytewise.jrhythm.guitk.UIConstraints.H;
import static gr.bytewise.jrhythm.guitk.UIConstraints.N;
import gr.bytewise.jrhythm.api.Chord;
import gr.bytewise.jrhythm.api.ChordType;
import gr.bytewise.jrhythm.api.Event;
import gr.bytewise.jrhythm.api.Octave;
import gr.bytewise.jrhythm.api.Scale;
import gr.bytewise.jrhythm.api.Tone;
import gr.bytewise.jrhythm.gui.config.Resource;
import gr.bytewise.jrhythm.guitk.BaseDialog;
import gr.bytewise.jrhythm.guitk.GInventoryList;
import gr.bytewise.jrhythm.guitk.GPanel;
import gr.bytewise.jrhythm.guitk.UI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChordEditorDialog extends BaseDialog implements ActionListener {

	protected static Logger logger = LoggerFactory.getLogger(ChordEditorDialog.class);

	public ChordEditorDialog(JFrame parent) {
		super(parent, "Rhythm Editor", true);
		finalizeConstruction();
	}

	class MyInputVerifier extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			return false;
		};
	}

	@Override
	protected Dimension configureSize() {
		return new Dimension(500, 420);
	}

	@Override
	protected Component configureGui() {
		GPanel content = new GPanel();
		content.add(0, 0, 1, 1, 1, F, CR, B, configureTabbedPanel());
		content.add(0, 1, 1, 1, 1, 1, CR, N, configureButtons());
		return content.getPanel();
	}

	JTabbedPane tabbedPane = new JTabbedPane();

	private Component configureTabbedPanel() {
		tabbedPane.addTab("Single", configureSingleChordPane());
		tabbedPane.addTab("Multiple", configureMultipleChordsPane());
		tabbedPane.addTab("Advanced", configureAdvancedSelectionPane());
		tabbedPane.addTab("Melody", configureMelodySelectionPane());
		return tabbedPane;
	}

	GInventoryList<Tone> toneList;
	GInventoryList<ChordType> chordList;
	GInventoryList<Octave> octaveList;

	public JPanel configureSingleChordPane() {
		GPanel panel = new GPanel();
		toneList = new GInventoryList<>(Tone.getListOfTones());
		chordList = new GInventoryList<>(ChordType.values());
		octaveList = new GInventoryList<>(Octave.values());

		panel.add(0, 0, 1, 1, 1, 1, CR, B, new Insets(5, 5, 5, 5), toneList);
		panel.add(1, 0, 1, 1, 1, 1, CR, B, new Insets(5, 5, 5, 5), chordList);
		panel.add(2, 0, 1, 1, 1, 1, CR, B, new Insets(5, 5, 5, 5), octaveList);
		return panel.getPanel();
	}

	GInventoryList<Scale> scaleList;
	GInventoryList<Tone> toneList2;
	JLabel multipleChordsInfo;

	ListSelectionListener listSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			if (!arg0.getValueIsAdjusting()) {
				multipleChordsInfo.setText(getChordsDescription());
			}
		}
	};

	protected String getChordsDescription() {

		int idx = scaleList.getSelectedIndex();
		Tone tone = (Tone) toneList2.getSelectedValue();
		List<Chord> chords = Scale.getChords(idx, tone);

		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<h2>Chords:</h2>");
		for (Chord c : chords)
			sb.append("<p><b>" + c + "</b></p>");
		sb.append("</html>");
		return sb.toString();
	}

	public JPanel configureMultipleChordsPane() {
		GPanel panel = new GPanel();

		scaleList = new GInventoryList<>(Scale.values());
		toneList2 = new GInventoryList<>(Tone.getListOfTones());

		scaleList.addListSelectionListener(listSelectionListener);
		toneList2.addListSelectionListener(listSelectionListener);

		multipleChordsInfo = new JLabel(getChordsDescription());
		panel.add(0, 0, 1, 1, 1, 1, CR, B, new Insets(5, 5, 5, 5), scaleList);
		panel.add(1, 0, 1, 1, 1, 1, CR, B, new Insets(5, 5, 5, 5), toneList2);
		panel.add(2, 0, 1, 1, 2, 1, CR, B, new Insets(5, 5, 5, 5), multipleChordsInfo);
		return panel.getPanel();
	}

	JCheckBox checkMuteChord = new JCheckBox("Muted chord");

	public JPanel configureAdvancedSelectionPane() {
		GPanel panel = new GPanel();
		checkMuteChord.setSelected(false);
		panel.add(0, 0, 1, 1, 1, 1, CR, B, checkMuteChord);
		return panel.getPanel();
	}

	JTextField nameField = new JTextField();
	JTextArea melodyArea = new JTextArea();

	public JPanel configureMelodySelectionPane() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, H, new Insets(5, 5, 5, 5), new JLabel("Melody name"));
		panel.add(0, 1, 1, 1, 1, 1, CR, H, new Insets(5, 5, 5, 5), nameField);
		panel.add(0, 2, 1, 1, 1, 1, CR, H, new Insets(5, 5, 5, 5), new JLabel("Melody configuration"));
		JScrollPane scrollPane = new JScrollPane(melodyArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(0, 3, 1, 1, 1, F, CR, B, new Insets(5, 5, 5, 5), scrollPane);

		melodyArea.setLineWrap(true);

		return panel.getPanel();
	}

	private static final String ACTION_SUBMIT = "SUBMIT";
	private static final String ACTION_HIDE = "HIDE";

	private Component configureButtons() {
		GPanel panel = new GPanel();
		JButton btnSubmit = UI.newButton("Add", ACTION_SUBMIT, null, "", this);
		JButton btnHide = UI.newButton("Hide", ACTION_HIDE, null, "", this);
		panel.add(0, 0, 1, 1, 1, 1, CR, B, btnHide);
		panel.add(1, 0, 1, 1, 1, 1, CR, B, btnSubmit);
		//	f.setInputVerifier(new MyInputVerifier());
		return panel.getPanel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(ACTION_SUBMIT)) {
			switch (tabbedPane.getSelectedIndex()) {
			case 0:
				addSingleChord();
				break;
			case 1:
				addMultipleChords();
				break;
			case 2:
				addAdvancedChord();
				break;
			case 3:
				addMelodyChord();
				break;
			default:
				showMessage(Resource.MIDI, "Unexpected Error!?");
				break;
			}
		}
		if (e.getActionCommand().equalsIgnoreCase(ACTION_HIDE))
			hide();
	}

	protected void addSingleChord() {
		Tone tone = (Tone) toneList.getSelectedValue();
		ChordType chord = (ChordType) chordList.getSelectedValue();
		Octave octave = (Octave) octaveList.getSelectedValue();
		Event.post(new ChordsAddEvent(new Chord(tone, chord, octave)));
	}

	protected void addMultipleChords() {
		int idx = scaleList.getSelectedIndex();
		Tone tone = (Tone) toneList2.getSelectedValue();
		Event.post(new ChordsAddEvent(Scale.getChords(idx, tone)));

	}

	private void addAdvancedChord() {
		Event.post(new ChordsAddEvent(new Chord(true)));
	}

	/*
	 * TODO: add validation and regular expressions
	 *	logger.info(s + " " + Pattern.matches("([1234])*([A-G]+)([#b])*", s));
	 *	
	 */
	private static final String PATTERN = "([<>]?)([1234]?)([A-G]{1})([#b]?)";

	/*
	private void addMelodyChord() {
		List<Tone> tonesList = new ArrayList<Tone>();
		List<Integer> durationList = new ArrayList<Integer>();
		List<String> notes = Arrays.asList(melodyArea.getText().trim().split(" "));
		for (String s : notes) {
			if (Pattern.matches(PATTERN, s)) {
				int tone = 0;
				int alter = 0;
				int dur = 1;

				switch (s.charAt(0)) {
				case '1':
					dur = 1;
					break;
				case '2':
					dur = 2;
					break;
				case '3':
					dur = 3;
					break;
				case '4':
					dur = 4;
					break;
				}

				tone = s.contains("A") ? 0 : tone;
				tone = s.contains("B") ? 2 : tone;
				tone = s.contains("C") ? 3 : tone;
				tone = s.contains("D") ? 5 : tone;
				tone = s.contains("E") ? 7 : tone;
				tone = s.contains("F") ? 8 : tone;
				tone = s.contains("G") ? 10 : tone;

				alter = s.contains("#") ? 1 : 0;
				alter = s.contains("b") ? -1 : 0;

				tonesList.add(new Tone(tone, alter));
				durationList.add(dur);

			} else
				logger.info(s + " does not match");
		}
		Chord c = new Chord(nameField.getText(), tonesList, durationList);
		c.setIsChord(false);
		Event.post(new ChordsAddEvent(c));
	}*/
	private void addMelodyChord() {
		List<Tone> tonesList = new ArrayList<Tone>();
		List<Integer> durationList = new ArrayList<Integer>();
		List<String> notes = Arrays.asList(melodyArea.getText().trim().split(" "));

		for (String s : notes) {
			if (Pattern.matches(PATTERN, s)) {
				Matcher m = Pattern.compile(PATTERN).matcher(s);
				if (m.find()) {
					int oct = m.group(1).contains("<") ? -12 : m.group(1).contains(">") ? 12 : 0;
					int dur = Integer.parseInt(m.group(2));
					int note = 0;

					if (m.group(3).contains("A"))
						note = 0;
					if (m.group(3).contains("B"))
						note = 2;
					if (m.group(3).contains("C"))
						note = 3;
					if (m.group(3).contains("D"))
						note = 5;
					if (m.group(3).contains("E"))
						note = 7;
					if (m.group(3).contains("F"))
						note = 8;
					if (m.group(3).contains("G"))
						note = 10;

					int alter = m.group(4).contains("b") ? -1 : m.group(4).contains("#") ? 1 : 0;
					Tone tone = new Tone(note, alter);
					tone.setTone(tone.getTone() + oct);
					tonesList.add(tone);
					durationList.add(dur);
				}
			} else
				logger.info(s + " does not match");
		}/*
			Chord c = new Chord(nameField.getText(), tonesList, durationList);
			c.setIsChord(false);
			Event.post(new ChordsAddEvent(c));*/
	}
}
