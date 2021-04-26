package jcwasmx86.store.data;

import java.util.Set;

public interface UninstallListener {
	//Return false to abort
	boolean onAppsCollected(final Set<InstalledApp> appsToRemove);

	void deletingFile(String fileName, int number, int numberOfFiles);
}
