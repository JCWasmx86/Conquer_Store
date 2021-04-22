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
		final var item = new JMenuItem();
		item.setAction(action);
		item.setText(Messages.getString("store.open"));
		return item;
	}

	@Override
	public JButton getButton() {
		final var button = new JButton(Messages.getString("store.open"));
		button.setAction(action);
		return button;
	}
}
