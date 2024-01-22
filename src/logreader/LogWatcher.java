package logreader;

import managers.GameStateManager;
import net.arikia.dev.drpc.DiscordRPC;
import utils.Utils;

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

	private final int runEvery = 1000;

	private String lastHash = "none";

	public void watchFile() throws InterruptedException {
		File file = null;
		long retryTime = runEvery * 3L;
		while (true) { //This is never supposed to end while the program is running
			try {
				if(file == null) { //Set it here to catch NoSuchFileException when the log is temporarily gone on restart
					file = new File(Utils.getFilePath() + "/OmegaStrikers.log");
				}
				startWatchingFile(file);
				System.out.println("Game closed or restarted, retrying in " + (double)(retryTime / 1000) + " seconds");
				sleep(retryTime);
			} catch (FileNotFoundException | NoSuchFileException e) {
				System.out.println("Game closed or restarted, or log file not present, retrying in " + (double)(retryTime / 1000) + " seconds");
				sleep(retryTime);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void startWatchingFile(File file) throws InterruptedException, IOException {
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
				fh.seek(lastPosition);

				StringBuilder newLines = new StringBuilder();
				int c;
				while((c = fh.read()) != -1) {
					newLines.append((char) c);
				}

				String[] lines = newLines.toString().split("\n");
				for(int i = 0; i < lines.length; i++) {
					LogManager.setClosed(true);
					String line = lines[i];
//						System.out.println(line);
					if (LogManager.clearLogBrackets(line).startsWith("Log file closed")) {
						DiscordRPC.discordClearPresence();
						GameStateManager.resetValues();
						return;
					} else if (line.startsWith("Log file open")) {
						GameStateManager.resetValues();
						GameStateManager.setInMenus();
					} else {
						LogManager.getActionFor(LogManager.clearLogBrackets(line));
					}

					if(i == lines.length - 1) { //After first run, update status to collected values
						LogManager.setClosed(false);
						GameStateManager.updateStatus();
					}
				}
				lastPosition = fh.getFilePointer();
			}

			fh.close();
			sleep(runEvery);
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
