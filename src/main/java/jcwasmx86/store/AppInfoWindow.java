package jcwasmx86.store;

import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jcwasmx86.store.data.AppDescriptor;
import jcwasmx86.store.data.StoreState;

public class AppInfoWindow extends JFrame {
	private final AppDescriptor descriptor;
	private final StoreState state;

	AppInfoWindow(AppDescriptor descriptor, StoreState state) {
		this.descriptor = descriptor;
		this.state = state;
	}

	void init() {
		this.setTitle(this.descriptor.name());
		final var topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		topPanel.add(new JLabel(new ImageIcon(this.descriptor.logo())));
		final var nameLabel = new JLabel(this.descriptor.name());
		nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
		topPanel.add(nameLabel);
		final var buttonPanel = this.buildButtonPanel();
		this.add(topPanel);
		this.setVisible(true);
	}

	private JPanel buildButtonPanel() {
		final var panel = new JPanel();
		if (this.state.isInstalled(this.descriptor)) {
			final var button = new JButton(Messages.getString("store.uninstall"));
			button.addActionListener(e -> {
				state.uninstall(AppInfoWindow.this.descriptor);
				state.serialize();
			});
		} else {
			final var button = new JButton(Messages.getString("store.install"));
			button.addActionListener(e -> {
				state.install(AppInfoWindow.this.descriptor);
				state.serialize();
			});
		}
		return panel;
	}
}
