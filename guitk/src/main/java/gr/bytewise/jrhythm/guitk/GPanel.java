package gr.bytewise.jrhythm.guitk;

import gr.bytewise.jrhythm.api.Event;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

public class GPanel {

	protected static final long serialVersionUID = -1;

	protected JPanel panel;

	public GPanel() {
		panel = new JPanel(new GridBagLayout());
		Event.register(this);
	}

	public void add(int x, int y, int gw, int gh, double wx, double wy, int a, int f, Insets i, int px, int py, Component comp) {
		panel.add(comp, UI.gbc(x, y, gw, gh, wx, wy, a, f, i, px, py));
	}

	public void add(int x, int y, int gw, int gh, double wx, double wy, int a, int f, Insets i, Component comp) {
		panel.add(comp, UI.gbc(x, y, gw, gh, wx, wy, a, f, i, 2, 0));
	}

	public void add(int x, int y, int gw, int gh, double wx, double wy, int a, int f, Component comp) {
		panel.add(comp, UI.gbc(x, y, gw, gh, wx, wy, a, f, UI.INSETS, 2, 0));
	}

	public void add(int x, int y, int gw, int gh, double wx, double wy, int a, int f, Insets i, int px, int py, GPanel comp) {
		panel.add(comp.getPanel(), UI.gbc(x, y, gw, gh, wx, wy, a, f, i, px, py));
	}

	public void add(int x, int y, int gw, int gh, double wx, double wy, int a, int f, Insets i, GPanel comp) {
		panel.add(comp.getPanel(), UI.gbc(x, y, gw, gh, wx, wy, a, f, i, 2, 0));
	}

	public void add(int x, int y, int gw, int gh, double wx, double wy, int a, int f, GPanel comp) {
		panel.add(comp.getPanel(), UI.gbc(x, y, gw, gh, wx, wy, a, f, UI.INSETS, 2, 0));
	}

	public JPanel getPanel() {
		return panel;
	}

}
