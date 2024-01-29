package corepresence.java;

import corepresence.java.logreader.LogManager;
import corepresence.java.managers.GameStateManager;
import corepresence.java.updates.VersionChecker;
import corepresence.java.window.GUI;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;

import javax.swing.*;

public class Main {

	public static final String version = "1.0";
	public static final int internalVersion = 1;

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		GUI.setupWindow();
		VersionChecker.doUpdateCheck();

		System.out.println("Starting " + GUI.appname + " (" + version + ")");
		LogManager.init();
		GameStateManager.updateTime();
		initDiscord();

		System.out.println("Running callbacks...");
		DiscordRPC.discordRunCallbacks();

		new VersionChecker().start();

		LogManager.postInit();
	}

	//https://github.com/Roadhog360/CorePresence/releases/download/<version>/CorePresence_<version>.jar

	static void initDiscord() {
		DiscordEventHandlers handler = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			StringBuilder initMsg = new StringBuilder("Connecting to Discord user: " + user.username);
			if(user.discriminator.length() == 4) {
				initMsg.append("#").append(user.discriminator);
			}
			initMsg.append(".");
			System.out.println(initMsg);
		}).build();
		DiscordRPC.discordInitialize("1197750595042418768", handler, false);
		DiscordRPC.discordRegister("1197750595042418768", "");
	}

}