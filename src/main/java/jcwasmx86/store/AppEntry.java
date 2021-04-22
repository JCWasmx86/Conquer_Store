package jcwasmx86.store;

import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jcwasmx86.store.data.AppDescriptor;
import jcwasmx86.store.data.StoreState;

public class AppEntry extends JPanel {
	private final AppDescriptor descriptor;

	AppEntry(final StoreState state, final AppDescriptor appDescriptor) {
		this.descriptor = appDescriptor;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		final var iconLabel = new JLabel(new ImageIcon(appDescriptor.logo()));
		this.add(iconLabel);
		final var nameLabel = new JLabel(appDescriptor.name());
		nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
		this.add(nameLabel);
		if (state.isInstalled(appDescriptor)) {
			final var installedIcon = new JLabel(new ImageIcon(ClassLoader.getSystemClassLoader().getResource("store" +
				"/installed.png")));
			installedIcon.setToolTipText(Messages.getString("store.alreadyInstalled"));
			this.add(installedIcon);
		}
	}
}
