package gr.bytewise.jrhythm.guitk;

import javax.swing.JFrame;

public abstract class GuiTkApplicationFrame extends GFrame {

	public static boolean restartFrameOnExit = false;

	private boolean internalWindowClosingTrigger = false;

	public GuiTkApplicationFrame(JFrame main, String string, boolean shouldExit, boolean resizable, String iconName) {
		super(main, string, shouldExit, resizable, iconName);
		restartFrameOnExit = true;
	}

	public boolean isInternalWindowClosingTrigger() {
		return internalWindowClosingTrigger;
	}

	public void setInternalWindowClosingTrigger(boolean internalWindowClosingTrigger) {
		this.internalWindowClosingTrigger = internalWindowClosingTrigger;
	}

	private void controlLifecycle(boolean restart) {
		setInternalWindowClosingTrigger(restart);
		getGuiRunner().triggerLifecycle(restart);
	}

	protected void shutdownApp() {
		controlLifecycle(false);
	}

	protected void restartApp() {
		controlLifecycle(true);
	}

	protected abstract <T extends GuiTkApplicationFrame> GuiRunner<T> getGuiRunner();
	
	public interface RunnerFrameFactory<T extends GuiTkApplicationFrame> {

		public T getInstance();

	}

}
