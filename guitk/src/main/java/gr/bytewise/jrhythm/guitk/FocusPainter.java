package gr.bytewise.jrhythm.guitk;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

public class FocusPainter extends JComponent {

	private static final long serialVersionUID = 1L;

	private Rectangle rect = null;

	public void set(Rectangle rect) {
		this.rect = rect;
	}

	public Rectangle get() {
		return rect;
	}

	public void clear() {
		rect = null;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (rect != null) {
			g.setColor(Color.RED);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
	}

}
