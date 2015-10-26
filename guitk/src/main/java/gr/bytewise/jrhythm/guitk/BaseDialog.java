package gr.bytewise.jrhythm.guitk;

import static gr.bytewise.jrhythm.guitk.UIConstraints.B;
import static gr.bytewise.jrhythm.guitk.UIConstraints.CR;
import gr.bytewise.jrhythm.api.Event;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

public abstract class BaseDialog {

	protected JDialog dialog;
	protected Container parent;

	@SuppressWarnings("unused")
	private BaseDialog() {
	}

	public BaseDialog(Container frame, String title, boolean resizable) {
		parent = frame;
		Event.register(this);
		dialog = new JDialog((Frame) frame, title, true);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setLayout(new GridBagLayout());
		dialog.setResizable(resizable);
		addEscapeListener(dialog);
	}

	protected void finalizeConstruction() {
		dialog.add(configureGui(), UI.gbc(0, 0, 1, 1, 1, 1, CR, B, new Insets(10, 10, 10, 10), 0, 0));
		dialog.setPreferredSize(configureSize());
		dialog.pack();
		dialog.setMinimumSize(dialog.getSize());
	}

	protected abstract Component configureGui();

	protected Dimension configureSize() {
		return new Dimension();
	}

	public void show() {
		dialog.setLocation(UI.getLocation(parent, dialog));
		dialog.setVisible(true);
	}

	public void hide() {
		dialog.setVisible(false);
	}

	protected void showMessage(String resource, String msg) {
		UI.showMessage(dialog, msg, resource);
	}

	public static void addEscapeListener(final JDialog dialog) {
		ActionListener escListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		};
		dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

}
