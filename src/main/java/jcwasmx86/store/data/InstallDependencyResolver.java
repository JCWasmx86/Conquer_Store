package jcwasmx86.store.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record InstallDependencyResolver(AppDescriptor appToInstall, List<AppDescriptor> allAvailableDescriptors,
										List<InstalledApp> installedApps) {

	Set<String> buildDependencySet() {
		final var ret = new HashSet<String>();
		this.buildDependencySet(this.appToInstall, ret);
		return ret;
	}

	private void buildDependencySet(AppDescriptor appToInstall, Set<String> ret) {
		ret.add(appToInstall.uniqueIdentifier());
		Arrays.stream(appToInstall.dependencies()).forEach(a -> {
			if (!ret.contains(a)) {
				final var matchingDescriptors =
					this.allAvailableDescriptors.stream().filter(desc -> desc.uniqueIdentifier().equals(a)).toList();
				if (matchingDescriptors.isEmpty()) {
					throw new ResolutionFailedException("Dependency " + a + " of " + appToInstall.uniqueIdentifier() +
						" failed to resolve!");
				}
				this.buildDependencySet(matchingDescriptors.get(0), ret);
			}
		});
	}

	Set<AppDescriptor> appsToInstall() {
		return this.buildDependencySet().stream()
			//Remove all that are already installed
			.filter(a -> this.installedApps.stream().filter(b -> b.uniqueIdentifier().equals(a)).count() == 0)
			.map(this::forName)
			.collect(Collectors.toSet());
	}

	private AppDescriptor forName(final String name) {
		return this.allAvailableDescriptors.stream().filter(a -> a.uniqueIdentifier().equals(name)).findFirst().get();
	}
}
