package logreader;

import com.google.gson.*;
import gamedata.Location;
import gamedata.Striker;
import managers.GameStateManager;

public class LogManager {

	private static boolean closed = false;
	private static final LogWatcher watcher = new LogWatcher();

	public static void init() {
	}

	public static void postInit() {
		watcher.start();
	}

	public static void getActionFor(String logLine) {
		if(performAction(logLine)) {
			GameStateManager.updateStatus();
		}
	}

	private static boolean performAction(String logLine) {
		//Gets the username of the player
		String phrase = "LogPMIdentitySubsystem: UPMIdentitySubsystem::HandleSuccessfulLoginResponse - Logged in as user: ";
		if(clearLogBrackets(logLine).startsWith(phrase)) {
			String name = clearLogBrackets(logLine).replace(phrase, "").replace("\r", "");
			GameStateManager.playerName = name.substring(0, name.indexOf(" with id"));
			String[] contents = name.split(" ");
			GameStateManager.playerID = contents[contents.length - 1];
			System.out.println("Setting player name/ID to: " + GameStateManager.playerName + " : " + GameStateManager.playerID);
			return true;
		}

		//Gets the level of the player, and character
		phrase = "LogPMServicesSubsystem: Warning: UPMServicesSubsystem::ConnectWebSocket::<lambda_964673719509e731966eb292ee6d2929>::operator () - WebSocketConnection->OnMessage: ";
		if(clearLogBrackets(logLine).startsWith(phrase)) {
			String rawData = clearLogBrackets(logLine).replace(phrase, "");
			JsonElement data = JsonParser.parseString(rawData);
			if(data.isJsonObject()) {
				if(data.getAsJsonObject().get("type").getAsString().equals("group-statusV2")) {
					JsonObject playerData = JsonParser.parseString(data.getAsJsonObject().get("strData").getAsString()).getAsJsonObject().get("group").getAsJsonObject();

					JsonArray players = playerData.get("players").getAsJsonArray();
					for(JsonElement element : players.asList()) {
						if(element.getAsJsonObject().get("playerId").getAsString().equals(GameStateManager.playerID)) {
							int prevLevel = GameStateManager.playerLevel;
							GameStateManager.playerLevel = element.getAsJsonObject().get("masteryLevel").getAsInt();
							if(prevLevel != GameStateManager.playerLevel) {
								System.out.println("Setting player level to: " + GameStateManager.playerLevel);
								return true;
							}
							break;
						}
					}

					JsonArray loadouts = playerData.get("playerLoadouts").getAsJsonArray();
					for(JsonElement element : loadouts.asList()) {
						if(element.getAsJsonObject().get("playerId").getAsString().equals(GameStateManager.playerID)) {
							boolean charaChanged = false;
							JsonObject loadout = element.getAsJsonObject().get("loadout").getAsJsonObject();
							Striker selectedStriker = Striker.getFromInternalNameNoPrefixSuffix(loadout.get("characterAssetId").getAsString().replace("CD_", ""));
							if(GameStateManager.location == Location.MENUS) {
								if(GameStateManager.menuCharacter != selectedStriker) {
									GameStateManager.menuCharacter = selectedStriker;
									charaChanged = true;
								}
							} else {
								if(GameStateManager.ingameCharacter != selectedStriker) {
									GameStateManager.ingameCharacter = selectedStriker;
									charaChanged = true;
								}
							}
							if(charaChanged) {
								System.out.println("Choosing character: " + selectedStriker.getTooltip());
								return true;
							}
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Removes the date and three-digit number in brackets from a log entry, if present.
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
