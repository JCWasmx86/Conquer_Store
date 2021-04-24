package jcwasmx86.store.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import conquer.data.Shared;
import jcwasmx86.store.data.InstalledApp.InstalledFile;

public class AppInstallerTask {
	private final AppDescriptor descriptor;

	AppInstallerTask(AppDescriptor a) {
		this.descriptor = a;
	}

	public InstalledApp install(final boolean explicit) {
		final var file = this.downloadFile();
		this.checkHashes(file);
		final var installedFiles = this.extractFiles(file);
		return new InstalledApp(descriptor.name(), descriptor.uniqueIdentifier(),
			installedFiles.stream().map(InstalledFile::new).toList().toArray(new InstalledFile[0]), explicit,
			Arrays.copyOf(this.descriptor.dependencies(), this.descriptor.dependencies().length));
	}

	private List<String> extractFiles(final File file) {
		final List<String> ret = new ArrayList<>();
		try (final var zip = new ZipInputStream(new FileInputStream(file))) {
			ZipEntry ze;
			while ((ze = zip.getNextEntry()) != null) {
				final var name = ze.getName();
				if (name.contains("..")) {
					throw new AppInstallFailedException("ZipEntry in " + file.getName() + " contained ..! Aborting " +
						"installation");
				}
				ret.add(name);
				final var outputFile = new File(Shared.BASE_DIRECTORY, name);
				if (outputFile.exists()) {
					throw new AppInstallFailedException("Tried to overwrite " + name + " while extracting " + descriptor.uniqueIdentifier());
				}
				Files.copy(zip, Paths.get(outputFile.toURI()));
			}
		} catch (IOException e) {
			throw new AppInstallFailedException(e);
		}
		return ret;
	}

	private void checkHashes(File file) {
		try {
			final var bytes = Files.readAllBytes(Paths.get(file.toURI()));
			this.descriptor.hashes().validate(bytes, this.descriptor.uniqueIdentifier());
		} catch (IOException | SecurityException e) {
			throw new AppInstallFailedException(e);
		}
	}

	private File downloadFile() {
		final var url = this.descriptor.downloadBundle();
		try (final var in = url.openStream()) {
			var output = File.createTempFile("store", descriptor.name());
			Files.copy(in, Paths.get(output.toURI()), StandardCopyOption.REPLACE_EXISTING);
			return output;
		} catch (IOException e) {
			throw new AppInstallFailedException(e);
		}
	}
}
