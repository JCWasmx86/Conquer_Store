package jcwasmx86.store.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record UninstallDependencyResolver(InstalledApp toRemove, List<InstalledApp> installedApps) {
	Set<InstalledApp> getAllRemovablePackages() {
		final var ret = new HashSet<InstalledApp>();
		this.getAllRemovablePackages(this.toRemove, ret);
		return ret;
	}

	private void getAllRemovablePackages(final InstalledApp toRemove, final Set<InstalledApp> ret) {
		ret.add(toRemove);
		//Unconditionally remove apps that depend on the app to remove.
		this.installedApps.stream().filter(a -> a.dependsOn(toRemove)).forEach(a -> {
			if (!ret.contains(a)) {
				this.getAllRemovablePackages(a, ret);
			}
		});
		Arrays.stream(this.toRemove.dependencies()).forEach(a -> {
			final var dep = this.forName(a);
			//Remove non-explicitly installed packages...
			if (!ret.contains(a) && !dep.explicitlyInstalled()) {
				final var numberOfDependents = this.numberOfDependents(dep);
				final var alreadyRemoved =
					this.installedApps.stream().filter(b -> b.dependsOn(dep)).filter(b -> ret.contains(b)).count();
				//if all apps that depend on it will be uninstalled, too.
				if (alreadyRemoved == numberOfDependents) {
					this.getAllRemovablePackages(dep, ret);
				}
			}
		});
	}

	private long numberOfDependents(final InstalledApp app) {
		return this.installedApps.stream().filter(a -> a.dependsOn(app)).count();
	}

	private InstalledApp forName(final String s) {
		return this.installedApps.stream().filter(a -> a.uniqueIdentifier().equals(s)).findFirst().get();
	}
}
