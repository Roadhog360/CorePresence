package logreader;

import managers.GameStateManager;
import net.arikia.dev.drpc.DiscordRPC;
import utils.Utils;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

public class LogWatcher extends Thread {

	@Override
	public void run() {
		try {
			watchFile();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		super.run();
	}

	private final int runEvery = 1000;
	private boolean firstRun = true;

	private String lastHash = "none";

	public void watchFile() throws InterruptedException {
		File file = new File(Utils.getFilePath() + "/OmegaStrikers.log");
		long retryTime = runEvery * 3L;
		while (true) { //This is never supposed to end while the program is running
			try {
				startWatchingFile(file);
				System.out.println("Game closed or restarted, retrying in " + (double)(retryTime / 1000) + " seconds");
				sleep(retryTime);
			} catch (FileNotFoundException e) {
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
					String line = lines[i];
//						System.out.println(line);
					if (LogManager.clearLogBrackets(line).startsWith("Log file closed")) {
						DiscordRPC.discordClearPresence();
						LogManager.setClosed(true);
						GameStateManager.clearPresence();
						return;
					} else if (line.startsWith("Log file open")) {
						if(!firstRun) { //Log file was reopened
							LogManager.setClosed(false);
							GameStateManager.clearPresence();
						}
					} else {
						if(firstRun) { //Do not update presence on first batch of lines read
							LogManager.setClosed(true);
						}
						try {
							LogManager.getActionFor(line);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(firstRun) {  //Do not update presence on first batch of lines read
							LogManager.setClosed(false);
						}
					}

					if(firstRun && i == lines.length - 1) { //After first run, update status to collected values
						GameStateManager.updateStatus();
					}
				}
				lastPosition = fh.getFilePointer();
			}

			fh.close();
			firstRun = false;
			sleep(runEvery);
		}
	}

	public boolean isFirstRun() {
		return firstRun;
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
