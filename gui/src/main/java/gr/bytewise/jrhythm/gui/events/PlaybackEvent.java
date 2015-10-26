package gr.bytewise.jrhythm.gui.events;

public class PlaybackEvent {

	private boolean playing;

	public PlaybackEvent(boolean playing) {
		this.setPlaying(playing);
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
}
