package jcwasmx86.store.data;

import java.util.Set;
import java.util.stream.Collectors;

final class TestUtils {
	private TestUtils() {
		throw new UnsupportedOperationException();
	}

	static Set<String> appDescriptorsToStringSet(Set<AppDescriptor> set) {
		return set.stream().map(AppDescriptor::uniqueIdentifier).collect(Collectors.toSet());
	}

	static Set<String> installedAppsToStringSet(final Set<InstalledApp> set) {
		return set.stream().map(InstalledApp::uniqueIdentifier).collect(Collectors.toSet());
	}

	static InstalledApp buildInstalledApp(final String uniqueName, final String... dependencies) {
		return TestUtils.buildInstalledApp(uniqueName, false, dependencies);
	}

	static InstalledApp buildInstalledApp(final String uniqueName, final boolean explicit,
										  final String... dependencies) {
		return new InstalledApp(null, uniqueName, null, explicit, dependencies, null, 0);
	}

	static AppDescriptor buildAppDescriptor(final String uniqueName, final String... dependencies) {
		return new AppDescriptor(null, uniqueName, 0, null, null, null, null, dependencies,
			null,
			null, false, null);
	}
}
