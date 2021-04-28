package jcwasmx86.store.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import conquer.data.Shared;
import conquer.init.Initializer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WholeTest implements InstallationListener {
	private static WebServer server;

	@BeforeClass
	public static void setupEverything() {
		WholeTest.resetup();
		server = new WebServer();
		new Thread(server).start();
		System.setProperty("jcwasmx86.store.force", "true");
		Initializer.INSTANCE().initialize(null);
	}

	private static void resetup() {
		final var base = new File(Shared.BASE_DIRECTORY);
		if (base.exists()) {
			try {
				WholeTest.delDir(base);
			} catch (IOException e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
				return;
			}
		}
		base.mkdirs();
		final var dirBase = new File(base, "jcwasmx86.store").getAbsolutePath();
		new File(dirBase).mkdirs();
		try {
			Files.writeString(Paths.get(dirBase, "installed.json"), "[]",
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			Files.writeString(Paths.get(dirBase, "apps.json"), "[]",
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			Files.writeString(Paths.get(dirBase, "urls"), "http://localhost:32451",
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	static void delDir(File dir) throws IOException {
		Path path = Paths.get(dir.toURI());
		try (Stream<Path> walk = Files.walk(path)) {
			walk.sorted(Comparator.reverseOrder())
				.forEach(WholeTest::delDir);
		}

	}

	public static void delDir(Path path) {
		try {
			Files.delete(path);
		} catch (IOException e) {
			System.err.printf("Unable to delete this path : %s%n%s", path, e);
		}
	}

	@AfterClass
	public static void end() {
		server.stop();
	}

	@Test
	public void runTests() {
		final var state = StoreState.obtain();
		state.install(state.getDescriptors().forName("0"), new InstallationListener() {
			@Override
			public boolean onAppsCollected(Set<AppDescriptor> toInstall) {
				return true;
			}

			@Override
			public void onDownload(AppDescriptor downloaded, int number, int maximum) {
				System.out.println(number + "//" + maximum);
			}

			@Override
			public void afterCheckingChecksum(AppDescriptor checked, int number, int maximum) {

			}

			@Override
			public void afterExtracting(AppDescriptor from, int numberOfFile, int numberOfFiles) {

			}

			@Override
			public void copy(String fileName, int numberOfFile, int numberOfFiles) {

			}
		});
		this.tryInstallingTwice(state);
		this.tryUninstalling(state);
		this.tryUninstallingTwice(state);
	}

	private void tryUninstallingTwice(StoreState state) {
		try {
			this.tryUninstalling(state);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		Assert.fail("Could uninstall \"0\" twice!");
	}

	private void tryInstallingTwice(final StoreState state) {
		try {
			state.install(state.getDescriptors().forName("0"), this);
		} catch (final AppInstallFailedException aife) {
			//Expected
			System.out.println(aife.getMessage());
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.fail("Could install \"0\" twice!");
	}

	private void tryUninstalling(final StoreState state) {
		state.uninstall(state.getInstalledApps().getForName("0"), new UninstallListener() {
			@Override
			public boolean onAppsCollected(Set<InstalledApp> appsToRemove) {
				return true;
			}

			@Override
			public void deletingFile(String fileName, int number, int numberOfFiles) {

			}
		});
	}

	@Override
	public boolean onAppsCollected(Set<AppDescriptor> toInstall) {
		return true;
	}

	@Override
	public void onDownload(AppDescriptor downloaded, int number, int maximum) {

	}

	@Override
	public void afterCheckingChecksum(AppDescriptor checked, int number, int maximum) {

	}

	@Override
	public void afterExtracting(AppDescriptor from, int numberOfFile, int numberOfFiles) {

	}

	@Override
	public void copy(String fileName, int numberOfFile, int numberOfFiles) {

	}

	private static class WebServer implements Runnable, HttpHandler {

		private final HttpServer server;
		private boolean run;

		WebServer() {
			try {
				server = HttpServer.create(new InetSocketAddress(32451), 0);
				server.createContext("/index.json", this);
				for (var i = 0; i < 16; i++) {
					System.out.println("/_res/" + i + ".zip");
					server.createContext("/_res/" + i + ".zip", this);
				}
			} catch (IOException e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
				throw null;
			}
			server.setExecutor(null);
		}

		@Override
		public void run() {
			server.start();
		}

		void stop() {
			//server.stop(0);
		}

		@Override
		public void handle(HttpExchange t) throws IOException {
			final var uri = t.getRequestURI().toString();
			final var inputStream = this.getClass().getResourceAsStream(uri);
			final var bytes = Objects.requireNonNull(inputStream).readAllBytes();
			inputStream.close();
			final var s = new String(bytes);
			t.sendResponseHeaders(200, bytes.length);
			OutputStream os = t.getResponseBody();
			os.write(bytes);
			os.flush();
			os.close();
			t.close();
		}
	}
}
