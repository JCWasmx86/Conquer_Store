package jcwasmx86.store.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import conquer.data.Shared;

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
}
