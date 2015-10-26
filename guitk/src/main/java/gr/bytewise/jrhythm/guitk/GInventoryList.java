package gr.bytewise.jrhythm.guitk;

import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

public class GInventoryList<E> extends JList<E> {

	private final Font font = new Font("Monospace", Font.BOLD, 18);

	public GInventoryList(E[] values) {
		super(values);
		visualConfig();
	}

	public GInventoryList(ListModel<E> chords) {
		super(chords);
		visualConfig();
	}

	private void visualConfig() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) getCellRenderer();
		setFont(font);
		renderer.setHorizontalAlignment(JLabel.CENTER);
		setSelectedIndex(0);
	}
}
