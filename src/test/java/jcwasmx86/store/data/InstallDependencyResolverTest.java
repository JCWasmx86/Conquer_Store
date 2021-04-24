package jcwasmx86.store.data;

import java.util.ArrayList;
import java.util.Set;

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
		availableDescriptors.add(this.buildAppDescriptor("dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingTwoDependencies() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test(expected = ResolutionFailedException.class)
	public void testResolvingMissing() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2", "dep3"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingCycles() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2", "dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2"), resolver.buildDependencySet());
	}

	@Test
	public void testResolvingIndirectCycles() {
		final var toInstall = this.buildAppDescriptor("toInstall", "dep1");
		final var availableDescriptors = new ArrayList<AppDescriptor>();
		availableDescriptors.add(this.buildAppDescriptor("dep1", "dep2"));
		availableDescriptors.add(this.buildAppDescriptor("dep2", "dep3"));
		availableDescriptors.add(this.buildAppDescriptor("dep3", "dep1"));
		final var resolver = new InstallDependencyResolver(toInstall, availableDescriptors, null);
		Assert.assertEquals(Set.of("toInstall", "dep1", "dep2", "dep3"), resolver.buildDependencySet());
	}

	private AppDescriptor buildAppDescriptor(final String uniqueName, final String... deps) {
		return new AppDescriptor(null, uniqueName, 0, null, null, null, null, deps,
			null,
			null, false, null);
	}
}
