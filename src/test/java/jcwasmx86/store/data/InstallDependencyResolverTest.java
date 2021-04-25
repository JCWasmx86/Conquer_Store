package jcwasmx86.store.data;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class InstallDependencyResolverTest {
	@Test
	public void testResolvingEmpty() {
		final var toInstall = this.buildAppDescriptor("toInstall");
		final var resolver = new InstallDependencyResolver(toInstall, null, null);
		Assert.assertEquals(Set.of("toInstall"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingOneDependency() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(this.buildAppDescriptor("dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingTwoDependencies() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test(expected = ResolutionFailedException.class)
	public void testResolvingMissing() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2", "dep3"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingCycles() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2", "dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingIndirectCycles() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2", "dep3"));
		availableDescriptors.add(this.buildAppDescriptor("dep3", "dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2", "dep3"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingSomeAlreadyInstalled() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2", "dep3"));
		availableDescriptors.add(this.buildAppDescriptor("dep3"));
		final var alreadyInstalled = new ArrayList<InstalledApp>();
		alreadyInstalled.add(this.buildInstalledApp("dep3"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, alreadyInstalled);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), this.toStringSet(resolver.appsToInstall()));
	}

	@Test
	public void testResolvingSomeAlreadyInstalledTree() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2", "dep3"));
		availableDescriptors.add(this.buildAppDescriptor("dep3"));
		final var alreadyInstalled = new ArrayList<InstalledApp>();
		alreadyInstalled.add(this.buildInstalledApp("dep1", "dep2"));
		alreadyInstalled.add(this.buildInstalledApp("dep2", "dep3"));
		alreadyInstalled.add(this.buildInstalledApp("dep3"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, alreadyInstalled);
		Assert.assertEquals(Set.of("toInstall"), this.toStringSet(resolver.appsToInstall()));
	}

	private Set<String> toStringSet(Set<AppDescriptor> set) {
		return set.stream().map(AppDescriptor::uniqueIdentifier).collect(Collectors.toSet());
	}

	private InstalledApp buildInstalledApp(final String uniqueName, final String... dependencies) {
		return new InstalledApp(null, uniqueName, null, false, dependencies, null);
	}

	private AppDescriptor buildAppDescriptor(final String uniqueName, final String... dependencies) {
		return new AppDescriptor(null, uniqueName, 0, null, null, null, null, dependencies,
			null,
			null, false, null);
	}
}
