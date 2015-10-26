package gr.bytewise.jrhythm.guitk;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UI extends GridBagConstraints {

	public static Logger logger = LoggerFactory.getLogger(UI.class);

	protected static final int ICON_SIZE = 20;
	public static final Insets INSETS = new Insets(1, 1, 1, 1);

	public static GridBagConstraints gbc(int gx, int gy, int gw, int gh, double wx, double wy, int a, int f, Insets i, int px, int py) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = gx;
		g.gridy = gy;
		g.gridwidth = gw;
		g.gridheight = gh;
		g.weightx = wx;
		g.weighty = wy;
		g.anchor = a;
		g.fill = f;
		g.insets = i;
		g.ipadx = px;
		g.ipady = py;
		return g;
	}

	public static JButton newButton(String title, Object action, String icon, String tip, ActionListener listener) {
		JButton btn = new JButton();
		btn.setText(title);
		btn.setActionCommand(action.toString());
		if (icon != null)
			btn.setIcon(getScaledIcon(icon));
		btn.setToolTipText(tip);
		btn.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		btn.addActionListener(listener);
		return btn;
	}

	public static JCheckBox newCheckBox(String title, String action, boolean enabled) {
		JCheckBox check = new JCheckBox();
		check.setText(title);
		check.setEnabled(enabled);
		check.setActionCommand(action);
		return check;
	}

	public static BufferedImage getBufferedImage(String iconpath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(UI.class.getResource(iconpath));
		} catch (IOException e) {
			logger.error("Error reading " + iconpath + " (" + e.getMessage() + ")");
		}
		return image;
	}

	public static Icon getUnscaledIcon(String path) {
		return new ImageIcon(UI.class.getResource(path));
	}

	public static Icon getScaledIcon(int h, int w, String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(UI.class.getResource(path)).getScaledInstance(h, w, Image.SCALE_SMOOTH));
	}

	public static ImageIcon getScaledImageIcon(int h, int w, String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(UI.class.getResource(path)).getScaledInstance(h, w, Image.SCALE_SMOOTH));
	}

	public static Icon getScaledIcon(String path) {
		return getScaledIcon(ICON_SIZE, ICON_SIZE, path);
	}

	public static Icon getIcon(String path) {
		return new ImageIcon(UI.class.getResource(path));
	}

	public static void configureSize(Component... components) {
		Dimension max = new Dimension();
		for (Component comp : components)
			if (comp.getPreferredSize().height >= max.height && comp.getPreferredSize().width >= max.width)
				max.setSize(comp.getPreferredSize());
		for (Component comp : components)
			comp.setPreferredSize(max);
	}

	public static Point getLocation(Component parent, Component child) {
		Point p = new Point();
		if (parent == null)
			return p;
		p.x = Math.abs(parent.getSize().width - child.getSize().width) / 2;
		p.y = Math.abs(parent.getSize().height - child.getSize().height) / 2;
		p.x += parent.getLocation().x;
		p.y += parent.getLocation().y;
		return p;
	}

	public static String bold(Object obj) {
		return "<html><b>" + obj + "</b></html>";
	}

	public static String boldc(String string) {
		return "<html><h2><center>" + string + "</center></h2></html>";
	}

	public static Border getBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title, TitledBorder.LEADING, TitledBorder.TOP);
	}

	public static Point getComponentLocation(JFrame parent, Component component) {
		if (component == null)
			return null;
		Point pointp = new Point();
		pointp.x = component.getLocation().x;
		pointp.y = component.getLocation().y;
		Component temp = component;
		int blowfuse = 0;
		while (temp != parent.getContentPane()) {
			temp = temp.getParent();
			pointp.x += temp.getLocation().x;
			pointp.y += temp.getLocation().y;
			if (blowfuse++ == 20)
				return null;
		}
		return pointp;
	}

	public static boolean showConfirmDialog(Component comp, String msg, String iconName) {
		logger.trace("showConfirmDialog: [{}]", msg);
		return JOptionPane.showOptionDialog(comp,
				msg,
				"Rhythm Guitar",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				UI.getScaledIcon(128, 128, iconName), null, null) == JOptionPane.OK_OPTION;
	}

	public static void showMessage(Component comp, String msg, String iconName) {
		logger.trace("showMessage: [{}]", msg);
		JOptionPane.showMessageDialog(comp, msg,
				"Rhyth Guitar",
				JOptionPane.PLAIN_MESSAGE,
				UI.getUnscaledIcon(iconName));
	}

}
