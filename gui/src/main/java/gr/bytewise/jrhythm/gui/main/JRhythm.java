package gr.bytewise.jrhythm.gui.main;

import static gr.bytewise.jrhythm.guitk.UIConstraints.B;
import static gr.bytewise.jrhythm.guitk.UIConstraints.CR;
import static gr.bytewise.jrhythm.guitk.UIConstraints.F;
import static gr.bytewise.jrhythm.guitk.UIConstraints.H;
import static gr.bytewise.jrhythm.guitk.UIConstraints.LE;
import static gr.bytewise.jrhythm.guitk.UIConstraints.LS;
import static gr.bytewise.jrhythm.guitk.UIConstraints.N;
import eu.hansolo.custom.SteelCheckBox;
import eu.hansolo.steelseries.tools.ColorDef;
import gr.bytewise.jrhythm.api.Chord;
import gr.bytewise.jrhythm.api.ChordListModel;
import gr.bytewise.jrhythm.api.ChordPoolModel;
import gr.bytewise.jrhythm.api.Event;
import gr.bytewise.jrhythm.api.Rhythm;
import gr.bytewise.jrhythm.api.RhythmComboModel;
import gr.bytewise.jrhythm.api.Row;
import gr.bytewise.jrhythm.api.enums.OSType;
import gr.bytewise.jrhythm.gui.config.Config;
import gr.bytewise.jrhythm.gui.config.Resource;
import gr.bytewise.jrhythm.gui.config.Statics;
import gr.bytewise.jrhythm.gui.dialogs.ChordEditorDialog;
import gr.bytewise.jrhythm.gui.dialogs.ChordsAddEvent;
import gr.bytewise.jrhythm.gui.dialogs.ExpertSettingsDialog;
import gr.bytewise.jrhythm.gui.dialogs.RhythmEditorDialog;
import gr.bytewise.jrhythm.gui.events.GenericRestartEvent;
import gr.bytewise.jrhythm.gui.events.MidiPlayBackProgressEvent;
import gr.bytewise.jrhythm.gui.events.PlaybackEvent;
import gr.bytewise.jrhythm.gui.midi.MidiService;
import gr.bytewise.jrhythm.gui.tools.DataHandler;
import gr.bytewise.jrhythm.gui.tools.RGFileFilter;
import gr.bytewise.jrhythm.gui.tools.XmlDataHandler;
import gr.bytewise.jrhythm.guitk.FocusPainter;
import gr.bytewise.jrhythm.guitk.GInventoryList;
import gr.bytewise.jrhythm.guitk.GPanel;
import gr.bytewise.jrhythm.guitk.GuiRunner;
import gr.bytewise.jrhythm.guitk.GuiTkApplicationFrame;
import gr.bytewise.jrhythm.guitk.IconedLabel;
import gr.bytewise.jrhythm.guitk.LookAndFeelManager;
import gr.bytewise.jrhythm.guitk.UI;
import gr.bytewise.jrhythm.guitk.UIAction;
import gr.bytewise.jrhythm.guitk.UpdateGUIEvent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;



public class JRhythm extends GuiTkApplicationFrame {

	protected static final Logger logger = LoggerFactory.getLogger(JRhythm.class);
	
	static {
		LookAndFeelManager.setLookAndFeel(Config.getInstance().getLookAndFeel());
	}

	protected ChordPoolModel model;
	protected FocusPainter focusPainter;
	protected MidiService midi = MidiService.getInstance();
	protected Config config = Config.getInstance();
	protected JComboBox<Rhythm> rhythmCombo;
	protected RhythmComboModel rhythms;
	protected JFileChooser fileChooser;
	protected RGFileFilter rhythmGuitarFileFilter;
	protected JProgressBar progressBar;
	protected ChordListModel chords;
	protected GInventoryList<Chord> chordList;
	protected SteelCheckBox checkEdit;
	protected JTable table;
	protected JLabel labelSpeed;
	protected ExpertSettingsDialog expertSettingsDialog = new ExpertSettingsDialog(frame);
	protected ChordEditorDialog chordEditorDialog = new ChordEditorDialog(frame);
	protected RhythmEditorDialog rhythmEditorDialog = new RhythmEditorDialog(frame);

	protected DataHandler dataHandler = new XmlDataHandler();

	protected IconedLabel playStatus;
	static int frameInstanceCount = 0;

	public static final String PLAY = "Play";
	public static final String STOP = "Stop";

