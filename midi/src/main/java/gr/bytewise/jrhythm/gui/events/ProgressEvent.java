package gr.bytewise.jrhythm.gui.events;

public class ProgressEvent {

	private final String msg;

	public ProgressEvent(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
