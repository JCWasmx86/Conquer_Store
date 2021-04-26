package jcwasmx86.store.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import conquer.data.Shared;

public class Data {
	static final String STORE_DATA_DIR = Shared.BASE_DIRECTORY + "/jcwasmx86.store";
	static final String STORE_URLS_FILE = Data.STORE_DATA_DIR + "/urls";

	static {
		new File(Data.STORE_DATA_DIR).mkdirs();
	}

	static List<URL> collectURLs() {
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
			return List.of();
		}
		return urls;
	}
}
