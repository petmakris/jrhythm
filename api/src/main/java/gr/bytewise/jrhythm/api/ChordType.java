package gr.bytewise.jrhythm.api;

public enum ChordType {

	MAJOR(new int[] { 0, 4, 7 }, "+"),
	MINOR(new int[] { 0, 3, 7 }, "-"),
	DIM_KEYS(new int[] { 0, 3, 6 }, "dim"),
	AUG_KEYS(new int[] { 0, 4, 8 }, "aug"),
	MAJ7_KEYS(new int[] { 0, 4, 11 }, "7"),
	MIN7_KEYS(new int[] { 0, 3, 11 }, "7-"),
	MIN6_KEYS(new int[] { 0, 3, 7, 9 }, "6-");

	private final int[] keys;
	private final String svalue;

	ChordType(int[] k, String ret) {
		keys = new int[k.length];
		for (int i = 0; i < k.length; i++)
			keys[i] = k[i];
		this.svalue = ret;
	}

	@Override
	public String toString() {
		return svalue;
	}

	public int[] getKeys() {
		return keys;
	}

}
