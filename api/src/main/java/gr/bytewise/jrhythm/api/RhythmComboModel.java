package gr.bytewise.jrhythm.api;

import javax.swing.MutableComboBoxModel;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RhythmComboModel")
public class RhythmComboModel extends RhythmListModel implements MutableComboBoxModel<Rhythm> {

	@XmlElement(name = "SelectedRhythm")
	protected Rhythm selected;

	@Override
	protected void clearSelectedValue() {
		selected = null;
	}

	@Override
	public void setSelectedItem(Object anRhythm) {
		if (selected != null && !selected.equals(anRhythm) || selected == null && anRhythm != null) {
			selected = (Rhythm) anRhythm;
			fireContentsChanged(this, -1, -1);
		}
	}

	@Override
	public Rhythm getSelectedItem() {
		return selected;
	}

	@Override
	public void addElement(Rhythm anRhythm) {
		chords.add((Rhythm) anRhythm);
		fireIntervalAdded(this, chords.size() - 1, chords.size() - 1);
		if (chords.size() == 1 && selected == null && anRhythm != null) {
			setSelectedItem(anRhythm);
		}
	}

	@Override
	public void insertElementAt(Rhythm anRhythm, int index) {
		chords.add(index, (Rhythm) anRhythm);
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
	public void removeElement(Object anRhythm) {
		int index = chords.indexOf(anRhythm);
		if (index != -1) {
			removeElementAt(index);
		}
	}

}
