package jcwasmx86.store.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record UninstallDependencyResolver(InstalledApp toRemove, List<InstalledApp> installedApps) {
	Set<InstalledApp> getAllRemovablePackages() {
		System.out.println("Removing: " + this.toRemove.uniqueIdentifier());
		final var ret = new HashSet<InstalledApp>();
		this.getAllRemovablePackages(this.toRemove, ret);
		return ret;
	}


	private void getAllRemovablePackages(final InstalledApp appToRemove, final Set<InstalledApp> set) {
		System.out.println("Visiting: " + appToRemove.uniqueIdentifier());
		set.add(appToRemove);
		//Unconditionally remove apps that depend on the app to remove.
		this.installedApps.stream().filter(a -> a.dependsOn(appToRemove)).forEach(a -> {
			System.out.println("Visiting dependent " + a.uniqueIdentifier() + " of " + appToRemove.uniqueIdentifier());
			if (!set.contains(a)) {
				this.getAllRemovablePackages(a, set);
			}
		});
		Arrays.stream(appToRemove.dependencies()).forEach(a -> {
			System.out.println("Visiting dependency " + a + " of " + appToRemove.uniqueIdentifier());
			final var dep = this.forName(a);
			System.out.println(set.contains(dep) + "//" + dep.explicitlyInstalled());
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

	private InstalledApp forName(final String s) {
		return this.installedApps.stream().filter(a -> a.uniqueIdentifier().equals(s)).findFirst().get();
	}
}
