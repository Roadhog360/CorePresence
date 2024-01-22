package logreader;

import com.google.gson.*;
import gamedata.*;
import managers.GameStateManager;

public class LogManager {

	private static boolean closed = false;
	private static final LogWatcher watcher = new LogWatcher();
	private static Location pendingLocation = Location.MENUS;

	public static void init() {
	}

	public static void postInit() {
		watcher.start();
	}

	public static void getActionFor(String logLine) {
		if(performAction(logLine.replaceFirst("\\r$", ""))) {
			try {
				GameStateManager.updateStatus();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean performAction(String logLine) {
		//Gets the username of the player
		String phrase = "LogPMIdentitySubsystem: UPMIdentitySubsystem::HandleSuccessfulLoginResponse - Logged in as user: ";
		if(logLine.startsWith(phrase)) {
			String name = logLine.replace(phrase, "");
			GameStateManager.playerName = name.substring(0, name.indexOf(" with id"));
			String[] contents = name.split(" ");
			GameStateManager.playerID = contents[contents.length - 1];
			System.out.println("Setting player name/ID to: " + GameStateManager.playerName + " : " + GameStateManager.playerID);
			return true;
		}

		//Gets the level of the player, and character
		phrase = "LogPMServicesSubsystem: Warning: UPMServicesSubsystem::ConnectWebSocket::<lambda_964673719509e731966eb292ee6d2929>::operator () - WebSocketConnection->OnMessage: ";
		if(logLine.startsWith(phrase)) {
			String rawData = logLine.replace(phrase, "");
			JsonElement data = JsonParser.parseString(rawData);
			if(data.isJsonObject()) {
				String type = data.getAsJsonObject().get("type").getAsString();
				if(type.equals("group-statusV2")) {
					JsonObject playerData = JsonParser.parseString(data.getAsJsonObject().get("strData").getAsString()).getAsJsonObject().get("group").getAsJsonObject();
					JsonArray players = playerData.get("players").getAsJsonArray();
					for(JsonElement element : players.asList()) {
						if(element.getAsJsonObject().get("playerId").getAsString().equals(GameStateManager.playerID)) {
							int prevLevel = GameStateManager.playerLevel;
							GameStateManager.playerLevel = element.getAsJsonObject().get("masteryLevel").getAsInt();
							if(prevLevel != GameStateManager.playerLevel) {
								System.out.println("Setting player level to: " + GameStateManager.playerLevel);
							}
							break;
						}
					}

					JsonArray loadouts = playerData.get("playerLoadouts").getAsJsonArray();
					for(JsonElement element : loadouts.asList()) {
						if(element.getAsJsonObject().get("playerId").getAsString().equals(GameStateManager.playerID)) {
							JsonObject loadout = element.getAsJsonObject().get("loadout").getAsJsonObject();
							Striker selectedStriker = Striker.getFromInternalName(loadout.get("characterAssetId").getAsString().replace("CD_", ""));
							if(GameStateManager.menuCharacter != selectedStriker) {
								GameStateManager.menuCharacter = selectedStriker;
								System.out.println("Choosing character in menu: " + selectedStriker.getTooltip());
							}
						}
					}
					return true;
				} else if(type.equals("custom-lobby-game-options-v1")) { //Custom games
					JsonObject lobbyData = JsonParser.parseString(data.getAsJsonObject().get("strData").getAsString()).getAsJsonObject().get("gameOptions").getAsJsonObject();
					switch(lobbyData.get("gameFormatId").getAsString()) {
						case "GFD_Ranked":
							pendingLocation = Location.CUSTOM_NORMAL;
							Scoreboard.setMaxValues(3, 3);
							System.out.println("Custom lobby is in: Normal mode");
							break;
						case "GFD_QuickPlay":
							pendingLocation = Location.CUSTOM_QUICKPLAY;
							Scoreboard.setMaxValues(5, 1);
							System.out.println("Custom lobby is in: Quickplay mode");
							break;
						case "GFD_RGM":
							pendingLocation = Location.CUSTOM_TEATIME;
							Scoreboard.setMaxValues(3, 1);
							System.out.println("Custom lobby is in: Tea Time Tussle mode");
							break;
					}
				}
			}
			return false;
		}

		phrase = "LogPMUIDataModel: UPMMatchmakingUIData::UpdateMatchmakingData::<lambda_051a9b8984f58825f631440d1455f646>::operator () - Queue Selection: ";
		if(logLine.startsWith(phrase)) {
			String location = JsonParser.parseString(logLine.replace(phrase, "")).getAsJsonObject().get("queue").getAsString();
			if(!location.equals("queue:custom:NvM")) {
				pendingLocation = Location.getFromKey(location);
				switch(pendingLocation) {
					case COMPETITIVE:
					case NORMAL:
						Scoreboard.setMaxValues(3, 3);
						break;
					case COOP_VS_AI:
					case QUICKPLAY:
						Scoreboard.setMaxValues(5, 1);
						break;
					case PRACTICE:
						Scoreboard.setMaxValues(0, 0);
						break;
				}
			}
			return false;
		}

		phrase = "LogPMGameState: Display: APMGameState::PerformCurrentMatchPhaseEvents - Previous";
		if(logLine.startsWith(phrase)) {
			String[] gameStateInfo = logLine.replace(phrase, "").split(" ");
			String updatedGameState = gameStateInfo[gameStateInfo.length - 1];
			switch (updatedGameState) {
				case "Current[EMatchPhase::ArenaOverview]":
				case "Current[EMatchPhase::InGame]":
					if(Scoreboard.getGameState() == GameProgress.MENU) {
						Scoreboard.resetScoreBoard();
						GameStateManager.location = pendingLocation;
						Scoreboard.setGameState(GameStateManager.location == Location.PRACTICE ? GameProgress.PRACTICE : GameProgress.BEGINNING);
						System.out.println("Setting game phase to beginning");
						GameStateManager.updateTime();
						return true;
					}
					return false;
				case "Current[EMatchPhase::BanSelect]":
					Scoreboard.setGameState(GameProgress.BANNING);
					System.out.println("Setting game phase to banning");
					return true;
				case "Current[EMatchPhase::BanCelebration]":
					Scoreboard.setGameState(GameProgress.BEGINNING);
					System.out.println("Setting game phase back to beginning");
					return true;
				case "Current[EMatchPhase::VersusScreen]":
					Scoreboard.setGameState(GameProgress.IN_GAME);
					System.out.println("Setting game phase to ingame");
					return true;
				case "Current[EMatchPhase::IntermissionMvp]":
					Scoreboard.setGameState(GameProgress.AWAKENING);
					System.out.println("Entering Awakening Draft");
					return true;
				case "Current[EMatchPhase::FaceOffIntro]":
					if (Scoreboard.getGameState() == GameProgress.AWAKENING) {
						Scoreboard.setGameState(GameProgress.IN_GAME);
						System.out.println("Leaving Awakening Draft");
						return true;
					}
					return false;
				case "Current[EMatchPhase::PostGameSummary]":
				case "Current[EMatchPhase::EndGame]": //Used in practice mode
					System.out.println("Resetting game state to menu");
					GameStateManager.setInMenus();
					GameStateManager.ingameCharacter = Striker.NONE;
					return true;
				default:
					break;
			}
			return false;
		}

		//For now we determine character selected by VO data. This is late, there's hopefully a better way to do this
		//This works because when a game starts, your character makes a special voice line during the vs. screen
		phrase = "LogPMVoiceOverManagerComponent: UPMVoiceOverManagerComponent::ProcessNewEvents - Processing New Event 'VOD_";
		if(logLine.startsWith(phrase)) {
			//Practice mode sends CharacterSelect VO lines when changing character
			//There's no team practice atm so we can get away with using that to determine our practice character
			String find = GameStateManager.location == Location.PRACTICE ? "CharacterSelect" : "CharacterIntro";
			if(logLine.contains(find)) {
				String voiceData = logLine.replace(phrase, "");
				voiceData = voiceData.substring(0, voiceData.indexOf("_" + find));
				Striker selectedStriker = Striker.UNKNOWN_UNREGISTERED;
				for (Striker striker : Striker.values()) { //These don't use character internal name for some reason, let's see if it matches any names...
					if (striker == Striker.NONE) {
						continue;
					}
					if (voiceData.equalsIgnoreCase(striker.getAssetKey()) || voiceData.equalsIgnoreCase(striker.getTooltip())) {
						selectedStriker = striker;
						break;
					}
				}
				GameStateManager.ingameCharacter = selectedStriker;
				System.out.println("Choosing character in game: " + selectedStriker.getTooltip());
				return true;
			}
			return false;
		}

		phrase = "LogPMGameState: APMGameState::OnRep_CurrentTerrainData::<lambda_ecb4b71faa12728bcf33e4dfa87f5a6f>::operator () - Changed from Terrain ";
		if(logLine.startsWith(phrase)) {
			String[] stageInfo = logLine.replace(phrase, "")
					.replace("[", "").replace("]", "").replace("GTD_", "").split(" ");
			GameStateManager.arena = Arena.getFromInternalName(stageInfo[stageInfo.length - 1]); //Stage name is at end of string
			System.out.println("Setting stage to: " + GameStateManager.arena.getTooltip());
			return true;
		}

		phrase = "LogPMPlayerState: StreamTeamLevel Called, OldTeam";
		if (logLine.startsWith(phrase)) {
			String[] teamInfo = logLine.replace(phrase, "").split(" ");
			String updatedValue = teamInfo[teamInfo.length - 1]; //Team that we changed to is at end of string
			Scoreboard.setAllyTeamOne(updatedValue.replace("EAssignedTeam::Team", "").equalsIgnoreCase("one"));
			return false;
		}

		phrase = "LogPMGameState: APMGameState::OnRep_MatchScoreInfo - Team";
		if(logLine.startsWith(phrase)) {
			String[] scoreInfo = logLine.replace(phrase, "").split(" ");
			String updatedValue = scoreInfo[scoreInfo.length - 1];//Score to change to is at end of string
			if(logLine.contains("NumPointsThisSet")) { //Team that scored at beginning of scoreInfo (we trimmed the stuff before it off) + score to change to (at end of value)
				Scoreboard.setMaxValues(scoreInfo[0].replace("'s", ""), Integer.parseInt(updatedValue));
			} else {
				if(updatedValue.contains("unset")) {
					return false;
				}
				boolean wonMatch = logLine.contains("TeamThatWonMatch");
				updatedValue = updatedValue.replace("'", "").replace("EAssignedTeam::Team", "");
				if(wonMatch) { //Winning team is in updatedValue position
					String teamWonMatch = updatedValue.replace("'", "").replace("Team", "");
					if (Scoreboard.isAllyTeamOne() == teamWonMatch.equalsIgnoreCase("one")) {
						System.out.println("You won!!!");
						Scoreboard.setGameState(GameProgress.VICTORY);
					} else {
						System.out.println("You lose...");
						Scoreboard.setGameState(GameProgress.DEFEAT);
					}
				} else {
					Scoreboard.incrementSetsWon(updatedValue);
				}
			}
			return true;
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