	private JRhythm() {
		super(null, "JRhythm [" + frameInstanceCount + "]", false, true, Resource.MIDI);

		frameInstanceCount += 1;

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		model = new ChordPoolModel(null);

		rhythms = config.getRhythms();

		try {
			model.setRhythm(rhythms.get(config.getRhythmIndex()));
		} catch (Exception e) {
			model.setRhythm(rhythms.get(0));
			throw new RuntimeException(e);
		}

		rhythmCombo = new JComboBox<>(rhythms);
		rhythmCombo.setSelectedIndex(config.getRhythmIndex());
		rhythmCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					model.setRhythm((Rhythm) e.getItem());
					config.setRhythmIndex(rhythmCombo.getSelectedIndex());
				}
			}
		});
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(rhythmGuitarFileFilter = new RGFileFilter());
		progressBar = new JProgressBar();
		playStatus = new IconedLabel.Builder().add(STOP, Resource.PLAYGRAY).add(PLAY, Resource.PLAYBLUE).setDimension(24, 24).build();
		playStatus.setStatus(STOP);
		checkEdit = new SteelCheckBox();
		checkEdit.setAction(actionWorkspaceEdit);
		checkEdit.setColored(true);
		checkEdit.setSelectedColor(ColorDef.RED);
		checkEdit.setRised(true);
		labelSpeed = new JLabel();

		finalizeConstruction();
		frame.setMinimumSize(new Dimension(frame.getSize().width + 200, frame.getSize().height));
		frame.pack();
		focusPainter = new FocusPainter();
		configureGuiSizeAndLocation();
		frame.setGlassPane(focusPainter);
		frame.getGlassPane().setVisible(true);
		updateValidity(true);

		frame.addWindowListener(windowListener);
	}

	WindowListener windowListener = new WindowListener() {

		@Override
		public void windowClosing(WindowEvent arg0) {
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			if (!isInternalWindowClosingTrigger()) {
				shutdownApp();
			}
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}

	};

	public void setFocus(Rectangle p) {
		focusPainter.set(p);
		frame.repaint();
	}

	public Rectangle getCoord(int row, int col) {
		Point tableloc = UI.getComponentLocation(frame, table);
		if (tableloc == null) {
			focusPainter.clear();
			frame.repaint();
			return null;
		}
		Rectangle cell = table.getCellRect(row, col, false);
		return new Rectangle(tableloc.x + cell.x, tableloc.y + cell.y, cell.width, cell.height);
	}

	@Subscribe
	public void onDrawCell(MidiPlayBackProgressEvent evt) {
		focusPainter.set(getCoord(evt.x, evt.y));
		frame.repaint();
	}

	@Subscribe
	public void onRestartEvent(GenericRestartEvent evt) {
		Event.unregister(this);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(getFrame(), WindowEvent.WINDOW_CLOSING));
		restartApp();
	}

	@Subscribe
	public void onUpdateModels(UpdateGUIEvent update) {
		chords.update();
		model.getChordComboModel().update();
		table.updateUI();
	}

	protected void updateValidity(boolean enable) {
		toggleWorkspaceEnable(enable);
	}

	@Subscribe
	public void onPlaybackEvent(PlaybackEvent evt) {
		updateValidity(!evt.isPlaying());

		if (evt.isPlaying()) {
			model.setEditable(false);
			checkEdit.setSelected(false);
		}

		if (!evt.isPlaying()) {
			progressBar.setValue(0);
			playStatus.setStatus(STOP);
			focusPainter.clear();
			frame.repaint();
			labelSpeed.setText("");
		}

	}

	/**
	 * TODO: find a good model for the advanced playback!
	 * 
	 * 
	 * 
	 * @param evt
	 */

	// float i = (float) midi.getTickPosition() / (float) midi.getTickLength();
	// if (MidiService.getInstance().sequencer != null)
	// MidiService.getInstance().sequencer.setTempoFactor(1 + i);
	// labelSpeed.setText("<html><b>%</b>" + String.format("%4.2f", 100 * (1 +
	// i)) + "</html>");

	@Subscribe
	public void onMidiProgress(MidiPlayBackProgressEvent evt) {
		progressBar.setValue(midi.getTickPosition());
	}

	// Chords have to be configured and changed to a set

	@Subscribe
	public void onChordAdditionEvent(ChordsAddEvent chordsevt) {
		chords.addAll(chordsevt.getConfiguredChords());
	}

	protected UIAction actionNew = new UIAction("New", Resource.CLEAR, "Create a new pool", null) {
		@Override
		public void action(ActionEvent e) {
			if (showConfirmDialog(Resource.MIDI, "Clear everything?")) {
				table.clearSelection();
				model.clear();
				chords.clear();
			}
		}

	};

	UIAction actionPrint = new UIAction("Print", Resource.MIDI, "Print current workspace", null) {

		@Override
		public void action(ActionEvent e) {
			showMessage(Resource.MIDI, "Printing is under development.");

			/*
			 * try { boolean complete = table.print(); if (complete)
			 * UI.showMessage(Resource.MIDI, frame, "Printing Job Sent.", Resource.midi);
			 * 
			 * } catch (PrinterException pe) { UI.showMessage(Resource.MIDI, frame,
			 * "Printing failed.", Resource.midi); }
			 */
		}
	};

	UIAction actionExit = new UIAction("Exit", Resource.MIDI, "Exits the application", null) {
		@Override
		public void action(ActionEvent e) {
			shutdownApp();
		}
	};

	private void handleNewChordPoolModel(ChordPoolModel chordPoolModel) {

		Rhythm rhythm = chordPoolModel.getRhythm();
		boolean found = false;

		for (Rhythm r : rhythms) {
			if (rhythm.equals(r)) {
				rhythms.setSelectedItem(r);
				found = true;
				break;
			}
		}
		if (!found) {
			showMessage(Resource.MIDI, "Imported a new rhythm: " + rhythm.toString());
			config.getRhythms().add(rhythm);
			config.save();
			rhythms.setSelectedItem(rhythm);
		}
		model.fireTableStructureChanged();
		model.fireTableDataChanged();
		model.setupFromCopy(chordPoolModel);
		repeatSpinnerListModel.setValue(chordPoolModel.getRepeat());
		tempoSpinnerListModel.setValue(chordPoolModel.getTempo());
		Event.post(new UpdateGUIEvent());
	}

	protected UIAction actionSave = new UIAction("Save", Resource.SAVE, "Save the chord pool to a file", null) {
		@Override
		public void action(ActionEvent e) {
			fileChooser.setCurrentDirectory(new File(config.getDirectory()));
			int returnVal = fileChooser.showSaveDialog(getFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					File file = fileChooser.getSelectedFile();
					if (RGFileFilter.hasIncorectExtension(file)) {
						String path = file.getAbsolutePath();
						file = new File(path + "." + RGFileFilter.EXTENSION);
						config.setDirectory(fileChooser.getCurrentDirectory().getAbsolutePath());
					}
					dataHandler.saveObject(file, model, ChordPoolModel.class);
				} catch (Exception ex) {
					ex.printStackTrace();
					showMessage(Resource.MIDI, "Could not save the selected file");
				}
			}
		}
	};

	protected UIAction actionOpen = new UIAction("Open", Resource.LOAD, "Load chords from file", null) {
		@Override
		public void action(ActionEvent evt) {

			if (config.getDirectory() == null) {
				config.setDirectory(System.getProperty("user.home"));
				logger.info("Setting working directory to user's home directory");
			}
			File dir = new File(config.getDirectory());

			fileChooser.setCurrentDirectory(dir);
			fileChooser.setDialogTitle("Open a RhythmGuitar file");

			int returnVal = fileChooser.showOpenDialog(getFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					handleNewChordPoolModel(dataHandler.loadObject(fileChooser.getSelectedFile(), ChordPoolModel.class));
				} catch (FileNotFoundException e) {
					showMessage(Resource.MIDI, "Selected file was not found!", e);
					e.printStackTrace();
					return;
				}
				try {
					logger.info("Absolute path of directory is: " + fileChooser.getCurrentDirectory().getAbsolutePath());
					config.setDirectory(fileChooser.getCurrentDirectory().getAbsolutePath());
				} catch (Exception e) {
					showMessage(Resource.MIDI, "Error setting directory:", e);
				}
			}
		}
	};

	protected UIAction actionIncreaseRowRepeat = new UIAction("Increase Row Repeat", Resource.ADD, "Increase Row Repeat", null) {
		@Override
		public void action(ActionEvent e) {
			if (table.getSelectedRow() != -1) {
				int curvalue = model.get(table.getSelectedRow()).getRepeat();
				model.get(table.getSelectedRow()).setRepeat(curvalue + 1);
			} else
				showMessage(Resource.MIDI, "Select a row first");
		}
	};

	protected UIAction actionDecreaseRowRepeat = new UIAction("Decrease Row Repeat", Resource.REMOVE, "Decrease Row Repeat", null) {
		@Override
		public void action(ActionEvent e) {
			if (table.getSelectedRow() != -1) {
				int curvalue = model.get(table.getSelectedRow()).getRepeat();
				if (curvalue > 1)
					model.get(table.getSelectedRow()).setRepeat(curvalue - 1);
			} else
				showMessage(Resource.MIDI, "Select a row first");

		}

	};
	protected UIAction actionClearWorkspace = new UIAction("Clear", Resource.CLEAR, "Clear all the configured chords", null) {
		@Override
		public void action(ActionEvent e) {
			if (showConfirmDialog(Resource.MIDI, "Clear rows?")) {
				table.clearSelection();
				model.clear();
			}
		}

	};
	protected UIAction actionConfigureMidi = new UIAction("Configre Midi System", Resource.ALERT, "Configure Midi System", null) {
		@Override
		public void action(ActionEvent e) {
			expertSettingsDialog.show();
		}

	};
	protected UIAction actionConfigureRhythms = new UIAction("Configure Available Rhythms", Resource.BUP, "Configure Available Rhythms", null) {
		@Override
		public void action(ActionEvent e) {
			rhythmEditorDialog.show();
			if (rhythmEditorDialog.getRemovedIndices().size() > 0)
				rhythmCombo.setSelectedIndex(0);
		}

	};

	protected void play(ChordPoolModel mdl, int firstRow, int grow) {
		if (mdl.isEmpty())
			return;

		try {
			int deviceIdx = config.getMidiConfig().getDeviceIndex();
			int instrIdx = config.getMidiConfig().getInstrumentIndex();
			midi.play(deviceIdx, instrIdx, mdl, firstRow, grow);
			table.clearSelection();
			playStatus.setStatus(PLAY);
		} catch (InvalidMidiDataException e) {
			showMessage(Resource.MIDI, "Invalid Midi Data!", e);
			e.printStackTrace();
			return;
		} catch (MidiUnavailableException e) {
			showMessage(Resource.MIDI, "Midi Unavailable!", e);
			e.printStackTrace();
			return;
		}

		progressBar.setMaximum(midi.getTickLength());
	}

	protected UIAction actionPlaybackPlayAll = new UIAction("Play", Resource.PLAY, "Play", null) {
		@Override
		public void action(ActionEvent e) {
			play(model, 0, 10);
		}

	};
	protected UIAction actionPlaybackPlaySelection = new UIAction("Play Selected Rows", Resource.PLAY, "Play only the selected rows if any", null) {
		@Override
		public void action(ActionEvent e) {
			ChordPoolModel selection = new ChordPoolModel(null);
			selection.setRhythm(model.getRhythm());
			if (table.getSelectedRowCount() == 0)
				return;
			int[] selrows = table.getSelectedRows();
			for (int i : selrows)
				selection.add(model.get(i));
			play(selection, selrows[0], 0);
		}

	};
	protected UIAction actionPlaybackPlayAdvanced = new UIAction("Advanced Playback", Resource.PLAY, "Advanced Playback", null) {
		@Override
		public void action(ActionEvent e) {
			showMessage(Resource.MIDI, "Not implemented!");
		}

	};
	protected UIAction actionPlaybackStop = new UIAction("Stop", Resource.STOP, "Stop Playback", null) {
		@Override
		public void action(ActionEvent e) {
			midi.stop();
			playStatus.setStatus(STOP);
			progressBar.setValue(0);
		}
	};

	protected UIAction actionOnlineHelp = new UIAction("Online Help", "", null) {
		@Override
		public void action(ActionEvent evt) {
			try {
				java.awt.Desktop.getDesktop().browse(new URI(Statics.HOMEPAGE));
			} catch (Exception e) {
				showMessage(Resource.MIDI, "Cannot open homepage due to problematic browser configuration", e);
			}
		}

	};
	protected UIAction actionAbout = new UIAction("About", "About this application", null) {

		@Override
		public void action(ActionEvent e) {
			StringBuilder sb = new StringBuilder();
			sb.append("<html>");
			sb.append("<h1>Rhythm Guitar</h1>");
			sb.append("<p>Blah Blaah!</p>");
			sb.append("<p>Created by <a href='mailto:petmakris@gmail.com'>petmakris@gmail.com</a></p>");
			sb.append("</html>");
			String title = "About Rhythm Guitar!";
			JOptionPane.showMessageDialog(frame, sb.toString(), title, JOptionPane.OK_OPTION, UI.getUnscaledIcon(Resource.MIDI));
		}

	};

	protected UIAction actionWorkspaceAddRowFirst = new UIAction("Add on Top", Resource.BUP, "Add Row on Top", null) {
		@Override
		public void action(ActionEvent e) {
			table.clearSelection();
			if (chordList.getSelectedIndex() != -1) {
				Row r = new Row(1);
				Chord chord = (Chord) chordList.getSelectedValue();
				r.setupFromChord(chord, model.getRhythm());
				model.add(0, r);
			} else
				showMessage(Resource.MIDI, "No chord selected, select a chord first");
		}
	};

	protected UIAction actionWorkspaceAddRowAt = new UIAction("Add Before", Resource.BRIGHT, "Add Row before the selected Row", null) {
		@Override
		public void action(ActionEvent e) {
			if (chordList.getSelectedIndex() != -1 && table.getSelectedRow() != -1) {
				int idx = table.getSelectedRow();
				Row r = new Row(1);
				Chord chord = (Chord) chordList.getSelectedValue();
				r.setupFromChord(chord, model.getRhythm());
				model.add(idx, r);
			} else
				showMessage(Resource.MIDI, "No chord or row selected, select a row and a chord first");
		}
	};

	protected UIAction actionWorkspaceAddRowLast = new UIAction("Add on Bottom", Resource.BDOWN, "Add Row on Bottom", null) {
		@Override
		public void action(ActionEvent e) {
			table.clearSelection();
			if (chordList.getSelectedIndex() != -1) {
				Row r = new Row(1);
				Chord chord = (Chord) chordList.getSelectedValue();
				r.setupFromChord(chord, model.getRhythm());
				model.add(r);
			} else
				showMessage(Resource.MIDI, "No chord selected, select a chord first");
		}
	};

	protected UIAction actionWorkspaceCopyRowsLast = new UIAction("Copy Bottom", Resource.DOWN, "Copy Selected Rows on Bottom", null) {
		@Override
		public void action(ActionEvent e) {
			if (table.getSelectedRowCount() != 0) {
				logger.info("implement");
			} else
				showMessage(Resource.MIDI, "No row selected, select some rows first");
		}
	};

	protected UIAction actionWorkspaceRemoveRows = new UIAction("Remove Rows", Resource.DELETEX, "Remove selected Rows", null) {
		@Override
		public void action(ActionEvent e) {
			if (table.getSelectedRowCount() == 0) {
				showMessage(Resource.MIDI, "Select some rows to remove");
				return;
			}

			int rows[] = table.getSelectedRows();
			for (int j = rows.length - 1; j >= 0; j--) {
				model.remove(rows[j]);
			}
			table.clearSelection();
		}

	};

	protected UIAction actionWorkspaceEdit = new UIAction("Edit", Resource.ADD32, "Toggle Edit Modde", null) {
		@Override
		public void action(ActionEvent e) {
			if (!e.getSource().equals(checkEdit))
				checkEdit.setSelected(!checkEdit.isSelected());

			model.setEditable(checkEdit.isSelected());
			table.clearSelection();
			toggleEditEnable(!checkEdit.isSelected());
		}

	};

	protected UIAction actionWorkspaceHalfToneUp = new UIAction("Halftone Up", Resource.UP, "Move Halftone up", null) {
		@Override
		public void action(ActionEvent e) {
			model.moveUp();
		}
	};

	protected UIAction actionWorkspaceHalfToneDown = new UIAction("Halftone Down", Resource.DOWN, "Move Halftone down", null) {
		@Override
		public void action(ActionEvent e) {
			model.moveDown();
		}
	};

	protected UIAction actionChordsAdd = new UIAction("", Resource.CHORD_ADD, "Add Chord", null) {
		@Override
		public void action(ActionEvent e) {
			chordEditorDialog.show();
			chordList.clearSelection();
		}
	};

	protected UIAction actionChordsRemove = new UIAction("", Resource.CHORD_REMOVE, "Remove the selected chord", null) {
		@Override
		public void action(ActionEvent e) {

			if (chordList.getSelectedIndex() == -1) {
				showMessage(Resource.MIDI, "No chord to remove, select a chord for removal");
				return;
			}
			if (chords.size() == 1) {
				chords.remove(chordList.getSelectedIndex());
				model.clear();
			} else {
				chords.remove(chordList.getSelectedIndex());
			}
			model.removeUnconfiguredChords();
		}
	};

	protected UIAction actionChordsClear = new UIAction("", Resource.CHORD_CLEAR, "Clear configure chords", null) {
		@Override
		public void action(ActionEvent e) {
			if (chords.size() == 0) {
				showMessage(Resource.MIDI, "No chords to remove");
				return;
			}
			model.clear();
			chords.clear();
		}

	};

	protected void toggleControlsEnable(boolean enabled) {
		actionNew.setEnabled(enabled);
		actionPrint.setEnabled(enabled);
		actionWorkspaceAddRowFirst.setEnabled(enabled);
		actionWorkspaceAddRowAt.setEnabled(enabled);
		actionWorkspaceAddRowLast.setEnabled(enabled);
		actionWorkspaceHalfToneDown.setEnabled(enabled);
		actionWorkspaceHalfToneUp.setEnabled(enabled);
		actionIncreaseRowRepeat.setEnabled(enabled);
		actionDecreaseRowRepeat.setEnabled(enabled);
		actionWorkspaceRemoveRows.setEnabled(enabled);
		actionClearWorkspace.setEnabled(enabled);
		actionChordsAdd.setEnabled(enabled);
		actionChordsRemove.setEnabled(enabled);
		actionChordsClear.setEnabled(enabled);
		actionPlaybackPlayAll.setEnabled(enabled);
		actionPlaybackPlaySelection.setEnabled(enabled);
		actionPlaybackPlayAdvanced.setEnabled(enabled);
		actionPlaybackStop.setEnabled(!enabled);
		actionSave.setEnabled(enabled);
		actionOpen.setEnabled(enabled);
		actionConfigureRhythms.setEnabled(enabled);
		actionConfigureMidi.setEnabled(enabled);
		rhythmCombo.setEnabled(enabled);
		tempoSpinner.setEnabled(enabled);
		repeatSpinner.setEnabled(enabled);
	}

	protected void toggleWorkspaceEnable(boolean enabled) {
		toggleControlsEnable(enabled);
		actionWorkspaceEdit.setEnabled(enabled);
	}

	protected void toggleEditEnable(boolean enabled) {
		toggleControlsEnable(enabled);
		actionPlaybackStop.setEnabled(enabled);
	}

	/*
	 * GUI Configuration
	 */

	public static class UIMenuItem extends JMenuItem {

		public UIMenuItem(Action action) {
			super(action);
		}

		public UIMenuItem(Action action, KeyStroke keyStroke) {
			this(action);
			setAccelerator(keyStroke);
		}
	}

	private KeyStroke getMetaKey(int key) {
		int actionEvent = ActionEvent.CTRL_MASK;
		if (Config.getOSType() == OSType.Macosx)
			actionEvent = ActionEvent.META_MASK;
		return KeyStroke.getKeyStroke(key, actionEvent);
	}

	private KeyStroke getAltKey(int key) {
		return KeyStroke.getKeyStroke(key, ActionEvent.ALT_MASK);
	}

	@Override
	public JMenuBar configureMenu() {
		JMenuBar menuBar;
		JMenu menu;
		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.add(new UIMenuItem(actionNew, getMetaKey(KeyEvent.VK_N)));
		menu.add(new UIMenuItem(actionSave, getMetaKey(KeyEvent.VK_S)));
		menu.add(new UIMenuItem(actionOpen, getMetaKey(KeyEvent.VK_O)));
		menu.add(new UIMenuItem(actionPrint, getAltKey(KeyEvent.VK_P)));
		menu.addSeparator();
		menu.add(new UIMenuItem(actionExit, getMetaKey(KeyEvent.VK_X)));
		menuBar.add(menu);

		menu = new JMenu("Workspace");
		menu.add(new UIMenuItem(actionWorkspaceAddRowFirst, getMetaKey(KeyEvent.VK_T)));
		menu.add(new UIMenuItem(actionWorkspaceAddRowAt, getMetaKey(KeyEvent.VK_M)));
		menu.add(new UIMenuItem(actionWorkspaceAddRowLast, getMetaKey(KeyEvent.VK_B)));
		menu.add(new UIMenuItem(actionWorkspaceCopyRowsLast, getMetaKey(KeyEvent.VK_V)));
		menu.add(new UIMenuItem(actionWorkspaceRemoveRows, getMetaKey(KeyEvent.VK_R)));
		menu.addSeparator();
		menu.add(new UIMenuItem(actionWorkspaceHalfToneUp, getMetaKey(KeyEvent.VK_RIGHT)));
		menu.add(new UIMenuItem(actionWorkspaceHalfToneDown, getMetaKey(KeyEvent.VK_LEFT)));
		menu.addSeparator();
		menu.add(new UIMenuItem(actionIncreaseRowRepeat, getMetaKey(KeyEvent.VK_UP)));
		menu.add(new UIMenuItem(actionDecreaseRowRepeat, getMetaKey(KeyEvent.VK_DOWN)));
		menu.addSeparator();
		menu.add(new UIMenuItem(actionWorkspaceEdit, getMetaKey(KeyEvent.VK_E)));
		menu.add(new UIMenuItem(actionClearWorkspace, getMetaKey(KeyEvent.VK_C)));
		menuBar.add(menu);

		menu = new JMenu("Settings");
		menu.add(new UIMenuItem(actionConfigureMidi, getMetaKey(KeyEvent.VK_COMMA)));
		menu.add(new UIMenuItem(actionConfigureRhythms, getAltKey(KeyEvent.VK_R)));
		menuBar.add(menu);

		menu = new JMenu("Playback");
		menu.add(new UIMenuItem(actionPlaybackPlayAll, getMetaKey(KeyEvent.VK_P)));
		menu.add(new UIMenuItem(actionPlaybackPlaySelection));
		menu.add(new UIMenuItem(actionPlaybackPlayAdvanced));
		menu.addSeparator();
		menu.add(new UIMenuItem(actionPlaybackStop, KeyStroke.getKeyStroke(" ")));
		menuBar.add(menu);

		menu = new JMenu("Chords");
		UIMenuItem m = new UIMenuItem(actionChordsAdd, getMetaKey(KeyEvent.VK_A));
		m.setText("Add a new chord");
		menu.add(m);
		m = new UIMenuItem(actionChordsRemove);
		m.setText("Remove Selected Chord");
		menu.add(m);
		m = new UIMenuItem(actionChordsClear);
		m.setText("Clear Configured Chords");
		menu.add(m);
		menuBar.add(menu);

		menu = new JMenu("Help");
		menu.add(new UIMenuItem(actionOnlineHelp));
		menu.addSeparator();
		menu.add(new UIMenuItem(actionAbout));
		menuBar.add(menu);
		return menuBar;
	}

	@Override
	public GPanel configureGui() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, H, configureTopPanel());
		panel.add(0, 1, 1, 1, 1, F, CR, B, configureBottomPanel());
		return panel;
	}

	public GPanel configureBottomPanel() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, F, F, CR, B, configureWorkspacePanel());
		panel.add(1, 0, 1, 1, 1, F, CR, B, configureRightSidePanel());
		return panel;
	}

	public GPanel configureTopPanel() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, LS, N, new Insets(10, 5, 10, 5), rhythmCombo);
		panel.add(1, 0, 1, 1, 1, 1, CR, H, checkEdit);
		panel.add(2, 0, 1, 1, 1, 1, LE, N, configurePlaybackPanel());
		return panel;
	}

	private GPanel configureWorkspacePanel() {
		GPanel panel = new GPanel();
		table = new JTable(model);

		table.setRowHeight(25);
		table.setRowMargin(5);
		table.setDoubleBuffered(true);

		table.setFont(new Font("Monospace", Font.BOLD, 14));

		// TODO: find a way to resize the columns by percent
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					table.clearSelection();
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});

		JComboBox<Chord> combo = new JComboBox<>(model.getChordComboModel());
		combo.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// toggleWorkspaceEnable(false);
			}

			@Override
			public void focusLost(FocusEvent e) {
				// toggleWorkspaceEnable(true);
				table.clearSelection();
			}
		});

		// table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setDefaultEditor(Chord.class, new CustomTableCellEditor(combo));
		table.setDefaultRenderer(Chord.class, new CustomTableCellRenderer());

		JScrollPane sp = new JScrollPane(table);
		sp.setWheelScrollingEnabled(true);
		panel.add(0, 0, 1, 1, 1, F, CR, B, new Insets(1, 3, 1, 3), sp);
		panel.add(0, 1, 1, 1, 1, 1, CR, H, new Insets(1, 3, 1, 3), configureTableButton());
		return panel;
	}

	public static class CustomTableCellEditor extends DefaultCellEditor {

		public CustomTableCellEditor(JComboBox<Chord> paramJComboBox) {
			super(paramJComboBox);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

			@SuppressWarnings("unchecked")
			JComboBox<Chord> compo = (JComboBox<Chord>) super.getTableCellEditorComponent(table, value, isSelected, row, column);

			compo.setFont(new Font("Monospace", Font.BOLD, 14));
			Dimension d = compo.getSize();
			compo.setMinimumSize(new Dimension(d.width, d.height + 5));
			return compo;
		}
	}

	public class CustomTableCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			if (isSelected)
				return c;

			ChordPoolModel cpm = null;
			try {
				cpm = (ChordPoolModel) table.getModel();
			} catch (Exception e) {
				logger.error("Exception occured when trying to get the table model" + e.getMessage());
				return c;
			}

			Chord bs = chords.get(0);
			Chord ch = cpm.get(row).get(col);

			configureColor(c, Color.BLACK, 0xFFFFFF, Font.BOLD);

			switch (Chord.distanceOf(bs, ch)) {
			case TONE:
				configureColor(c, Color.BLACK, 0xdceafe, Font.BOLD);
				break;
			case SYN_TONE:
				configureColor(c, Color.MAGENTA, 0xdceafe, Font.ITALIC);
				break;
			case TONE_FOURTH:
				configureColor(c, Color.BLACK, 0x0cd1f5, Font.BOLD);
				break;
			case TONE_FIFTH:
				configureColor(c, Color.BLACK, 0x026b84, Font.BOLD);
				break;
			case TONE_SEVENTH:
				configureColor(c, Color.BLACK, 0x026b84, Font.BOLD);
				break;
			case REL_TONE:
				configureColor(c, Color.WHITE, 0xdceafe, Font.ITALIC);
				break;
			case REL_FOURTH:
				configureColor(c, Color.BLACK, 0xdceafe, Font.BOLD);
				break;
			case REL_FIFTH:
				configureColor(c, Color.BLACK, 0x06986c, Font.BOLD);
				break;
			case REL_SEVENT:
				configureColor(c, Color.BLACK, 0x026b84, Font.BOLD);
				break;
			case NONE:
				configureColor(c, Color.BLACK, 0xFFFFFF, Font.BOLD);
				break;
			default:
				configureColor(c, Color.BLACK, 0xFFFFFF, Font.BOLD);
				break;
			}
			return c;
		}
	}

	private static void configureColor(Component c, Color fg, int bg, int ft) {
		c.setForeground(fg);
		c.setBackground(new Color(bg));
		c.setFont(new Font("Monospace", ft, 14));
		c.setMinimumSize(new Dimension(100, 100));
	}

	private GPanel configureRightSidePanel() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, H, configureTempoAndRepeat());
		panel.add(0, 1, 1, 1, 1, F, CR, B, configureChordPanel());
		return panel;
	}

	private GPanel configureChordPanel() {
		GPanel panel = new GPanel();
		chords = model.getChordComboModel();
		chordList = new GInventoryList<>(chords);
		JScrollPane sp = new JScrollPane(chordList);
		sp.setPreferredSize(new Dimension(150, 100));
		panel.add(0, 0, 1, 1, 1, F, CR, B, sp);
		panel.add(0, 1, 1, 1, 1, 1, CR, H, configureChordPanelButtons());
		return panel;
	}

	private GPanel configureChordPanelButtons() {
		GPanel panel = new GPanel();
		JButton btnAdd = new JButton(actionChordsAdd);
		JButton btnRemove = new JButton(actionChordsRemove);
		JButton btnClear = new JButton(actionChordsClear);
		btnAdd.setIcon(UI.getScaledIcon(12, 12, Resource.CHORD_ADD));
		btnRemove.setIcon(UI.getScaledIcon(12, 12, Resource.CHORD_REMOVE));
		btnClear.setIcon(UI.getScaledIcon(12, 12, Resource.CHORD_CLEAR));
		panel.add(0, 0, 1, 1, 1, 1, CR, H, btnAdd);
		panel.add(1, 0, 1, 1, 1, 1, CR, H, btnRemove);
		panel.add(2, 0, 1, 1, 1, 1, CR, H, btnClear);
		UI.configureSize(btnAdd, btnRemove, btnClear);
		return panel;
	}

	public GPanel configureTempoAndRepeat() {
		GPanel panel = new GPanel();
		panel.add(0, 1, 1, 1, 1, 1, LE, H, new JLabel("Tempo"));
		panel.add(0, 2, 1, 1, 1, 1, LS, H, configureTempo());
		panel.add(0, 3, 1, 1, 1, 1, LE, H, new JLabel("Repeat"));
		panel.add(0, 4, 1, 1, 1, 1, LS, H, configureGlobalRepeat());
		return panel;
	}

	JSpinner tempoSpinner;
	SpinnerListModel tempoSpinnerListModel;

	public GPanel configureTempo() {
		GPanel panel = new GPanel();
		Integer[] tempovalues = new Integer[500];
		for (int i = 1; i <= 500; i++)
			tempovalues[i - 1] = i;

		tempoSpinnerListModel = new SpinnerListModel(tempovalues);
		tempoSpinner = new JSpinner(tempoSpinnerListModel);

		tempoSpinner.setToolTipText("Tembo in BPM");
		tempoSpinner.setValue(model.getTempo());
		tempoSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				model.setTempo((Integer) tempoSpinner.getValue());
			}
		});
		panel.add(0, 0, 1, 1, 1, 1, LE, H, UI.INSETS, 25, 0, tempoSpinner);
		return panel;
	}

	JSpinner repeatSpinner;
	SpinnerListModel repeatSpinnerListModel;

	public GPanel configureGlobalRepeat() {
		GPanel panel = new GPanel();
		Integer[] repvalues = new Integer[256];
		for (int i = 1; i <= 256; i++)
			repvalues[i - 1] = i;

		repeatSpinnerListModel = new SpinnerListModel(repvalues);
		repeatSpinner = new JSpinner(repeatSpinnerListModel);
		repeatSpinner.setToolTipText("Global Repeat");
		repeatSpinner.setValue(model.getRepeat());
		repeatSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				model.setRepeat((Integer) repeatSpinner.getValue());
			}
		});
		panel.add(0, 0, 1, 1, 1, 1, LE, H, UI.INSETS, 25, 0, repeatSpinner);
		return panel;
	}

	private GPanel configureTableButton() {
		GPanel panel = new GPanel();

		JButton btn1 = new JButton(actionWorkspaceAddRowFirst);
		JButton btn2 = new JButton(actionWorkspaceAddRowAt);
		JButton btn3 = new JButton(actionWorkspaceAddRowLast);
		JButton btn4 = new JButton(actionIncreaseRowRepeat);
		JButton btn5 = new JButton(actionDecreaseRowRepeat);
		JButton btn6 = new JButton(actionWorkspaceRemoveRows);

		btn1.setText("<html>Add row<br>on top</html>");
		btn2.setText("<html>Add row<br>before row</html>");
		btn3.setText("<html>Add row<br>on bottom</html>");
		btn4.setText("<html>Increase<br>row repeat</html>");
		btn5.setText("<html>Decrease<br>row repeat</html>");
		btn6.setText("<html>Remove<br>selected rows</html>");

		panel.add(0, 0, 1, 1, 1, 1, CR, B, btn1);
		panel.add(1, 0, 1, 1, 1, 1, CR, B, btn2);
		panel.add(2, 0, 1, 1, 1, 1, CR, B, btn3);
		panel.add(3, 0, 1, 1, 1, 1, CR, B, btn4);
		panel.add(4, 0, 1, 1, 1, 1, CR, B, btn5);
		panel.add(5, 0, 1, 1, 1, 1, CR, B, btn6);
		UI.configureSize(btn1, btn2, btn3, btn4, btn5, btn6);
		return panel;
	}

	public GPanel configurePlaybackPanel() {
		GPanel panel = new GPanel();
		JButton btnPlayAll = new JButton(actionPlaybackPlayAll);
		JButton btnPlaySelection = new JButton(actionPlaybackPlaySelection);
		JButton btnPlayAdvanced = new JButton(actionPlaybackPlayAdvanced);
		JButton btnPlayStop = new JButton(actionPlaybackStop);
		panel.add(0, 0, 1, 1, 1, 1, LE, H, new Insets(0, 10, 0, 10), labelSpeed);
		panel.add(1, 0, 1, 1, 1, 1, LE, H, btnPlayAll);
		panel.add(2, 0, 1, 1, 1, 1, LE, H, btnPlaySelection);
		panel.add(3, 0, 1, 1, 1, 1, LE, H, btnPlayAdvanced);
		panel.add(4, 0, 1, 1, 1, 1, LS, H, btnPlayStop);
		panel.add(5, 0, 1, 1, 1, 1, CR, H, new Insets(10, 10, 10, 10), configureProgress());
		btnPlayAll.setText("Play");
		btnPlaySelection.setText("Selected");
		btnPlayAdvanced.setText("Advanced");
		btnPlayStop.setText("Stop");
		UI.configureSize(btnPlayAll, btnPlaySelection, btnPlayAdvanced, btnPlayStop);
		return panel;
	}

	private GPanel configureProgress() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, N, new Insets(0, 0, 0, 5), playStatus);
		panel.add(1, 0, 1, 1, F, 1, CR, H, new Insets(0, 0, 0, 0), progressBar);
		return panel;
	}

	public static class FrameFactory implements RunnerFrameFactory<JRhythm> {

		@Override
		public JRhythm getInstance() {
			return new JRhythm();
		}
	}

	protected void shutdownApp() {
		boolean savedPreferences = config.save();
		if (!savedPreferences) {
			showMessage(Resource.MIDI, "Could not save preferences. Some recently performed changes will be lost.");
		}
		super.shutdownApp();
	}

	protected void restartApp() {
		boolean savedPreferences = config.save();
		if (!savedPreferences) {
			showMessage(Resource.MIDI, "Could not save preferences. Some recently performed changes will be lost.");
		}
		super.restartApp();
	}

	static GuiRunner<JRhythm> guiRunner = new GuiRunner<JRhythm>(new FrameFactory());

	@SuppressWarnings("unchecked")
	@Override
	protected GuiRunner<JRhythm> getGuiRunner() {
		return guiRunner;
	}

	public static void main(String[] tests) {
		guiRunner.run();
	}

}
