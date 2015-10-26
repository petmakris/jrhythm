package gr.bytewise.jrhythm.gui.dialogs;

import static gr.bytewise.jrhythm.guitk.UIConstraints.B;
import static gr.bytewise.jrhythm.guitk.UIConstraints.BE;
import static gr.bytewise.jrhythm.guitk.UIConstraints.CR;
import static gr.bytewise.jrhythm.guitk.UIConstraints.F;
import static gr.bytewise.jrhythm.guitk.UIConstraints.H;
import static gr.bytewise.jrhythm.guitk.UIConstraints.LE;
import static gr.bytewise.jrhythm.guitk.UIConstraints.LS;
import static gr.bytewise.jrhythm.guitk.UIConstraints.N;
import static gr.bytewise.jrhythm.guitk.UIConstraints.PE;
import static gr.bytewise.jrhythm.guitk.UIConstraints.TS;
import gr.bytewise.jrhythm.api.Rhythm;
import gr.bytewise.jrhythm.gui.config.Config;
import gr.bytewise.jrhythm.gui.config.Resource;
import gr.bytewise.jrhythm.guitk.BaseDialog;
import gr.bytewise.jrhythm.guitk.GPanel;
import gr.bytewise.jrhythm.guitk.UI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.swing.DefaultListCellRenderer;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RhythmEditorDialog extends BaseDialog implements ActionListener {

	Config config = Config.getInstance();

	static class Actions {
		public static final String ADD_RHYTHM = "ADD_RHYTHM";
		public static final String REM_RHYTHM = "REM_RHYTHM";
		public static final String HIDE = "ACTION_HIDE";
		public static final String UPDATE = "UDPATE";
	}

	public RhythmEditorDialog(JFrame parent) {
		super(parent, "Rhythm Editor", true);
		finalizeConstruction();
		dialog.setResizable(true);
		enable(false);
	}

	@Override
	protected Dimension configureSize() {
		return new Dimension(600, 400);
	}

	JButton btnUpdate = UI.newButton("Update", Actions.UPDATE, null, "Save changes to the currently selected Rhythm", this);
	JButton btnHide = UI.newButton("Close", Actions.HIDE, null, "Hide this dialog", this);

	@SuppressWarnings("rawtypes")
	JList rhythmList = new JList();

	@Override
	protected Component configureGui() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 2, 1, 1, CR, B, new Insets(0, 0, 0, 5), configureList().getPanel());
		panel.add(1, 0, 1, 1, 4, 1, TS, H, new Insets(0, 5, 0, 0), configureControls().getPanel());
		panel.add(1, 1, 1, 1, 1, 1, BE, N, new Insets(0, 0, 0, 0), configureEditButtons().getPanel());

		return panel.getPanel();

	}

	@SuppressWarnings("unchecked")
	private GPanel configureList() {
		GPanel panel = new GPanel();
		rhythmList.setModel(config.getRhythms());
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) rhythmList.getCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		rhythmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rhythmList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {

				clearEditor();

				if (!arg0.getValueIsAdjusting()) {
					int idx = rhythmList.getSelectedIndex();
					if (idx != -1) {
						textFieldName.setText(config.getRhythms().get(idx).getName());
						textFieldNotation.setText(arrayToString(config.getRhythms().get(idx).getNotation()));
						textFieldDuration.setText(arrayToString(config.getRhythms().get(idx).getDuration()));
						enable(true);

					}
				} else
					enable(false);
			}

		});
		JScrollPane pane = new JScrollPane(rhythmList);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(0, 0, 1, 1, 1, F, CR, B, pane);
		panel.add(0, 1, 1, 1, 1, 1, PE, H, new Insets(5, 0, 0, 0), configureListButtons().getPanel());
		return panel;
	}

	private void clearEditor() {
		enable(false);
		lblDurationValid.setIcon(null);
		lblNotationValid.setIcon(null);
	}

	private void enable(boolean v) {
		textFieldName.setEnabled(v);
		textFieldNotation.setEnabled(v);
		textFieldDuration.setEnabled(v);
		btnUpdate.setEnabled(v);

	}

	private static String arrayToString(int[] array) {
		StringBuilder sb = new StringBuilder();
		for (int o : array)
			sb.append(Integer.toString(o));
		return sb.toString();
	}

	JButton btnAddRhythm;
	JButton btnRemRhythm;

	private GPanel configureListButtons() {
		GPanel panel = new GPanel();
		btnAddRhythm = UI.newButton("Add", Actions.ADD_RHYTHM, null, "Add Rythm", this);
		btnRemRhythm = UI.newButton("Remove", Actions.REM_RHYTHM, null, "Remove Selected Rythm", this);
		panel.add(0, 1, 1, 1, 1, 1, LE, H, btnAddRhythm);
		panel.add(1, 1, 1, 1, 1, 1, LS, H, btnRemRhythm);
		UI.configureSize(btnAddRhythm, btnRemRhythm);
		return panel;
	}

	JTextField textFieldDuration = new JTextField();
	JTextField textFieldNotation = new JTextField();
	JTextField textFieldName = new JTextField();
	JLabel lblNotationValid = new JLabel();
	JLabel lblDurationValid = new JLabel();

	private GPanel configureControls() {
		GPanel panel = new GPanel();

		textFieldNotation.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				boolean matches = Pattern.matches("[01]+", ((JTextField) input).getText());
				lblNotationValid.setIcon(matches ? UI.getScaledIcon(Resource.VALID) : UI.getScaledIcon(Resource.INVALID));
				return matches;
			}
		});

		textFieldDuration.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				boolean matches = Pattern.matches("[1234]+", ((JTextField) input).getText());
				lblDurationValid.setIcon(matches ? UI.getScaledIcon(Resource.VALID) : UI.getScaledIcon(Resource.INVALID));
				return matches;
			}
		});
		/*
		 * textFieldName.addKeyListener(new KeyListener() {
		 * 
		 * @Override public void keyPressed(KeyEvent arg0) { }
		 * 
		 * @Override public void keyReleased(KeyEvent arg0) {}
		 * 
		 * @Override public void keyTyped(KeyEvent arg0) { }
		 * 
		 * });
		 */

		JLabel notationLabel = new JLabel();
		JLabel durationLabel = new JLabel();
		JLabel nameLabel = new JLabel();

		notationLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
		durationLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
		nameLabel.setHorizontalAlignment(JLabel.HORIZONTAL);

		nameLabel.setText("Rhythm Name");
		notationLabel.setText("Notation Description");
		durationLabel.setText("Duration Description");

		lblDurationValid.setIcon(null);
		lblNotationValid.setIcon(null);

		JLabel descLabel = new JLabel();
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<h3>Configuration</h3>");
		sb.append("<p>Valid configuration blah blah blah</p>");
		sb.append("</html>");
		descLabel.setText(sb.toString());
		panel.add(0, 0, 2, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), nameLabel);
		panel.add(0, 1, 2, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), textFieldName);
		panel.add(0, 2, 2, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), descLabel);
		panel.add(0, 3, 2, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), new JSeparator());
		panel.add(0, 4, 2, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), notationLabel);
		panel.add(0, 5, 1, 1, F, 1, CR, H, new Insets(0, 10, 0, 10), textFieldNotation);
		panel.add(1, 5, 1, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), lblNotationValid);
		panel.add(0, 6, 2, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), new JSeparator());
		panel.add(0, 7, 2, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), durationLabel);
		panel.add(0, 8, 1, 1, F, 1, CR, H, new Insets(0, 10, 0, 10), textFieldDuration);
		panel.add(1, 8, 1, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), lblDurationValid);
		panel.add(0, 9, 2, 1, 1, 1, CR, H, new Insets(0, 10, 0, 10), new JSeparator());
		return panel;
	}

	private GPanel configureEditButtons() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, H, new Insets(0, 0, 0, 0), btnUpdate);
		panel.add(1, 0, 1, 1, 1, 1, CR, H, new Insets(0, 0, 0, 0), btnHide);
		UI.configureSize(btnUpdate, btnHide);
		return panel;
	}

	private int[] getStringAsIntArray(String s) throws Exception {
		int[] inta = new int[s.length()];
		for (int i = 0; i < s.length(); i++)
			inta[i] = Integer.parseInt(new String(new char[] { s.charAt(i) }));
		return inta;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String s = evt.getActionCommand();

		if (s.equals(Actions.HIDE))
			hide();

		if (s.equals(Actions.ADD_RHYTHM)) {
			rhythmList.clearSelection();
			config.getRhythms().add(new Rhythm("Rhythm Example " + new Random().nextInt(9), new int[] { 1, 2, 2 }, new int[] { 1, 0, 0 }));
			rhythmList.setSelectedIndex(rhythmList.getModel().getSize() - 1);
		}
		if (s.equals(Actions.REM_RHYTHM)) {
			if (rhythmList.getModel().getSize() == 1) {
				showMessage(Resource.MIDI, "Cannot remove the last configured Rhythm!");
				return;
			}

			int idx = rhythmList.getSelectedIndex();
			if (idx != -1)
				config.getRhythms().remove(idx);
			getRemovedIndices().add(idx);
		}
		if (s.equals(Actions.UPDATE)) {
			int idx = rhythmList.getSelectedIndex();
			if (idx != -1) {
				String nam = textFieldName.getText();
				String dur = textFieldDuration.getText();
				String not = textFieldNotation.getText();
				if (nam.length() > 0 && dur.length() == not.length()) {
					config.getRhythms().get(idx).setName(textFieldName.getText());
					try {
						config.getRhythms().get(idx).setDuration(getStringAsIntArray(textFieldDuration.getText()));
						config.getRhythms().get(idx).setNotation(getStringAsIntArray(textFieldNotation.getText()));
					} catch (Exception e) {
						UI.showMessage(parent, "Could not convert text field to arrays! Rhythm not saved!\nError message: " + e.getMessage(), Resource.ERROR1);
					}
					rhythmList.clearSelection();
					btnUpdate.setEnabled(false);
				} else
					UI.showMessage(parent, "Name must have some length!\nDuration array and notation array must have equal size!", Resource.ERROR1);
			}
		}
	}

	@Override
	public void show() {
		setRemovedIndices(new ArrayList<Integer>());
		super.show();
	}

	private List<Integer> removedIndex;

	public List<Integer> getRemovedIndices() {
		return removedIndex;
	}

	public void setRemovedIndices(List<Integer> removedIndex) {
		this.removedIndex = removedIndex;
	}

}
