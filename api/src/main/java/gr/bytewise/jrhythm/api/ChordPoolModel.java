package gr.bytewise.jrhythm.api;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.table.AbstractTableModel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ChordPoolModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChordPoolModel extends AbstractTableModel implements List<Row> {

	@XmlElementWrapper(name = "Rows")
	@XmlElement(name = "Row")
	private List<Row> data;

	@XmlElement(name = "Rhythm")
	private Rhythm rhythm = null;

	@XmlElementWrapper(name = "ChordList")
	@XmlElement(name = "Chord")
	private ChordComboModel chordComboModel;

	@XmlElement(name = "Description")
	private String description;

	@XmlElement(name = "Repeat")
	private Integer repeat;

	@XmlElement(name = "Tempo")
	private Integer tempo;

	private boolean editable = false;

	public ChordPoolModel() {
		data = new CopyOnWriteArrayList<Row>();
		chordComboModel = new ChordComboModel();
		setEditable(false);
		setRepeat(1);
		setTempo(60);
	}

	public ChordPoolModel(Void ingore) {
		data = new CopyOnWriteArrayList<Row>();
		chordComboModel = new ChordComboModel();
		setEditable(false);
		setRepeat(1);
		setTempo(60);
	}

	/*
	 * Custom methods
	 * In the future integrade the jcombo for rhythms in chordpoolmodel
	 * 
	 */

	public synchronized void setupFromCopy(ChordPoolModel cpm) {
		clear();
		setRhythm(cpm.getRhythm());
		setTempo(cpm.getTempo());
		setRepeat(cpm.getRepeat());
		chordComboModel.removeAllElements();
		for (int i = 0; i < cpm.getChordComboModel().getSize(); i++)
			chordComboModel.add(cpm.getChordComboModel().getElementAt(i));
		for (Row r0 : cpm) {
			// COPY REPEAT ALSO
			Row r = new Row(r0.getRepeat());
			for (Chord c0 : r0)
				r.add(chordComboModel.getElementAt(chordComboModel.getIndexOf(c0)));

			add(r);
		}
		if (cpm.getDescription() != null)
			setDescription(cpm.getDescription());
	}

	public Integer getRepeat() {
		return repeat;
	}

	public void setRepeat(Integer repeat) {
		this.repeat = repeat;
	}

	public Integer getTempo() {
		return tempo;
	}

	public void setTempo(Integer tempo) {
		this.tempo = tempo;
	}

	public void removeUnconfiguredChords() {
		int row = 0;
		for (Row r : this) {
			int col = 0;
			for (Chord c : r) {
				if (!existsInAvailableChords(c))
					get(row).set(col, getChordComboModel().getElementAt(0));
				col++;
			}
			row++;
		}
	}

	private boolean existsInAvailableChords(Chord c) {
		for (int i = 0; i < getChordComboModel().getSize(); i++)
			if (getChordComboModel().getElementAt(i).equals(c))
				return true;
		return false;
	}

	public void moveUp() {
		for (Chord r : getChordComboModel())
			r.getTone().moveUp();

	}

	public void moveDown() {
		for (Chord r : getChordComboModel())
			r.getTone().moveDown();
	}

	public Rhythm getRhythm() {
		return rhythm;
	}

	/*
	 * Clear everything if we are about to change rhythm
	 * but keep the configured chords? should not
	 * 
	 */

	public void setRhythm(Rhythm rhythm) {
		clear();
		this.rhythm = rhythm;
		fireTableStructureChanged();
		fireTableDataChanged();
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public ChordComboModel getChordComboModel() {
		return chordComboModel;
	}

	//Implementation of AbstractTableModel Abstract Class

	@Override
	public int getRowCount() {
		return data.size();
	}

	/*
	 * Add one for repeat
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */

	@Override
	public int getColumnCount() {
		return rhythm.getLength() + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < rhythm.getLength())
			return data.get(rowIndex).get(columnIndex);
		else
			return data.get(rowIndex).getRepeat();

	}

	@Override
	public Class<? extends Object> getColumnClass(int col) {
		if (data.size() > 0)
			return getValueAt(0, col).getClass();
		else
			return super.getColumnClass(col);
	}

	@Override
	public String getColumnName(int col) {

		if (col == getRhythm().getNotation().length)
			return "<html>&nbsp Repeat</html>";

		StringBuilder sb = new StringBuilder();
		sb.append("<html><font size=3><font color=#2952A3>");
		sb.append(getRhythm().getNotation()[col] == 1 ? "\u25A8" : "&nbsp");
		sb.append("</font></font>&nbsp<font size=3>");
		switch (getRhythm().getDuration()[col]) {
		case 1:
			sb.append("<font color=#000000><b><sup>1</sup>&frasl;<sub>4</sub></b></font>");
			break;
		case 2:
			sb.append("<font color=#FF0000><sup>1</sup>&frasl;<sub>8</sub></font>");
			break;
		case 3:
			sb.append("<font color=#4C8066><sup>1</sup>&frasl;<sub>16</sub></font>");
			break;
		case 4:
			sb.append("<font color=#660000><sup>1</sup>&frasl;<sub>32</sub></font>");
			break;
		}
		sb.append("</font></html>");
		return sb.toString();

	}

	/*
	 * Adjusted for repeat
	 * 
	 */

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col > getRhythm().getDuration().length)
			return false;
		return isEditable();
	}

	@Override
	public void setValueAt(Object value, int row, int col) {

		Row rowData = data.get(row);

		if (col > getRhythm().getNotation().length) {
			rowData.setRepeat((Integer) value);
		} else {
			rowData.set(col, (Chord) value);
			for (int i = col + 1; i < getRhythm().getLength(); i++) {
				if (getRhythm().getNotation()[i] == 1)
					break;
				rowData.set(i, (Chord) value);
			}
		}
		fireTableCellUpdated(row, col);
	}

	/*
	 * Implementation of LIST Interface, write/modify methods
	 */

	@Override
	public boolean add(Row rowData) {
		boolean val = data.add(rowData);
		fireTableDataChanged();
		return val;
	}

	@Override
	public Row remove(int row) {
		Row val = data.remove(row);
		fireTableDataChanged();
		return val;
	}

	@Override
	public boolean remove(Object o) {
		boolean val = data.remove(o);
		fireTableDataChanged();
		return val;
	}

	@Override
	public boolean addAll(Collection<? extends Row> c) {
		boolean val = data.addAll(c);
		fireTableDataChanged();
		return val;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Row> c) {
		boolean val = data.addAll(index, c);
		fireTableDataChanged();
		return val;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean val = data.removeAll(c);
		fireTableDataChanged();
		return val;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean val = data.retainAll(c);
		fireTableDataChanged();
		return val;
	}

	@Override
	public void clear() {
		data.clear();
		fireTableDataChanged();
	}

	@Override
	public Row set(int index, Row element) {
		Row val = data.set(index, element);
		fireTableDataChanged();
		return val;
	}

	@Override
	public void add(int index, Row element) {
		data.add(index, element);
		fireTableDataChanged();
	}

	/*
	 * List interface, read-only mehods
	 */

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return data.contains(o);
	}

	@Override
	public Iterator<Row> iterator() {
		return data.iterator();
	}

	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	@Override
	public Row get(int index) {
		return data.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return data.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return data.lastIndexOf(o);
	}

	@Override
	public ListIterator<Row> listIterator() {
		return data.listIterator();
	}

	@Override
	public ListIterator<Row> listIterator(int index) {
		return data.listIterator(index);
	}

	@Override
	public List<Row> subList(int fromIndex, int toIndex) {
		return data.subList(fromIndex, toIndex);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		try {
			this.description = new String(description.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
