package gr.bytewise.jrhythm.gui.dialogs;

import static gr.bytewise.jrhythm.guitk.UIConstraints.B;
import static gr.bytewise.jrhythm.guitk.UIConstraints.BE;
import static gr.bytewise.jrhythm.guitk.UIConstraints.CR;
import static gr.bytewise.jrhythm.guitk.UIConstraints.H;
import static gr.bytewise.jrhythm.guitk.UIConstraints.LE;
import static gr.bytewise.jrhythm.guitk.UIConstraints.N;
import gr.bytewise.jrhythm.api.Event;
import gr.bytewise.jrhythm.gui.config.Config;
import gr.bytewise.jrhythm.gui.config.Resource;
import gr.bytewise.jrhythm.gui.events.GenericRestartEvent;
import gr.bytewise.jrhythm.gui.midi.MidiService;
import gr.bytewise.jrhythm.guitk.BaseDialog;
import gr.bytewise.jrhythm.guitk.GPanel;
import gr.bytewise.jrhythm.guitk.LookAndFeelManager;
import gr.bytewise.jrhythm.guitk.UI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpertSettingsDialog extends BaseDialog implements ActionListener {

	protected static final Logger logger = LoggerFactory.getLogger(ExpertSettingsDialog.class);

	protected MidiService midi = MidiService.getInstance();
	protected static Config config = Config.getInstance();

	public ExpertSettingsDialog(JFrame parent) {
		super(parent, "Expert Configuration", true);
		finalizeConstruction();
	}

	enum Action {
		RESET_SETTINGS, HIDE
	}

	private JButton newButton(String title, Action action, String name, String tip) {
		return UI.newButton(title, action.toString(), name, tip, this);
	}

	@Override
	protected Dimension configureSize() {
		return new Dimension(400, 300);
	}

	@Override
	protected Component configureGui() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, B, new Insets(5, 5, 5, 5), configureTop().getPanel());
		panel.add(0, 1, 1, 1, 1, 1, BE, N, new Insets(5, 5, 5, 5), configureButtons().getPanel());
		return panel.getPanel();
	}

	public GPanel configureTop() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, H, configureMidiSystem().getPanel());
		panel.add(0, 1, 1, 1, 1, 1, CR, H, configureLookAndFeel().getPanel());
		panel.add(0, 2, 1, 1, 1, 1, CR, H, configureLanguage().getPanel());
		panel.add(0, 3, 1, 1, 1, 1, CR, H, configureOptions().getPanel());
		return panel;
	}

	JButton btnReset;

	public GPanel configureOptions() {
		GPanel panel = new GPanel();
		btnReset = newButton("Reset Settings", Action.RESET_SETTINGS, Resource.REVERT, "Clear Settings");
		panel.add(0, 0, 1, 1, 1, 1, CR, H, btnReset);
		return panel;
	}

	@SuppressWarnings("rawtypes")
	final JComboBox comboMidiSystem = new JComboBox();

	@SuppressWarnings("rawtypes")
	final JComboBox comboMidiInstrument = new JComboBox();

	private final ItemListener itemMidiSystemListener = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {

				Info info = (Info) comboMidiSystem.getItemAt(comboMidiSystem.getSelectedIndex());
				config.getMidiConfig().setDeviceName(info.getName());
				config.getMidiConfig().setDeviceIndex(comboMidiSystem.getSelectedIndex());
			}
		}
	};

	private final ItemListener itemMidiInstrumentListener = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				config.getMidiConfig().setInstrumentIndex(comboMidiInstrument.getSelectedIndex());
			}
		}
	};

	public GPanel configureMidiSystem() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, H, comboMidiSystem);
		panel.add(0, 1, 1, 1, 1, 1, CR, H, comboMidiInstrument);
		return panel;
	}

	@SuppressWarnings("unchecked")
	public void updateMidiSystemComboBox() {
		// Work with Midi
		for (ItemListener i : comboMidiSystem.getItemListeners())
			comboMidiSystem.removeItemListener(i);

		comboMidiSystem.removeAllItems();
		Info[] midiInfos = MidiSystem.getMidiDeviceInfo();
		for (Info element : midiInfos)
			comboMidiSystem.addItem(element);
		comboMidiSystem.setSelectedIndex(config.getMidiConfig().getDeviceIndex());
		comboMidiSystem.addItemListener(itemMidiSystemListener);

		// Work with Instruemnts
		for (ItemListener i : comboMidiInstrument.getItemListeners())
			comboMidiInstrument.removeItemListener(i);

		comboMidiInstrument.removeAllItems();
		for (Instrument ins : midi.getInstruments(config.getMidiConfig().getDeviceIndex()))
			comboMidiInstrument.addItem(ins);
		comboMidiInstrument.setSelectedIndex(config.getMidiConfig().getInstrumentIndex());
		comboMidiInstrument.addItemListener(itemMidiInstrumentListener);
	}

	JButton btnHide;

	private GPanel configureButtons() {
		GPanel panel = new GPanel();
		btnHide = newButton("Hide", Action.HIDE, Resource.EXIT, "Close this form");
		panel.add(0, 0, 1, 1, 1, 1, LE, N, btnHide);
		return panel;
	}

	@Override
	public void show() {
		updateMidiSystemComboBox();
		dialog.pack();
		super.show();
	}

	@SuppressWarnings("rawtypes")
	private JComboBox comboLookAndFeel;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private GPanel configureLookAndFeel() {
		GPanel panel = new GPanel();
		comboLookAndFeel = new JComboBox();
		for (String element : LookAndFeelManager.getLookndFeelNameList())
			comboLookAndFeel.addItem(element);
		comboLookAndFeel.setSelectedIndex(config.getLookAndFeel());
		panel.add(0, 0, 1, 1, 1, 1, CR, H, comboLookAndFeel);
		comboLookAndFeel.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					config.setLookAndFeel(comboLookAndFeel.getSelectedIndex());
					//configure here the static look and feel
					LookAndFeelManager.setLookAndFeel(comboLookAndFeel.getSelectedIndex());
					hide();
					Event.post(new GenericRestartEvent());
				}
			}
		});
		return panel;
	}

	@SuppressWarnings("rawtypes")
	private JComboBox comboLanguage;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private GPanel configureLanguage() {
		GPanel panel = new GPanel();
		comboLanguage = new JComboBox();
		comboLanguage.addItem("English");
		comboLanguage.addItem("Greek");
		comboLanguage.setSelectedItem(config.getLanguage());
		panel.add(0, 0, 1, 1, 1, 1, CR, H, comboLanguage);
		comboLanguage.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					config.setLanguage((String) comboLanguage.getSelectedItem());
					hide();
					Event.post(new GenericRestartEvent());
				}
			}
		});
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (Action.valueOf(e.getActionCommand())) {
		case HIDE:
			hide();
			break;
		case RESET_SETTINGS:
			if (UI.showConfirmDialog(dialog, "Application will be restarted, all data will be lost!", Resource.MIDI)) {
				if (Config.clearPreferences()) {
					hide();
					config.reload(true);
					Event.post(new GenericRestartEvent());
				} else
					UI.showMessage(dialog, "Could not clear preferences!", Resource.MIDI);
			}
			break;
		default:
			logger.error("Unhandled event " + e.getSource());
			break;
		}
	}

}
