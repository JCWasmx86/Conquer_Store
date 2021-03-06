package jcwasmx86.store.data;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import conquer.data.Shared;

public class StoreState {
	private static final String appData = Data.STORE_DATA_DIR + "/apps.json";
	private static final String installedMetaData = Data.STORE_DATA_DIR + "/installed.json";
	private final AvailableAppsState descriptors;
	private final List<URL> urls;
	private final InstalledAppsState installedApps;

	StoreState(final AppDescriptor[] descriptors, final List<URL> urls, final InstalledApp[] installedApps) {
		this.descriptors = new AvailableAppsState(Arrays.asList(descriptors));
		this.urls = urls;
		this.installedApps = new InstalledAppsState(Arrays.asList(installedApps));
	}

	public static StoreState obtain() {
		var descriptors = new AppDescriptor[0];
		try (final var fr = new FileReader(appData)) {
			descriptors = new Gson().fromJson(fr, AppDescriptor[].class);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
		}
		final var urls = Data.collectURLs();
		var installedApps = new InstalledApp[0];
		try (final var fr = new FileReader(installedMetaData)) {
			installedApps = new Gson().fromJson(fr, InstalledApp[].class);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
		}
		return new StoreState(descriptors, urls, installedApps);
	}

	public AvailableAppsState getDescriptors() {
		return descriptors;
	}

	public List<URL> getUrls() {
		return urls;
	}

	public InstalledAppsState getInstalledApps() {
		return installedApps;
	}

	public void install(final AppDescriptor descriptor, final InstallationListener listener) {
		if (this.getInstalledApps().isInstalled(descriptor.uniqueIdentifier())) {
			throw new AppInstallFailedException(descriptor.uniqueIdentifier() + " is already installed!");
		}
		final var installationProcess = new InstallationProcess(listener, descriptor, this, Shared.BASE_DIRECTORY);
		installationProcess.run();
		if (installationProcess.isFinished()) {
			this.installedApps.addAll(installationProcess.installedApps());
		}
	}

	public void uninstall(final InstalledApp app, final UninstallListener listener) {
		final var uninstallProgress = new UninstallProcess(listener, this.installedApps, app);
		uninstallProgress.run();
		if (uninstallProgress.isFinished()) {
			this.installedApps.removeAll(uninstallProgress.getRemovedApps());
		}
	}
}
