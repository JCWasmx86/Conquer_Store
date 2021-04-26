package jcwasmx86.store.data;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class InstallDependencyResolverTest {
	@Test
	public void testResolvingEmpty() {
		final var toInstall = TestUtils.buildAppDescriptor("toInstall");
		final var resolver = new InstallDependencyResolver(toInstall, null, null);
		Assert.assertEquals(Set.of("toInstall"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingOneDependency() {
		final var toInstall = TestUtils.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, new AvailableAppsState(availableDescriptors), null);
		Assert.assertEquals(Set.of("toInstall", "dep1"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingTwoDependencies() {
		final var toInstall = TestUtils.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep2"));
		final var resolver = new InstallDependencyResolver(toInstall, new AvailableAppsState(availableDescriptors), null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test(expected = ResolutionFailedException.class)
	public void testResolvingMissing() {
		final var toInstall = TestUtils.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep2", "dep3"));
		final var resolver = new InstallDependencyResolver(toInstall, new AvailableAppsState(availableDescriptors), null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingCycles() {
		final var toInstall = TestUtils.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep2", "dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, new AvailableAppsState(availableDescriptors), null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingIndirectCycles() {
		final var toInstall = TestUtils.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep2", "dep3"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep3", "dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, new AvailableAppsState(availableDescriptors), null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2", "dep3"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingSomeAlreadyInstalled() {
		final var toInstall = TestUtils.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep2", "dep3"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep3"));
		final var alreadyInstalled = new ArrayList<InstalledApp>();
		alreadyInstalled.add(TestUtils.buildInstalledApp("dep3"));
		final var resolver = new InstallDependencyResolver(toInstall, new AvailableAppsState(availableDescriptors),
			new InstalledAppsState(alreadyInstalled));
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"),
			TestUtils.appDescriptorsToStringSet(resolver.appsToInstall()));
	}

	@Test
	public void testResolvingSomeAlreadyInstalledTree() {
		final var toInstall = TestUtils.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(toInstall);
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep2", "dep3"));
		availableDescriptors.add(TestUtils.buildAppDescriptor("dep3"));
		final var alreadyInstalled = new ArrayList<InstalledApp>();
		alreadyInstalled.add(TestUtils.buildInstalledApp("dep1", "dep2"));
		alreadyInstalled.add(TestUtils.buildInstalledApp("dep2", "dep3"));
		alreadyInstalled.add(TestUtils.buildInstalledApp("dep3"));
		final var resolver = new InstallDependencyResolver(toInstall, new AvailableAppsState(availableDescriptors),
			new InstalledAppsState(alreadyInstalled));
		Assert.assertEquals(Set.of("toInstall"), TestUtils.appDescriptorsToStringSet(resolver.appsToInstall()));
	}
}
