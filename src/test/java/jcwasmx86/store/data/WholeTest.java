package jcwasmx86.store.data;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import conquer.init.Initializer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WholeTest {
	private static WebServer server;

	@BeforeClass
	public static void setupEverything() {
		server = new WebServer();
		new Thread(server).start();
		System.setProperty("jcwasmx86.store.force", "true");
		Initializer.INSTANCE().initialize(null);
	}

	@AfterClass
	public static void end() {
		server.stop();
	}

	@Test
	public void initialize() {
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
	}

	private static class WebServer implements Runnable, HttpHandler {

		private final HttpServer server;
		private boolean run;

		WebServer() {
			try {
				server = HttpServer.create(new InetSocketAddress(32451), 0);
				server.createContext("/index.json", this);
				for (var i = 0; i < 16; i++) {
					System.out.println("/_res/"+i+".zip");
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
			final var uri = t.getRequestURI().toString().substring(0);
			final var inputStream = this.getClass().getResourceAsStream(uri);
			final var bytes = inputStream.readAllBytes();
			inputStream.close();
			final var s = new String(bytes);
			System.out.println(s);
			t.sendResponseHeaders(200, bytes.length);
			OutputStream os = t.getResponseBody();
			os.write(bytes);
			os.flush();
			os.close();
			t.close();
		}
	}
}
