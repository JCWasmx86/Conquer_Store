package jcwasmx86.store;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jcwasmx86.store.data.SearchEngine;
import jcwasmx86.store.data.StoreState;

public class AppExplorer extends JPanel implements KeyListener {
	private StoreState state;
	private JPanel searchPanel;
	private JPanel appPanel;
	private JTextField textField;

	AppExplorer() {
		this.searchPanel = new JPanel();
		this.appPanel = new JPanel();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.searchPanel.setLayout(new BoxLayout(this.searchPanel, BoxLayout.X_AXIS));
		this.textField = new JTextField(15);
		this.textField.addKeyListener(this);
		final var searchLabel = new JLabel(Messages.getString("store.search"));
		this.searchPanel.add(searchLabel);
		this.searchPanel.add(this.textField);
		this.add(this.searchPanel);
		this.appPanel.setLayout(new BoxLayout(this.appPanel, BoxLayout.Y_AXIS));
		this.add(new JScrollPane(this.appPanel));
	}

	void setState(final StoreState state) {
		this.state = state;
		this.rebuildGUI();
	}

	private void rebuildGUI() {
		this.cleanGUI();
		if (this.state.getDescriptors().isEmpty()) {
			final var jl = new JLabel(Messages.getString("store.nothingHere"));
			this.appPanel.add(jl);
		} else {
			this.state.getDescriptors().stream().map(a -> new AppEntry(this.state, a)).forEach(this.appPanel::add);
		}
	}

	private void cleanGUI() {
		this.appPanel.removeAll();
		this.appPanel.revalidate();
		this.appPanel.repaint();
	}

	private void showSearchEntries(final String query) {
		this.cleanGUI();
		final var searcher = new SearchEngine(this.state, query.trim());
		final var predicates = searcher.parse();
		var stream = this.state.getDescriptors().stream();
		for (final var p : predicates) {
			stream = stream.filter(p);
		}
		final var resultList = stream.toList();
		if (!resultList.isEmpty()) {
			resultList.stream().map(a -> new AppEntry(this.state, a)).forEach(this.appPanel::add);
		} else {
			final var label = new JLabel();
			label.setText(Messages.getString("store.nothingHere"));
			this.appPanel.add(label);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		this.showSearchEntries(this.textField.getText());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.showSearchEntries(this.textField.getText());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.showSearchEntries(this.textField.getText());
	}
}
