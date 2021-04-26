package jcwasmx86.store.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AvailableAppsState {
	private Map<String, AppDescriptor> map;

	AvailableAppsState(final List<AppDescriptor> descriptors) {
		this.map = new HashMap<>();
		descriptors.forEach(a -> this.map.put(a.uniqueIdentifier(), a));
	}

	Optional<AppDescriptor> getForName(final String uniqueIdentifier) {
		if (this.map.containsKey(uniqueIdentifier)) {
			return Optional.of(this.map.get(uniqueIdentifier));
		} else {
			return Optional.empty();
		}
	}

	AppDescriptor forName(final String uniqueIdentifier) {
		return this.map.get(uniqueIdentifier);
	}
}
