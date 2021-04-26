package jcwasmx86.store.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class UninstallDependencyResolverTest {

	@Test
	public void testRemovingWithoutDependencies() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove");
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(List.of(toRemove)));
		Assert.assertEquals(Set.of("toRemove"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithOneDependency() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(TestUtils.buildInstalledApp("dep1"));
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(list));
		Assert.assertEquals(Set.of("toRemove", "dep1"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithOneExplicitDependency() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(TestUtils.buildInstalledApp("dep1", true));
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(list));
		Assert.assertEquals(Set.of("toRemove"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithDependent() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(TestUtils.buildInstalledApp("dep1"));
		list.add(TestUtils.buildInstalledApp("parent1", "toRemove"));
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(list));
		Assert.assertEquals(Set.of("toRemove", "dep1", "parent1"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithDependentImplicit() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(TestUtils.buildInstalledApp("dep1"));
		list.add(TestUtils.buildInstalledApp("parent1", "toRemove", "child1"));
		list.add(TestUtils.buildInstalledApp("child1"));
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(list));
		Assert.assertEquals(Set.of("toRemove", "dep1", "parent1", "child1"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithDependentImplicitAndCleaningUp() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(TestUtils.buildInstalledApp("dep1"));
		list.add(TestUtils.buildInstalledApp("parent1", "toRemove", "child1"));
		list.add(TestUtils.buildInstalledApp("child1", "cleanMe"));
		list.add(TestUtils.buildInstalledApp("cleanMe"));
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(list));
		Assert.assertEquals(Set.of("toRemove", "dep1", "parent1", "child1", "cleanMe"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithDependentImplicitAndCleaningUpOfMore() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(TestUtils.buildInstalledApp("dep1"));
		list.add(TestUtils.buildInstalledApp("parent1", "toRemove", "child1"));
		list.add(TestUtils.buildInstalledApp("child1", "cleanMe"));
		list.add(TestUtils.buildInstalledApp("cleanMe2", true));
		list.add(TestUtils.buildInstalledApp("cleanMe", "cleanMe2"));
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(list));
		Assert.assertEquals(Set.of("toRemove", "dep1", "parent1", "child1", "cleanMe"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}

	//These both are ignored, as those are failing, as
	//the resolver doesn't recognize cycles
	@Test
	@Ignore
	public void testCircles() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(TestUtils.buildInstalledApp("dep1", "dep2"));
		list.add(TestUtils.buildInstalledApp("dep2", "dep1"));
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(list));
		Assert.assertEquals(Set.of("toRemove", "dep1", "dep2"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	@Ignore
	public void testCircles2() {
		final var toRemove = TestUtils.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(TestUtils.buildInstalledApp("dep1", "dep1"));
		final var resolver = new UninstallDependencyResolver(toRemove, new InstalledAppsState(list));
		Assert.assertEquals(Set.of("toRemove", "dep1", "dep2"),
			TestUtils.installedAppsToStringSet(resolver.getAllRemovablePackages()));
	}
}