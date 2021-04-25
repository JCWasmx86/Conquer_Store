package jcwasmx86.store.data;

import java.io.File;
import java.util.Arrays;

import conquer.data.Shared;

/**
 * {@code files} are all files that app installed. {@code explicitlyInstalled} shows, whether this app was installed
 * by the user or as an dependency.
 */
public record InstalledApp(String displayName, String uniqueIdentifier, InstalledFile[] files,
						   boolean explicitlyInstalled, String[] dependencies, String version) {

	public boolean dependsOn(InstalledApp toRemove) {
		return Arrays.stream(this.dependencies).anyMatch(a -> a.equals(toRemove.uniqueIdentifier));
	}

	/**
	 * {@code fileDir} is the filename, based on the base directory (E.g. ~/.config/.conquer on linux)
	 */
	public static record InstalledFile(String fileDir) {

		public void delete() {
			final var realPath = Shared.BASE_DIRECTORY + "/" + this.fileDir;
			final var file = new File(realPath);
			if (file.isFile()) {
				file.delete();
			}
			//Else an directory is here. Don't delete it, as there may be content
			//of other apps
		}
	}
}
