package utils;

public final class Utils {
	private static final String UNIX_DEFAULT_LOG_PATH =
			System.getProperty("user.home") + "/.steam/steam/steamapps/compatdata/1869590/pfx/drive_c/users/steamuser/AppData/Local/OmegaStrikers/Saved/Logs";
	private static final String WINDOWS_DEFAULT_LOG_PATH =
			System.getenv("LOCALAPPDATA") + "/Local/OmegaStrikers/Saved/Logs";

	public static String getFilePath() {
		if(isWindows()) {
			return WINDOWS_DEFAULT_LOG_PATH;
		}
		if(isUnix()) {
			return UNIX_DEFAULT_LOG_PATH;
		}
//		if(isMac()) {
//			return "";
//		}
		throw new RuntimeException("OS " + OS + " not implemented, open a GitHub issue to get your OS supported!");
	}

	private static final String OS = System.getProperty("os.name").toLowerCase();

	public static boolean isWindows() {
		return (OS.contains("win"));
	}

	public static boolean isMac() {
		return (OS.contains("mac"));
	}

	public static boolean isUnix() {
		return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
	}

//	public static boolean isSolaris() {
//		return (OS.contains("sunos"));
//	}
}
