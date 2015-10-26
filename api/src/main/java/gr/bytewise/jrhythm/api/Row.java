package gr.bytewise.jrhythm.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Row")
@XmlAccessorType(XmlAccessType.FIELD)
public class Row implements List<Chord> {

	@XmlElementWrapper(name = "Columns")
	@XmlElement(name = "Chord")
	private List<Chord> chords;

	@XmlAttribute
	private Integer repeat;

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public Integer getRepeat() {
		return this.repeat;
	}

	public Row() {
		this.chords = new CopyOnWriteArrayList<Chord>();
		setRepeat(1);
	}

	public Row(int repeat) {
		this.chords = new CopyOnWriteArrayList<Chord>();
		setRepeat(repeat);
	}

	public void setupFromChord(Chord chord, Rhythm rhythm) {
		for (int i : rhythm.getDuration()) {
			chords.add(chord);
		}
	}

	@Override
	public int size() {
		return chords.size();
	}

	@Override
	public boolean isEmpty() {
		return chords.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return chords.contains(o);
	}

	@Override
	public Iterator<Chord> iterator() {
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
	public boolean add(Chord e) {
		return chords.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return chords.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return chords.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Chord> c) {
		return chords.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Chord> c) {
		return chords.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return chords.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return chords.retainAll(c);
	}

	@Override
	public void clear() {
		chords.clear();
	}

	@Override
	public Chord get(int index) {
		return chords.get(index);
	}

	@Override
	public Chord set(int index, Chord element) {
		return chords.set(index, element);
	}

	@Override
	public void add(int index, Chord element) {
		chords.add(index, element);
	}

	@Override
	public Chord remove(int index) {
		return chords.remove(index);
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
	public ListIterator<Chord> listIterator() {
		return chords.listIterator();
	}

	@Override
	public ListIterator<Chord> listIterator(int index) {
		return chords.listIterator(index);
	}

	@Override
	public List<Chord> subList(int fromIndex, int toIndex) {
		return chords.subList(fromIndex, toIndex);
	}
}
