package jcwasmx86.store;

import javax.swing.JFrame;

public class StoreWindow extends JFrame {
	private static StoreWindow WINDOW = new StoreWindow();
	private boolean initialized = false;

	StoreWindow() {

	}

	public static void showWindow() {
		WINDOW.initialize();
		WINDOW.setVisible(true);
		WINDOW.toFront();
	}

	private void initialize() {
		if (this.initialized) {
			return;
		} else {
			this.setTitle(Messages.getString("store.title"));
		}
	}
}
