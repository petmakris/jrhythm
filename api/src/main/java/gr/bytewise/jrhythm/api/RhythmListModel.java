package gr.bytewise.jrhythm.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractListModel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RhythmListModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class RhythmListModel extends AbstractListModel<Rhythm> implements List<Rhythm> {

	@XmlElementWrapper(name = "RhythmList")
	@XmlElement(name = "Rhythm")
	protected List<Rhythm> chords;

	public RhythmListModel() {
		chords = new CopyOnWriteArrayList<Rhythm>();
	}

	@Override
	public int getSize() {
		return chords.size();
	}

	@Override
	public Rhythm getElementAt(int index) {
		if (index >= 0 && index < chords.size())
			return chords.get(index);
		else
			return null;
	}

	public int getIndexOf(Object anObject) {
		return chords.indexOf(anObject);
	}

	public void removeAllElements() {
		if (chords.size() > 0) {
			int firstIndex = 0;
			int lastIndex = chords.size() - 1;
			chords.clear();
			clearSelectedValue();
			fireIntervalRemoved(this, firstIndex, lastIndex);
		} else {
			clearSelectedValue();
		}
	}

	protected void clearSelectedValue() {
	}

	// Implementation of the List interface

	@Override
	public int size() {
		return chords.size();
	}

	@Override
	public boolean isEmpty() {
		return chords.size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return chords.contains(o);
	}

	@Override
	public Iterator<Rhythm> iterator() {
		return chords.iterator();
	}

	@Override
	public Object[] toArray() {
		return chords.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return chords.toArray(a);
	}

	@Override
	public boolean add(Rhythm e) {
		boolean v = chords.add(e);
		fireIntervalAdded(this, 0, size() - 1);
		update();
		return v;
	}

	@Override
	public boolean remove(Object o) {
		int i = chords.indexOf(o);
		boolean v = chords.remove(o);
		fireIntervalRemoved(this, i, i);
		update();
		return v;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return chords.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Rhythm> c) {
		boolean v = chords.addAll(c);
		update();
		return v;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Rhythm> c) {
		boolean v = chords.addAll(index, c);
		update();
		return v;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean v = chords.removeAll(c);
		update();
		return v;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean v = chords.retainAll(c);
		update();
		return v;
	}

	@Override
	public void clear() {
		removeAllElements();
	}

	@Override
	public Rhythm get(int index) {
		return chords.get(index);
	}

	@Override
	public Rhythm set(int index, Rhythm element) {
		Rhythm c = chords.set(index, element);
		update();
		return c;
	}

	@Override
	public void add(int index, Rhythm element) {
		chords.add(index, element);
		fireIntervalAdded(this, -1, size());
		update();

	}

	@Override
	public Rhythm remove(int index) {
		Rhythm c = chords.remove(index);
		fireIntervalRemoved(c, index, index);
		update();
		return c;
	}

	@Override
	public int indexOf(Object o) {
		return chords.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return chords.lastIndexOf(o);
	}

	@Override
	public ListIterator<Rhythm> listIterator() {
		return chords.listIterator();
	}

	@Override
	public ListIterator<Rhythm> listIterator(int index) {
		return chords.listIterator(index);
	}

	@Override
	public List<Rhythm> subList(int fromIndex, int toIndex) {
		return chords.subList(fromIndex, toIndex);
	}

	public void update() {
		fireContentsChanged(this, -1, getSize());
	}

}