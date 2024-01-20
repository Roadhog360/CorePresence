package logreader;

import gamedata.Scoreboard;
import gamedata.Striker;
import managers.GameStateManager;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LogWatcher extends Thread {
	private static final String LINUX_DEFAULT_LOG_PATH = System.getProperty("user.home") + "/.steam/steam/steamapps/compatdata/1869590/pfx/drive_c/users/steamuser/AppData/Local/OmegaStrikers/Saved/Logs";

	@Override
	public void run() {
		watchFile();
		super.run();
	}

	private File file;
	private int runEvery = 1000;
	private boolean firstRun = true;

	private long lastPosition = 0;
	private long lastRunDate = 0;

	public void watchFile() {
		String path = LINUX_DEFAULT_LOG_PATH;
		file = new File(path + "/OmegaStrikers.log");
		try {
			while(true) {
				BasicFileAttributes fileAttribs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				long fileLength = fileAttribs.size();
				if(fileLength > lastPosition && fileAttribs.lastModifiedTime().toMillis() > lastRunDate) {
					lastRunDate = System.currentTimeMillis();
					RandomAccessFile fh = new RandomAccessFile(file, "r");
					fh.seek(lastPosition);

					StringBuilder newLines = new StringBuilder();
					int c;
					while((c = fh.read()) != -1) {
						newLines.append((char) c);
					}

					for(String line : newLines.toString().split("\n")) {
//						System.out.println(line);
						if (LogManager.clearLogBrackets(line).startsWith("Log file closed")) {
//							DiscordRPC.discordClearPresence();
//							LogManager.setClosed(true);
						} else if (line.startsWith("Log file open")) {
							if(!firstRun) { //Log file was reopened
								LogManager.setClosed(false);
								Scoreboard.INSTANCE.resetScoreBoard();
								GameStateManager.setInMenus();
								GameStateManager.ingameCharacter = GameStateManager.menuCharacter = Striker.NONE;
							}
						} else {
//							if(firstRun) { //Do not update presence on first batch of lines read
//								LogManager.setClosed(true);
//							}
							try {
								LogManager.getActionFor(line);
							} catch (Exception e) {
								e.printStackTrace();
							}
//							if(firstRun) {  //Do not update presence on first batch of lines read
//								LogManager.setClosed(false);
//							}
						}
					}

					lastPosition = fh.getFilePointer();
					fh.close();
					firstRun = false;
					sleep(runEvery);
				}
			}
		}
		catch(Exception ignored) {
			if(ignored instanceof FileNotFoundException) {
				System.out.println("closed file");
			}
		}
	}

	private void startWatchingFile() {

	}

	public boolean isFirstRun() {
		return firstRun;
	}
}
