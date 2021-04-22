package jcwasmx86.store.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import conquer.data.Shared;
import conquer.init.InitTask;

public class StoreInitTask implements InitTask {
	private static String lastUpdated = Data.STORE_DATA_DIR + "/lastUpdated";
	private static String appData = Data.STORE_DATA_DIR + "/apps.json";
	private static long SECONDS_PER_DAY = 60 * 60 * 24;

	@Override
	public void initialize() {
		final var lastUpdated = readLastUpdated();
		final var now = Instant.now().getEpochSecond();
		if (Math.abs(now - lastUpdated) > StoreInitTask.SECONDS_PER_DAY) {
			return;
		}
		final var urls = this.collectUrls();
		final var descriptors = this.downloadDescriptors(urls);
		this.saveDescriptors(descriptors);
		this.writeLastUpdated();
	}

	private void writeLastUpdated() {
		final var string = Instant.now().getEpochSecond() + "";
		try {
			Files.write(Paths.get(lastUpdated), string.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			//Shouldn't fail!
			Shared.LOGGER.exception(e);
		}
	}

	private void saveDescriptors(final List<AppDescriptor> descriptors) {
		final var string = new GsonBuilder().setPrettyPrinting().create().toJson(descriptors);
		try {
			Files.write(Paths.get(appData), string.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			//Shouldn't fail!
			Shared.LOGGER.exception(e);
		}
	}

	private List<AppDescriptor> downloadDescriptors(final List<URL> urls) {
		final var ret = new ArrayList<AppDescriptor>();
		final var gson = new Gson();
		urls.forEach(url -> {
			try {
				final var newURL = new URL(url, "index.json");
				try (Reader r = new InputStreamReader(newURL.openStream())) {
					AppDescriptor[] descriptors = gson.fromJson(r, AppDescriptor[].class);
					ret.addAll(Arrays.asList(descriptors));
				} catch (IOException e) {
					Shared.LOGGER.exception(e);
					return;
				}
			} catch (MalformedURLException e) {
				Shared.LOGGER.exception(e);
			}
		});
		return ret;
	}

	private List<URL> collectUrls() {
		final var urls = new ArrayList<URL>();
		try (final var br = new BufferedReader(new FileReader(Data.STORE_URLS_FILE))) {
			br.lines().filter(a -> !a.startsWith("#")).map(spec -> {
				try {
					return new URL(spec);
				} catch (MalformedURLException e) {
					Shared.LOGGER.exception(e);
					return null;
				}
			}).filter(a -> a != null).distinct().sorted().forEach(urls::add);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
			return List.of();
		}
		return urls;
	}

	private long readLastUpdated() {
		try {
			return Long.parseLong(new String(Files.readAllBytes(Paths.get(lastUpdated))));
		} catch (IOException e) {
			return 0;
		}
	}
}