package gr.bytewise.jrhythm.guitk;

import gr.bytewise.jrhythm.guitk.GuiTkApplicationFrame.RunnerFrameFactory;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GuiRunner<T extends GuiTkApplicationFrame> {

	private static final Object applicationLock = new Object();
	private static volatile boolean applicationLockStatus = true;
	private static boolean applicationEnabled = true;
	private RunnerFrameFactory<T> runnerFrameFactory;

	public GuiRunner(RunnerFrameFactory<T> factory) {
		this.runnerFrameFactory = factory;
	}

	public void run() {

		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		while (applicationEnabled) {

			LookAndFeelManager.updateUI();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					runnerFrameFactory.getInstance().getFrame().setVisible(true);
				}
			});

			while (applicationLockStatus) {
				synchronized (applicationLock) {
					try {
						applicationLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			applicationEnabled = GuiTkApplicationFrame.restartFrameOnExit;
			applicationLockStatus = true;
		}
		System.exit(0);
	}

	public synchronized void triggerLifecycle(boolean restart) {
		GuiTkApplicationFrame.restartFrameOnExit = restart;
		synchronized (applicationLock) {
			GuiRunner.applicationLockStatus = false;
			GuiRunner.applicationLock.notifyAll();
		}
	}

}
