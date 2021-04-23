package jcwasmx86.store;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jcwasmx86.store.data.AppDescriptor;
import jcwasmx86.store.data.StoreState;

public class AppEntry extends JPanel implements MouseListener {
	private final AppDescriptor descriptor;
	private final StoreState state;

	AppEntry(final StoreState state, final AppDescriptor appDescriptor) {
		this.descriptor = appDescriptor;
		this.state = state;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		final var iconLabel = new JLabel(new ImageIcon(appDescriptor.logo()));
		this.add(iconLabel);
		iconLabel.addMouseListener(this);
		final var nameLabel = new JLabel(appDescriptor.name());
		nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
		this.add(nameLabel);
		nameLabel.addMouseListener(this);
		if (state.isInstalled(appDescriptor)) {
			final var installedIcon =
				new JLabel(new ImageIcon(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(
					"store" +
						"/installed.png"))));
			installedIcon.setToolTipText(Messages.getString("store.alreadyInstalled"));
			this.add(installedIcon);
			installedIcon.addMouseListener(this);
		}
		this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		final var appInfoWindow = new AppInfoWindow(this.descriptor, this.state);
		appInfoWindow.init();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Empty
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Empty
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Empty
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Empty
	}
}
