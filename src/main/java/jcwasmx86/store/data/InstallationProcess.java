package jcwasmx86.store.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import conquer.data.Shared;
import jcwasmx86.store.data.InstalledApp.InstalledFile;

public final class InstallationProcess implements Runnable {
	private final InstallationListener listener;
	private final AppDescriptor toInstall;
	private final StoreState state;
	private final String outputDirectory;
	private boolean finished;
	private Set<InstalledApp> installedApps;

	public InstallationProcess(InstallationListener listener, AppDescriptor toInstall,
							   StoreState state, String outputDirectory) {
		this.listener = listener;
		this.toInstall = toInstall;
		this.state = state;
		this.outputDirectory = outputDirectory;
		this.installedApps = new HashSet<>();
	}

	@Override
	public void run() {
		final var startTime = System.nanoTime();
		final var resolver = new InstallDependencyResolver(toInstall, state.getDescriptors(),
			state.getInstalledApps());
		final var toInstall = resolver.appsToInstall();
		if (!this.listener.onAppsCollected(toInstall)) {
			return;
		}
		final var downloadFolder = this.makeDownloadFolder(startTime);
		final var files = this.downloadFiles(toInstall, downloadFolder);
		this.checkChecksums(files);
		final var extractingDirectory = this.makeDownloadFolder(startTime + 1);
		this.extract(extractingDirectory, files);
		this.copyFilesIntoFinalDestination(extractingDirectory);
		this.finished = true;
	}

	private void copyFilesIntoFinalDestination(final File source) {
		final var files = new HashSet<File>();
		this.collectFiles(source, files);
		var cnter = 0;
		final var baseDir = Paths.get(source.getAbsolutePath());
		for (var file : files) {
			final var sourceFile = Paths.get(file.getAbsolutePath());
			final var relativized = baseDir.relativize(sourceFile);
			final var f = relativized.toString();
			final var destination = new File(Shared.BASE_DIRECTORY, f);
			final var parent = destination.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			try {
				Files.copy(Paths.get(sourceFile.toUri()), Paths.get(destination.toURI()),
					StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e) {
				throw new AppInstallFailedException(e);
			}
			this.listener.copy(relativized.toString(), cnter + 1, files.size());
			cnter++;
		}
	}

	private void collectFiles(final File source, final Set<File> files) {
		if (source.isFile()) {
			files.add(source.getAbsoluteFile());
		} else {
			final var children = source.listFiles();
			if (children == null) {
				return;
			}
			for (final var child : children) {
				if (child.isFile()) {
					files.add(child.getAbsoluteFile());
				} else {
					this.collectFiles(child, files);
				}
			}
		}
	}

	private void extract(File extractingDirectory, Map<AppDescriptor, File> files) {
		var cnter = 0;
		for (final var entry : files.entrySet()) {
			final var set = new HashSet<InstalledFile>();
			final var file = entry.getValue();
			final var descriptor = entry.getKey();
			this.extractZip(extractingDirectory, file, set);
			this.listener.afterExtracting(descriptor, cnter, files.size());
			cnter++;
			this.installedApps.add(new InstalledApp(descriptor.name(), descriptor.uniqueIdentifier(),
				set.toArray(new InstalledFile[0]), descriptor == this.toInstall,
				Arrays.copyOf(descriptor.dependencies(), descriptor.dependencies().length), descriptor.version()));
		}
	}

	private void extractZip(File extractingDirectory, File file, Set<InstalledFile> set) {
		try (ZipFile zis = new ZipFile(file)) {
			var entries = zis.entries();
			while (entries.hasMoreElements()) {
				final var entry = entries.nextElement();
				set.add(new InstalledFile(entry.getName()));
				final var outputFile = new File(extractingDirectory, entry.getName());
				if (outputFile.exists()) {
					throw new AppInstallFailedException(outputFile + " already exists!");
				} else if (new File(this.outputDirectory, entry.getName()).exists()) {
					throw new AppInstallFailedException(entry.getName() + " already exists in target directory");
				}
				outputFile.getParentFile().mkdirs();
				try (final var in = zis.getInputStream(entry)) {
					Files.copy(in, Paths.get(outputFile.toURI()),
						StandardCopyOption.COPY_ATTRIBUTES);
				}
			}
		} catch (IOException e) {
			throw new AppInstallFailedException(e);
		}
	}

	private void checkChecksums(Map<AppDescriptor, File> files) {
		var cnter = 0;
		for (final var entry : files.entrySet()) {
			final var file = entry.getValue();
			try {
				final var bytes = Files.readAllBytes(Paths.get(file.toURI()));
				entry.getKey().hashes().validate(bytes, entry.getKey().uniqueIdentifier());
				listener.afterCheckingChecksum(entry.getKey(), cnter + 1, files.size());
			} catch (IOException e) {
				throw new AppInstallFailedException(e);
			}
			cnter++;
		}
	}

	private Map<AppDescriptor, File> downloadFiles(final Set<AppDescriptor> toInstall, final File downloadFolder) {
		final var map = new HashMap<AppDescriptor, File>();
		var cnter = 0;
		for (final var app : toInstall) {
			try (final var in = app.downloadBundle().openStream()) {
				final var file = new File(downloadFolder, app.uniqueIdentifier() + "__.zip");
				Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
				this.listener.onDownload(app, cnter + 1, toInstall.size());
				map.put(app, file);
			} catch (IOException e) {
				throw new AppInstallFailedException(e);
			}
			cnter++;
		}
		return map;
	}

	private File makeDownloadFolder(final long startTime) {
		try {
			return Files.createTempDirectory("starttime_" + startTime).toFile();
		} catch (IOException e) {
			throw new AppInstallFailedException(e);
		}
	}

	public boolean isFinished() {
		return finished;
	}

	public Set<InstalledApp> installedApps() {
		if (this.finished) {
			return this.installedApps;
		} else {
			throw new UnsupportedOperationException("Not finished yet!");
		}
	}
}
