package gr.bytewise.jrhythm.api;

import gr.bytewise.jrhythm.api.Rhythm;
import gr.bytewise.jrhythm.api.RhythmComboModel;

public class RhythmDefaults {

	private static final String R01 = "\u0396\u03b5\u03b9\u03bc\u03c0\u03ad\u03ba\u03b9\u03ba\u03bf \u0391";
	private static final String R02 = "\u0391\u03c0\u03c4\u03ac\u03bb\u03b9\u03ba\u03bf";

	public static int[] durationToIntArray(String durationText) {
		int[] resultArr = new int[durationText.length()];
		for (int i = 0; i < durationText.length(); i++)
			resultArr[i] = Integer.parseInt(new String(new char[] { durationText.charAt(i) }));
		return resultArr;
	}

	public static RhythmComboModel createRhythms() {
		RhythmComboModel rhythmComboModel = new RhythmComboModel();
		rhythmComboModel.add(new Rhythm(R01, durationToIntArray("12211122111"), durationToIntArray("10010100100")));
		rhythmComboModel.add(new Rhythm(R02, durationToIntArray("11112211122"), durationToIntArray("10010010100")));
		return rhythmComboModel;
	}
}
