package jcwasmx86.store.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public record UninstallDependencyResolver(InstalledApp toRemove, InstalledAppsState installedApps) {
	Set<InstalledApp> getAllRemovablePackages() {
		final var ret = new HashSet<InstalledApp>();
		this.getAllRemovablePackages(this.toRemove, ret);
		return ret;
	}


	private void getAllRemovablePackages(final InstalledApp appToRemove, final Set<InstalledApp> set) {
		set.add(appToRemove);
		//Unconditionally remove apps that depend on the app to remove.
		this.installedApps.stream().filter(a -> a.dependsOn(appToRemove)).forEach(a -> {
			if (!set.contains(a)) {
				this.getAllRemovablePackages(a, set);
			}
		});
		Arrays.stream(appToRemove.dependencies()).forEach(a -> {
			final var dep = this.installedApps.getForName(a);
			//Remove non-explicitly installed packages...
			if (!set.contains(dep) && !dep.explicitlyInstalled()) {
				final var numberOfDependents = this.numberOfDependents(dep);
				final var alreadyRemoved =
					this.installedApps.stream().filter(b -> b.dependsOn(dep)).filter(set::contains).count();
				//if all apps that depend on it will be uninstalled, too.
				if (alreadyRemoved == numberOfDependents) {
					this.getAllRemovablePackages(dep, set);
				}
			}
		});
	}

	private long numberOfDependents(final InstalledApp app) {
		return this.installedApps.stream().filter(a -> a.dependsOn(app)).count();
	}
}
