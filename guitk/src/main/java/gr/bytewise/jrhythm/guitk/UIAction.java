package gr.bytewise.jrhythm.guitk;

import gr.bytewise.jrhythm.api.Event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public abstract class UIAction extends AbstractAction {

	public UIAction(String text, String icon, String desc, Integer mnemonic) {
		super(text, UI.getScaledIcon(16, 16, icon));
		putValue(SHORT_DESCRIPTION, desc);
		putValue(MNEMONIC_KEY, mnemonic);
	}

	public UIAction(String text, String desc, Integer mnemonic) {
		super(text);
		putValue(SHORT_DESCRIPTION, desc);
		putValue(MNEMONIC_KEY, mnemonic);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		action(evt);
		Event.post(new UpdateGUIEvent());
	}

	public abstract void action(ActionEvent evt);

}
