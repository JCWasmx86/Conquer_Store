package jcwasmx86.store;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import conquer.frontend.spi.GUIMenuPlugin;

public class StoreButton implements GUIMenuPlugin {
	private Action action = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			StoreWindow.showWindow();
		}
	};

	@Override
	public JMenuItem getMenuItem() {
		//Not needed
		return null;
	}

	@Override
	public JButton getButton() {
		final var button = new JButton();
		button.setAction(action);
		button.setText(Messages.getString("store.open"));
		return button;
	}
}
