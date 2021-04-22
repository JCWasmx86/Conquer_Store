package jcwasmx86.store;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import jcwasmx86.store.data.StoreState;

public class StoreWindow extends JFrame implements WindowListener {
	private static StoreWindow WINDOW = new StoreWindow();
	private boolean initialized = false;
	private StoreState state;
	private boolean firstTime = true;
	private AppExplorer appExplorer;
	private InstalledAppExplorer installedAppExplorer;

	StoreWindow() {

	}

	public static void showWindow() {
		WINDOW.initialize();
		WINDOW.setVisible(true);
		WINDOW.toFront();
	}

	private void initialize() {
		if (this.initialized) {
		} else {
			this.setTitle(Messages.getString("store.title"));
			this.state = StoreState.obtain();
			this.addWindowListener(this);
			if (this.firstTime) {
				this.buildGUI();
				this.firstTime = false;
			}
			this.appExplorer.setState(this.state);
		}
	}

	private void buildGUI() {
		final var tabbedPane = new JTabbedPane();
		this.appExplorer = new AppExplorer();
		tabbedPane.add(Messages.getString("store.explore"), this.appExplorer);
		this.installedAppExplorer = new InstalledAppExplorer();
		tabbedPane.add(Messages.getString("store.exploreInstalled"), this.installedAppExplorer);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		//Empty
	}

	@Override
	public void windowClosing(WindowEvent e) {
		//Empty
	}

	@Override
	public void windowClosed(WindowEvent e) {
		this.initialized = false;
	}

	@Override
	public void windowIconified(WindowEvent e) {
		//Empty
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		//Empty
	}

	@Override
	public void windowActivated(WindowEvent e) {
		//Empty
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		//Empty
	}
}
