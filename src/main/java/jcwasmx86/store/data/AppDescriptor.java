package jcwasmx86.store.data;

import java.net.URL;

/**
 * An descriptor representing an "app". An app is either a plugin, a strategy, just a library and so on.
 * {@code downloadBundle} is the direct link to the .zip file. {@code uniqueIdentifier} is an globally unique identifier
 * used for dependencies. {@code installedSize} is the estimated size in bytes after installation. {@code name} is
 * the user-facing
 * name of the app. {@code description} is the description shown in the store. {@code logo} is the thumbnail in the
 * minified view.
 * {@code imageURLs} are URLs as showcases. {@code dependencies} are the dependencies of the app. These are the
 * {@code uniqueIdentifier} of an app. {@code version} is the version in the form <code>major.minor.patch</code>.
 * {@code tags} are tags that show what this is, e.g. a plugin, addition or similar.
 * {@code isLibrary} signals, that this app shouldn't be listed.
 */
public record AppDescriptor(URL downloadBundle, String uniqueIdentifier, long installedSize, String name,
							String description, URL logo,
							URL[] imageURLs, String[] dependencies, String version, String[] tags, boolean isLibrary,
							Hashes hashes) {

	/**
	 * Hashes of the downloaded bundle. MD5, SHA1, SHA2 to prevent data corruption and validate the downloaded package.
	 */
	public static record Hashes(String md5, String sha1, String sha2) {}
}