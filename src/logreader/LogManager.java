package logreader;

import com.google.gson.*;
import gamedata.Location;
import gamedata.Striker;
import managers.GameStateManager;

import java.util.Locale;

public class LogManager {

	private static boolean closed = false;
	private static LogWatcher watcher = new LogWatcher();

	public static void init() {
	}

	public static void postInit() {
		watcher.start();
	}

	public static void getActionFor(String logLine) {
		boolean actionPerformed = false;
		//Gets the username of the player
		String phrase = "LogPMIdentitySubsystem: UPMIdentitySubsystem::HandleSuccessfulLoginResponse - Logged in as user: ";
		if(clearLogBrackets(logLine).startsWith(phrase)) {
			String name = clearLogBrackets(logLine).replace(phrase, "").replace("\r", "");
			GameStateManager.playerName = name.substring(0, name.indexOf(" with id"));
			String[] contents = name.split(" ");
			GameStateManager.playerID = contents[contents.length - 1];
			System.out.println("Setting player data to: " + GameStateManager.playerName + " : " + GameStateManager.playerID);
			actionPerformed = true;
		}

		//Gets the level of the player, and character
		phrase = "LogPMServicesSubsystem: Warning: UPMServicesSubsystem::ConnectWebSocket::<lambda_964673719509e731966eb292ee6d2929>::operator () - WebSocketConnection->OnMessage: ";
		if(clearLogBrackets(logLine).startsWith(phrase)) {
			String rawData = clearLogBrackets(logLine).replace(phrase, "");
			JsonElement data = JsonParser.parseString(rawData);
			System.out.println(data);
			if(data.isJsonObject()) {
				if(data.getAsJsonObject().get("type").getAsString().equals("group-statusV2")) {
					JsonObject playerData = JsonParser.parseString(data.getAsJsonObject().get("strData").getAsString()).getAsJsonObject().get("group").getAsJsonObject();

					JsonArray players = playerData.get("players").getAsJsonArray();
					for(JsonElement element : players.asList()) {
						if(element.getAsJsonObject().get("playerId").getAsString().equals(GameStateManager.playerID)) {
							GameStateManager.playerLevel = element.getAsJsonObject().get("masteryLevel").getAsInt();
							actionPerformed = true;
							break;
						}
					}

					JsonArray loadouts = playerData.get("playerLoadouts").getAsJsonArray();
					for(JsonElement element : loadouts.asList()) {
						if(element.getAsJsonObject().get("playerId").getAsString().equals(GameStateManager.playerID)) {
							JsonObject loadout = element.getAsJsonObject().get("loadout").getAsJsonObject();
							if(GameStateManager.location == Location.MENUS) {
								GameStateManager.menuCharacter = Striker.getFromInternalNameNoPrefixSuffix(loadout.get("characterAssetId").getAsString().replace("CD_", ""));
							}
							actionPerformed = true;
							break;
						}
					}
				}
			}
		}
		if(!watcher.isFirstRun() && actionPerformed) {
			GameStateManager.updateStatus();
		}
	}

	/**
	 * Removes the date and three-digit number in brackets from a log entry, if present.
	 * @param logLine
	 * @return
	 */
	public static String clearLogBrackets(String logLine) {
		String findRegex = "^\\[[^\\]]+\\]\\[[^\\]]+\\]\\s*";
		return logLine.replaceFirst(findRegex, "");
	}

	public static void setClosed(boolean closed) {
		LogManager.closed = closed;
	}

	public static boolean isClosed() {
		return closed;
	}
}
