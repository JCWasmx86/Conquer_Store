package jcwasmx86.store.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class InstalledAppsState {
	private Map<String, InstalledApp> apps;

	InstalledAppsState(final List<InstalledApp> installedApps) {
		this.apps = new HashMap<>();
		installedApps.forEach(a -> this.apps.put(a.uniqueIdentifier(), a));
	}

	public boolean isInstalled(final String uniqueIdentifier) {
		return this.apps.containsKey(uniqueIdentifier);
	}

	public InstalledApp getForName(final String uniqueIdentifier) {
		return this.apps.get(uniqueIdentifier);
	}

	public Stream<InstalledApp> stream() {
		return this.apps.values().stream();
	}

	void addAll(final Set<InstalledApp> installedApps) {
		installedApps.forEach(a -> this.apps.put(a.uniqueIdentifier(), a));
	}
}
