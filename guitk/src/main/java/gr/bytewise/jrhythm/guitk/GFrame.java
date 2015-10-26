package gr.bytewise.jrhythm.guitk;

import static gr.bytewise.jrhythm.guitk.UIConstraints.B;
import static gr.bytewise.jrhythm.guitk.UIConstraints.CR;
import gr.bytewise.jrhythm.api.Event;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public abstract class GFrame {

	protected JFrame frame;
	protected JFrame mainFrame;

	private boolean resizable = false;

	public GFrame(JFrame main, String string, boolean shouldExit, boolean resizable, String iconName) {
		frame = new JFrame(string);
		this.mainFrame = main;
		frame.setDefaultCloseOperation(shouldExit ? JFrame.EXIT_ON_CLOSE : JFrame.HIDE_ON_CLOSE);
		this.resizable = resizable;
		frame.setIconImage(UI.getBufferedImage(iconName));
	}

	public void finalizeConstruction() {
		frame.setContentPane(configureMainContentPanel().getPanel());
		frame.setJMenuBar(configureMenu());
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		frame.setResizable(resizable);
		if (mainFrame != null)
			frame.setLocation(UI.getLocation(mainFrame, frame));
		Event.register(this);
	}

	private GPanel configureMainContentPanel() {
		GPanel panel = new GPanel();
		panel.add(0, 0, 1, 1, 1, 1, CR, B, configureGui().getPanel());
		return panel;
	}

	protected abstract GPanel configureGui();

	protected abstract JMenuBar configureMenu();

	public JFrame getFrame() {
		return frame;
	}

	public void configureGuiSizeAndLocation() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		int xpos = Math.abs(width - frame.getSize().width) / 2;
		int ypos = Math.abs(height - frame.getSize().height) / 2;
		frame.setLocation(xpos, ypos);
	}

	protected void showMessage(String resource, String msg) {
		UI.showMessage(frame, msg, resource);
	}

	protected void showMessage(String resource, Throwable t) {
		showMessage(resource, "Error occured:", t);
	}

	protected void showMessage(String resource, String msg, Throwable t) {
		UI.showMessage(frame, msg + "\n\n" + t.getMessage(), resource);
		t.printStackTrace();
	}

	protected boolean showConfirmDialog(String resource, String msg) {
		return UI.showConfirmDialog(frame, msg, resource);
	}

}
