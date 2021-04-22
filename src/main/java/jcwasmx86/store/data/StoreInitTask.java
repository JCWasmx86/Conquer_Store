package jcwasmx86.store.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import conquer.data.Shared;
import conquer.init.InitTask;

public class StoreInitTask implements InitTask {
	private static String lastUpdated = Data.STORE_DATA_DIR + "/lastUpdated";
	private static long SECONDS_PER_DAY = 60 * 60 * 24;

	@Override
	public void initialize() {
		final var lastUpdated = readLastUpdated();
		final var now = Instant.now().getEpochSecond();
		if (Math.abs(now - lastUpdated) > StoreInitTask.SECONDS_PER_DAY) {
			return;
		}
		final var urls = this.collectUrls();
		final var descriptors = new ArrayList<AppDescriptor>();
		for (final var url : urls) {

		}
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
