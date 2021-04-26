package jcwasmx86.store.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record InstallDependencyResolver(AppDescriptor appToInstall, AvailableAppsState allAvailableDescriptors,
										InstalledAppsState installedApps) {

	Set<String> buildDependencySet() {
		final var ret = new HashSet<String>();
		this.buildDependencySet(this.appToInstall, ret);
		return ret;
	}

	private void buildDependencySet(AppDescriptor appToInstall, Set<String> ret) {
		ret.add(appToInstall.uniqueIdentifier());
		Arrays.stream(appToInstall.dependencies()).forEach(a -> {
			if (!ret.contains(a)) {
				final var matchingDescriptors = this.allAvailableDescriptors.getForName(a);
				if (matchingDescriptors.isEmpty()) {
					throw new ResolutionFailedException("Dependency " + a + " of " + appToInstall.uniqueIdentifier() +
						" failed to resolve!");
				}
				this.buildDependencySet(matchingDescriptors.get(), ret);
			}
		});
	}

	Set<AppDescriptor> appsToInstall() {
		return this.buildDependencySet().stream()
			//Remove all that are already installed
			.filter(a -> !this.installedApps.isInstalled(a))
			.map(this.allAvailableDescriptors::forName)
			.collect(Collectors.toSet());
	}
}
