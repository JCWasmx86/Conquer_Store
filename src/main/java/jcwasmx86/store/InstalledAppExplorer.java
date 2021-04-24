package jcwasmx86.store;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import jcwasmx86.store.data.StoreState;

public class InstalledAppExplorer extends JPanel {
	private StoreState state;
	private JPanel showPanel;

	InstalledAppExplorer(final StoreState state) {
		this.state = state;
		this.showPanel = new JPanel();
		this.showPanel.setLayout(new BoxLayout(this.showPanel, BoxLayout.Y_AXIS));
		this.initGUI();
		this.repaintPanels();
	}

	private void initGUI() {
		this.showPanel.removeAll();
		this.showPanel.revalidate();
		this.showPanel.repaint();
	}

	void setState(final StoreState state) {
		this.state = state;
		this.repaintPanels();
	}

	private void repaintPanels() {
		this.initGUI();
		this.state.getInstalledApps().stream().map(a -> new AppEntry(this.state, this.state.getDescriptor(a))).forEach(this.showPanel::add);
	}
}
