package jcwasmx86.store.data;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import jcwasmx86.store.data.InstalledApp.InstalledFile;

public class UninstallProcess implements Runnable {

	private UninstallListener listener;
	private InstalledAppsState apps;
	private InstalledApp toRemove;
	private boolean finished;
	private Set<InstalledApp> appsToRemove;

	UninstallProcess(UninstallListener listener, InstalledAppsState apps, InstalledApp toRemove) {
		this.listener = listener;
		this.apps = apps;
		this.toRemove = toRemove;
	}

	@Override
	public void run() {
		final var resolver = new UninstallDependencyResolver(this.toRemove, this.apps);
		this.appsToRemove = resolver.getAllRemovablePackages();
		if (!this.listener.onAppsCollected(appsToRemove)) {
			return;
		}
		final var files = this.collectFiles(appsToRemove);
		this.remove(files);
		this.finished = true;
	}

	private void remove(final Set<InstalledFile> files) {
		var cnter = 0;
		for (final var file : files) {
			file.delete();
			this.listener.deletingFile(file.fileDir(), cnter + 1, files.size());
			cnter++;
		}
	}

	private Set<InstalledFile> collectFiles(final Set<InstalledApp> toRemove) {
		return toRemove.stream().flatMap(a -> Arrays.stream(a.files())).collect(Collectors.toSet());
	}

	Set<InstalledApp> getRemovedApps() {
		if (this.finished) {
			return this.appsToRemove;
		} else {
			throw new UnsupportedOperationException("Not finished!");
		}
	}

	public boolean isFinished() {
		return this.finished;
	}
}
