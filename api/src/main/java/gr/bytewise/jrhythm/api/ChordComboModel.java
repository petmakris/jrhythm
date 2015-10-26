package gr.bytewise.jrhythm.api;

import javax.swing.MutableComboBoxModel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ChordComboModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChordComboModel extends ChordListModel implements MutableComboBoxModel<Chord> {

	@XmlElement(name = "SelectedChord")
	protected Chord selected;

	@Override
	protected void clearSelectedValue() {
		selected = null;
	}

	@Override
	public void setSelectedItem(Object anChord) {
		if (selected != null && !selected.equals(anChord) || selected == null && anChord != null) {
			selected = (Chord) anChord;
			fireContentsChanged(this, -1, -1);
		}
	}

	@Override
	public Chord getSelectedItem() {
		return selected;
	}

	@Override
	public void addElement(Chord anChord) {
		chords.add((Chord) anChord);
		fireIntervalAdded(this, chords.size() - 1, chords.size() - 1);
		if (chords.size() == 1 && selected == null && anChord != null) {
			setSelectedItem(anChord);
		}
	}

	@Override
	public void insertElementAt(Chord anChord, int index) {
		chords.add(index, (Chord) anChord);
		fireIntervalAdded(this, index, index);
	}

	@Override
	public void removeElementAt(int index) {
		if (getElementAt(index) == selected) {
			if (index == 0) {
				setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
			} else {
				setSelectedItem(getElementAt(index - 1));
			}
		}

		chords.remove(index);
		fireIntervalRemoved(this, index, index);
	}

	@Override
	public void removeElement(Object anChord) {
		int index = chords.indexOf(anChord);
		if (index != -1) {
			removeElementAt(index);
		}
	}

}
