package gr.bytewise.jrhythm.gui.events;

import java.awt.Point;

public class MidiPlayBackProgressEvent extends Point {

	public MidiPlayBackProgressEvent(int x, int y, int rep) {
		super(x, y);
		setRepeat(rep);
	}

	private int repeat;

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

}
