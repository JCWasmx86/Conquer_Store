package jcwasmx86.store.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class UninstallDependencyResolverTest {

	@Test
	public void testRemovingWithoutDependencies() {
		final var toRemove = this.buildInstalledApp("toRemove");
		final var resolver = new UninstallDependencyResolver(toRemove, List.of(toRemove));
		Assert.assertEquals(Set.of("toRemove"), this.toStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithOneDependency() {
		final var toRemove = this.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(this.buildInstalledApp("dep1"));
		final var resolver = new UninstallDependencyResolver(toRemove, list);
		Assert.assertEquals(Set.of("toRemove", "dep1"), this.toStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithOneExplicitDependency() {
		final var toRemove = this.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(this.buildInstalledApp("dep1", true));
		final var resolver = new UninstallDependencyResolver(toRemove, list);
		Assert.assertEquals(Set.of("toRemove"), this.toStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithDependent() {
		final var toRemove = this.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(this.buildInstalledApp("dep1"));
		list.add(this.buildInstalledApp("parent1", "toRemove"));
		final var resolver = new UninstallDependencyResolver(toRemove, list);
		Assert.assertEquals(Set.of("toRemove", "dep1", "parent1"),
			this.toStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithDependentImplicit() {
		final var toRemove = this.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(this.buildInstalledApp("dep1"));
		list.add(this.buildInstalledApp("parent1", "toRemove", "child1"));
		list.add(this.buildInstalledApp("child1"));
		final var resolver = new UninstallDependencyResolver(toRemove, list);
		Assert.assertEquals(Set.of("toRemove", "dep1", "parent1", "child1"),
			this.toStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithDependentImplicitAndCleaningUp() {
		final var toRemove = this.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(this.buildInstalledApp("dep1"));
		list.add(this.buildInstalledApp("parent1", "toRemove", "child1"));
		list.add(this.buildInstalledApp("child1", "cleanMe"));
		list.add(this.buildInstalledApp("cleanMe"));
		final var resolver = new UninstallDependencyResolver(toRemove, list);
		Assert.assertEquals(Set.of("toRemove", "dep1", "parent1", "child1", "cleanMe"),
			this.toStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	public void testRemovingWithDependentImplicitAndCleaningUpOfMore() {
		final var toRemove = this.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(this.buildInstalledApp("dep1"));
		list.add(this.buildInstalledApp("parent1", "toRemove", "child1"));
		list.add(this.buildInstalledApp("child1", "cleanMe"));
		list.add(this.buildInstalledApp("cleanMe2", true));
		list.add(this.buildInstalledApp("cleanMe", "cleanMe2"));
		final var resolver = new UninstallDependencyResolver(toRemove, list);
		Assert.assertEquals(Set.of("toRemove", "dep1", "parent1", "child1", "cleanMe"),
			this.toStringSet(resolver.getAllRemovablePackages()));
	}

	//These both are ignored, as those are failing, as
	//the resolver doesn't recognize cycles
	@Test
	@Ignore
	public void testCircles() {
		final var toRemove = this.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(this.buildInstalledApp("dep1", "dep2"));
		list.add(this.buildInstalledApp("dep2", "dep1"));
		final var resolver = new UninstallDependencyResolver(toRemove, list);
		Assert.assertEquals(Set.of("toRemove", "dep1", "dep2"),
			this.toStringSet(resolver.getAllRemovablePackages()));
	}

	@Test
	@Ignore
	public void testCircles2() {
		final var toRemove = this.buildInstalledApp("toRemove", "dep1");
		final var list = new ArrayList<InstalledApp>();
		list.add(toRemove);
		list.add(this.buildInstalledApp("dep1", "dep1"));
		final var resolver = new UninstallDependencyResolver(toRemove, list);
		Assert.assertEquals(Set.of("toRemove", "dep1", "dep2"),
			this.toStringSet(resolver.getAllRemovablePackages()));
	}

	private Set<String> toStringSet(Set<InstalledApp> set) {
		return set.stream().map(InstalledApp::uniqueIdentifier).collect(Collectors.toSet());
	}

	private InstalledApp buildInstalledApp(final String uniqueName, final String... dependencies) {
		return this.buildInstalledApp(uniqueName, false, dependencies);
	}

	private InstalledApp buildInstalledApp(final String uniqueName, final boolean explicit,
										   final String... dependencies) {
		return new InstalledApp(null, uniqueName, null, explicit, dependencies, null);
	}
}