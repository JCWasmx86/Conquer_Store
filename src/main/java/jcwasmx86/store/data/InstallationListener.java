package jcwasmx86.store.data;

import java.io.File;
import java.util.Set;

public interface InstallationListener {
	//Return false to abort installation
	boolean onAppsCollected(Set<AppDescriptor> toInstall);

	void onDownload(AppDescriptor downloaded, int number, int maximum);

	void afterCheckingChecksum(AppDescriptor checked, int number, int maximum);

	void afterExtracting(AppDescriptor from, int numberOfFile, int numberOfFiles);

	void copy(String fileName, int numberOfFile, int numberOfFiles);
}
