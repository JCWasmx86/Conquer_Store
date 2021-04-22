package jcwasmx86.store.data;

import java.io.File;

import conquer.data.Shared;

public class Data {
	public static String STORE_DATA_DIR = Shared.BASE_DIRECTORY + "/jcwasmx86.store";
	public static String STORE_URLS_FILE = Data.STORE_DATA_DIR + "/urls";

	static {
		new File(Data.STORE_DATA_DIR).mkdirs();
	}
}
