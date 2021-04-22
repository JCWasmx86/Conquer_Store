package jcwasmx86.store.data;

/**
 * {@code files} are all files that app installed. {@code explicitlyInstalled} shows, whether this app was installed
 * by the user or as an dependency.
 */
public record InstalledApp(String displayName, String uniqueIdentifier, InstalledFile[] files,
						   boolean explicitlyInstalled) {

	/**
	 * {@code fileDir} is the filename, based on the base directory (E.g. ~/.config/.conquer on linux)
	 */
	public static record InstalledFile(String fileDir) {

	}
}
