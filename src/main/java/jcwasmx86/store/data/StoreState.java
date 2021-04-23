package jcwasmx86.store.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import conquer.data.Shared;
import jcwasmx86.store.data.InstalledApp.InstalledFile;

public class StoreState {
	private static String appData = Data.STORE_DATA_DIR + "/apps.json";
	private static String installedMetaData = Data.STORE_DATA_DIR + "/installed.json";
	private final List<AppDescriptor> descriptors;
	private final List<URL> urls;
	private final List<InstalledApp> installedApps;

	StoreState(final AppDescriptor[] descriptors, final List<URL> urls, final InstalledApp[] installedApps) {
		this.descriptors = Arrays.asList(descriptors);
		this.urls = urls;
		this.installedApps = Arrays.asList(installedApps);
	}

	public static StoreState obtain() {
		AppDescriptor[] descriptors = new AppDescriptor[0];
		try (final var fr = new FileReader(appData)) {
			descriptors = new Gson().fromJson(fr, AppDescriptor[].class);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
		}
		final var urls = new ArrayList<URL>();
		try (final var br = new BufferedReader(new FileReader(Data.STORE_URLS_FILE))) {
			br.lines().filter(a -> !a.startsWith("#")).map(spec -> {
				try {
					return new URL(spec);
				} catch (MalformedURLException e) {
					Shared.LOGGER.exception(e);
					return null;
				}
			}).filter(Objects::nonNull).distinct().sorted().forEach(urls::add);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
		}
		InstalledApp[] installedApps = new InstalledApp[0];
		try (final var fr = new FileReader(installedMetaData)) {
			installedApps = new Gson().fromJson(fr, InstalledApp[].class);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
		}
		return new StoreState(descriptors, urls, installedApps);
	}

	public List<AppDescriptor> getDescriptors() {
		return descriptors;
	}

	public List<URL> getUrls() {
		return urls;
	}

	public List<InstalledApp> getInstalledApps() {
		return installedApps;
	}

	public boolean isInstalled(AppDescriptor appDescriptor) {
		return this.installedApps.stream().filter(a -> a.uniqueIdentifier().equals(appDescriptor.uniqueIdentifier())).count() == 1;
	}

	public void uninstall(final AppDescriptor descriptor) {
		if (this.isInstalled(descriptor)) {
			return;
		} else {
			final var appHandle =
				this.installedApps.stream().filter(a -> a.uniqueIdentifier().equals(descriptor.uniqueIdentifier())).findFirst().orElseGet(null);
			if (appHandle == null) {
				return;
			}
			this.uninstall(appHandle);
		}
	}

	private void uninstall(InstalledApp appHandle) {
		//Remove the app itself
		Arrays.stream(appHandle.files()).forEach(InstalledFile::delete);
		this.installedApps.remove(appHandle);
		//Get all dependencies (apt autoremove-like)
		final var deps = appHandle.dependencies();
		for (final var dep : deps) {
			//For each dependency, check whether another app requires it.
			//If yes, don't do anything, else
			final var hasOtherDependencies =
				this.installedApps.stream().filter(a -> a.uniqueIdentifier().equals(dep)).filter(a -> !a.explicitlyInstalled()).filter(a -> {
					return this.installedApps.stream().filter(b -> Arrays.stream(b.dependencies()).filter(c -> c.equals(a.uniqueIdentifier())).count() == 1).count() == 0;
				}).count() != 0;
			if (!hasOtherDependencies) {
				final var ia =
					this.installedApps.stream().filter(a -> a.uniqueIdentifier().equals(dep)).findFirst().get();
				this.uninstall(ia);
			}
		}
	}

	public void serialize() {
		final var s =
			new GsonBuilder().setPrettyPrinting().create().toJson(this.installedApps.toArray(new InstalledApp[0]));
		try {
			Files.writeString(Paths.get(installedMetaData), s, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
		}
	}
}
