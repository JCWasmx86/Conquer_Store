package jcwasmx86.store.data;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	public static record Hashes(String md5, String sha1, String sha2) {
		void validate(final byte[] bytes, final String uniqueIdentifier) {
			this.checkHash("MD5", this.md5(), bytes, uniqueIdentifier);
			this.checkHash("SHA-1", this.sha1(), bytes, uniqueIdentifier);
			this.checkHash("SHA-256", this.sha2(), bytes, uniqueIdentifier);
		}

		private void checkHash(final String hashType, final String expected, final byte[] bytes,
							   final String uniqueIdentifier) {
			try {
				final var digest = MessageDigest.getInstance(hashType);
				digest.update(bytes);
				final var hashAsBytes = digest.digest();
				StringBuilder hash = new StringBuilder();
				for (final var hashAsByte : hashAsBytes) {
					hash.append(Integer.toString((hashAsByte & 0xff) + 0x100, 16).substring(1));
				}
				if (!hash.toString().equals(expected)) {
					throw new SecurityException(uniqueIdentifier + ": " + hashType + " doesn't match. Expected: " +
						expected + ", got " + hash);
				}
			} catch (NoSuchAlgorithmException e) {
				throw new AppInstallFailedException(e);
			}
		}
	}
}
