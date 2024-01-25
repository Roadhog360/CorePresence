package corepresence.java.logreader;

import corepresence.java.managers.GameStateManager;
import net.arikia.dev.drpc.DiscordRPC;
import corepresence.java.utils.Utils;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

public class LogWatcher extends Thread {

	@Override
	public void run() {
		System.out.println("Initializing LogWatcher in location: " + Utils.getFilePath().replaceAll("\\\\", "/") + "/OmegaStrikers.log");
		try {
			watchFile();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		super.run();
	}

	private static final int runEvery = 1000;

	private String lastHash = "none";
	private boolean sentWarnings = false;

	public void watchFile() throws InterruptedException {
		File file = null;
		while (true) { //This is never supposed to end while the program is running
			try {
				if(file == null) { //Set it here to catch NoSuchFileException when the log is temporarily gone on restart
					file = new File(Utils.getFilePath() + "/OmegaStrikers.log");
				}
				startWatchingFile(file);
				gameClosedWarning(); //This line is reached of the above loop exited
				sleepWithErrorHandling(runEvery * 4L);
			} catch (FileNotFoundException | NoSuchFileException e) {
				gameClosedWarning();
				sleepWithErrorHandling(runEvery * 4L);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void gameClosedWarning() {
		if(!sentWarnings) {
			sentWarnings = true;
			System.out.println("Game closed or restarted, waiting for logs to re-initialize.");
		}
	}

	private void sleepWithErrorHandling(long time) {
		try {
			sleep(time);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void startWatchingFile(File file) throws IOException {
		String hash = getSHAHash(file);
		if(lastHash.equals(hash)) {
			return;
		}
		lastHash = hash;
		long lastPosition = 0;
		while (true) {
			BasicFileAttributes fileAttribs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			long fileLength = fileAttribs.size();
			RandomAccessFile fh = new RandomAccessFile(file, "r");
			if(fileLength > lastPosition) {
				if(sentWarnings) {
					sentWarnings = false;
					System.out.println("Reconnected to game logs.");
				}

				fh.seek(lastPosition);

				StringBuilder newLines = new StringBuilder();
				int c;
				while((c = fh.read()) != -1) {
					newLines.append((char) c);
				}

				String[] lines = newLines.toString().split("\n");
				boolean actionPerformed = false;
				for (String line : lines) {
					if (LogManager.clearLogBrackets(line).startsWith("Log file closed")) {
						DiscordRPC.discordClearPresence();
						GameStateManager.resetValues();
						return;
					} else if (line.startsWith("Log file open")) {
						GameStateManager.setInMenus();
						actionPerformed = true;
					} else if (LogManager.getActionFor(line)) {
						actionPerformed = true;
					}
				}
				if(actionPerformed) { //Update status to collected values
					GameStateManager.updateStatus();
				}
				lastPosition = fh.getFilePointer();
			}
			fh.close();
			sleepWithErrorHandling(runEvery);
		}
	}

	private String getSHAHash(File file) {
		try {
			byte[] data = Files.readAllBytes(file.toPath());
			byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
			return new BigInteger(1, hash).toString(16);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
