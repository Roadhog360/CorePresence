package corepresence.java.updates;

import corepresence.java.Main;
import corepresence.java.window.GUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Adapted from Jabelar's tutorials
 * Taken from VillageNames with permission
 * http://jabelarminecraft.blogspot.com/p/minecraft-forge-1721710-making-mod.html
 * Parallel threading provided by Roadhog360
 *
 * @author AstroTibs
 */

public class VersionChecker extends Thread {

	public static void doUpdateCheck() {
		new VersionChecker().start();
	}

	@Override
	public void run() {
		BufferedReader in = null;

		try {
			URL url = new URL("https://raw.githubusercontent.com/Roadhog360/CorePresence/main/LATEST_VERSION");
			in = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (Exception e) {
			System.err.println("Could not connect with server to compare " + Main.version + " (" + Main.internalVersion + ") version");
			GUI.setUpdateStatus("Update check failed. No internet or connection failed.");
			return;
		}

		int internalVersion;
		String version;
		try {
			internalVersion = Integer.parseInt(in.readLine());
			version = in.readLine();
			in.close();
		} catch (Exception e) {
			System.err.println("Failed to compare " + Main.version + " (" + Main.internalVersion + ") version");
			GUI.setUpdateStatus("Update check failed. An error occured. (Check logs)");
			e.printStackTrace();
			return;
		}

		if(internalVersion > Main.internalVersion) {
			GUI.setUpdateStatus("A new update is available! Click to download.");
			GUI.setUpdateVersion(version);
		} else {
			GUI.setUpdateStatus("CorePresence is up to date.");
		}
	}
}
