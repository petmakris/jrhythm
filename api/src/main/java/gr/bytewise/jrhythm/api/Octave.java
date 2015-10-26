package gr.bytewise.jrhythm.api;

public enum Octave {
	CUR {
		@Override
		public String toString() {
			return " ";
		}
	},
	PRE {
		@Override
		public String toString() {
			return "<";
		}
	},
	NEX {
		@Override
		public String toString() {
			return ">";
		}
	}
}
