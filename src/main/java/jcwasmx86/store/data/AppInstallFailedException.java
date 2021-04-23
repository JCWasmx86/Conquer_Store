package jcwasmx86.store.data;

public class AppInstallFailedException extends RuntimeException {
	public AppInstallFailedException(final Exception e) {super(e);}

	public AppInstallFailedException(final String s) {super(s);}
}
