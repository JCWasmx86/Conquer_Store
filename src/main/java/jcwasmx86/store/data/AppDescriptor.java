package jcwasmx86.store.data;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

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
public final class AppDescriptor {
	private URL downloadBundle;
	private String uniqueIdentifier;
	private long installedSize;
	private String name;
	private String description;
	private URL logo;
	private URL[] imageURLs;
	private String[] dependencies;
	private String version;
	private String[] tags;
	private boolean isLibrary;
	private Hashes hashes;

	public AppDescriptor(URL downloadBundle, String uniqueIdentifier, long installedSize, String name,
						 String description, URL logo,
						 URL[] imageURLs, String[] dependencies, String version, String[] tags, boolean isLibrary,
						 Hashes hashes) {
		this.downloadBundle = downloadBundle;
		this.uniqueIdentifier = uniqueIdentifier;
		this.installedSize = installedSize;
		this.name = name;
		this.description = description;
		this.logo = logo;
		this.imageURLs = imageURLs;
		this.dependencies = dependencies;
		this.version = version;
		this.tags = tags;
		this.isLibrary = isLibrary;
		this.hashes = hashes;
	}

	public URL downloadBundle() { return downloadBundle; }

	public String uniqueIdentifier() { return uniqueIdentifier; }

	public long installedSize() { return installedSize; }

	public String name() { return name; }

	public String description() { return description; }

	public URL logo() { return logo; }

	public URL[] imageURLs() { return imageURLs; }

	public String[] dependencies() { return dependencies; }

	public String version() { return version; }

	public String[] tags() { return tags; }

	public boolean isLibrary() { return isLibrary; }

	public Hashes hashes() { return hashes; }

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (AppDescriptor) obj;
		return Objects.equals(this.downloadBundle, that.downloadBundle) &&
			Objects.equals(this.uniqueIdentifier, that.uniqueIdentifier) &&
			this.installedSize == that.installedSize &&
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.description, that.description) &&
			Objects.equals(this.logo, that.logo) &&
			Objects.equals(this.imageURLs, that.imageURLs) &&
			Objects.equals(this.dependencies, that.dependencies) &&
			Objects.equals(this.version, that.version) &&
			Objects.equals(this.tags, that.tags) &&
			this.isLibrary == that.isLibrary &&
			Objects.equals(this.hashes, that.hashes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(downloadBundle, uniqueIdentifier, installedSize, name, description, logo, imageURLs,
			dependencies, version, tags, isLibrary, hashes);
	}

	@Override
	public String toString() {
		return "AppDescriptor[" +
			"downloadBundle=" + downloadBundle + ", " +
			"uniqueIdentifier=" + uniqueIdentifier + ", " +
			"installedSize=" + installedSize + ", " +
			"name=" + name + ", " +
			"description=" + description + ", " +
			"logo=" + logo + ", " +
			"imageURLs=" + imageURLs + ", " +
			"dependencies=" + dependencies + ", " +
			"version=" + version + ", " +
			"tags=" + tags + ", " +
			"isLibrary=" + isLibrary + ", " +
			"hashes=" + hashes + ']';
	}


	/**
	 * Hashes of the downloaded bundle. MD5, SHA1, SHA2 to prevent data corruption and validate the downloaded package.
	 */
	public static final class Hashes {
		private String md5;
		private String sha1;
		private String sha2;

		public Hashes(String md5, String sha1, String sha2) {
			this.md5 = md5;
			this.sha1 = sha1;
			this.sha2 = sha2;
		}

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

		public String md5() { return md5; }

		public String sha1() { return sha1; }

		public String sha2() { return sha2; }

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null || obj.getClass() != this.getClass()) return false;
			var that = (Hashes) obj;
			return Objects.equals(this.md5, that.md5) &&
				Objects.equals(this.sha1, that.sha1) &&
				Objects.equals(this.sha2, that.sha2);
		}

		@Override
		public int hashCode() {
			return Objects.hash(md5, sha1, sha2);
		}

		@Override
		public String toString() {
			return "Hashes[" +
				"md5=" + md5 + ", " +
				"sha1=" + sha1 + ", " +
				"sha2=" + sha2 + ']';
		}

	}
}
