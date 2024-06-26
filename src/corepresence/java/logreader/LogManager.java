package corepresence.java.logreader;

import com.google.gson.*;
import corepresence.java.gamedata.Scoreboard;
import corepresence.java.gamedata.Striker;
import corepresence.java.gamedata.Arena;
import corepresence.java.gamedata.GameProgress;
import corepresence.java.gamedata.Location;
import corepresence.java.managers.GameStateManager;

public class LogManager {

	private static final corepresence.java.logreader.LogWatcher watcher = new corepresence.java.logreader.LogWatcher();

	public static void init() {
	}

	public static void postInit() {
		watcher.start();
	}

	public static boolean getActionFor(String logLine) {
		try {
			return performAction(clearLogBrackets(logLine).replaceFirst("\\r$", ""));
		} catch (Exception e) {
			System.err.println("Error on log line: " + logLine);
			e.printStackTrace();
			return false;
		}
	}

	private static boolean performAction(String logLine) {
		//Gets the username of the player
		String phrase = "LogPMIdentitySubsystem: UPMIdentitySubsystem::HandleSuccessfulLoginResponse - Logged in as user: ";
		String phrase2;
		if(logLine.startsWith(phrase)) {
			String name = logLine.replace(phrase, "");
			GameStateManager.playerName = name.substring(0, name.indexOf(" with id"));
			String[] contents = name.split(" ");
			GameStateManager.playerID = contents[contents.length - 1];
			System.out.println("Setting player name/ID to: " + GameStateManager.playerName + " : " + GameStateManager.playerID);
			return true;
		}

		//Gets the level of the player, and character, as well as custom lobby status
		phrase = "LogPMServicesSubsystem: Warning: UPMServicesSubsystem::ConnectWebSocket::";
		phrase2 = "WebSocketConnection->OnMessage:";
		if(logLine.startsWith(phrase) && logLine.contains(phrase2)) {
			String rawData = logLine.substring(logLine.indexOf(phrase2) + phrase2.length() + 1);
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
							GameStateManager.pendingLocation = Location.CUSTOM_NORMAL;
							Scoreboard.setMaxValues(3, 3);
							System.out.println("Custom lobby is in: Normal mode");
							break;
						case "GFD_QuickPlay":
							GameStateManager.pendingLocation = Location.CUSTOM_QUICKPLAY;
							Scoreboard.setMaxValues(5, 1);
							System.out.println("Custom lobby is in: Quickplay mode");
							break;
						case "GFD_RGM":
							GameStateManager.pendingLocation = Location.CUSTOM_TEATIME;
							Scoreboard.setMaxValues(3, 1);
							System.out.println("Custom lobby is in: Tea Time Tussle mode");
							break;
						case "GFD_1v1Tournament":
							GameStateManager.pendingLocation = Location.CUSTOM_1V1;
							Scoreboard.setMaxValues(3, 2);
							System.out.println("Custom lobby is in: 1v1 Tournament mode");
							break;
						case "GFD_Practice":
							GameStateManager.pendingLocation = Location.PRACTICE;
							Scoreboard.setMaxValues(0, 0);
							System.out.println("Custom lobby is in: Practice mode");
							break;
					}
				}
			}
			return false;
		}

		phrase = "LogPMUIDataModel: UPMMatchmakingUIData::UpdateMatchmakingData::";
		phrase2 = "Matchmaking Status:";
		if(logLine.startsWith(phrase) && logLine.contains(phrase2)) {
			if(GameStateManager.arena == Arena.MENU) {
				JsonObject queueJson = JsonParser.parseString(logLine.substring(logLine.indexOf(phrase2) + phrase2.length() + 1)).getAsJsonObject();
				String queueType = queueJson.get("queued").getAsJsonObject().get("queue").getAsString();
				if (!queueType.equals("queue:custom:NvM") && !queueType.isEmpty()) {
					GameStateManager.pendingLocation = Location.getFromKey(queueType);
					switch (GameStateManager.pendingLocation) {
						case COMPETITIVE:
						case NORMAL:
							Scoreboard.setMaxValues(3, 3);
							break;
						case COOP_VS_AI:
						case QUICKPLAY:
							Scoreboard.setMaxValues(5, 1);
							break;
						case PRACTICE:
						default:
							Scoreboard.setMaxValues(0, 0);
							break;
					}
				}

				String queueStatus = queueJson.get("state").getAsString();
				if (queueStatus.equals("Idle") && GameStateManager.location == Location.MENUS) {
					Scoreboard.setGameState(GameProgress.MENU);
				} else if (Scoreboard.getGameState() != GameProgress.QUEUE && GameStateManager.pendingLocation != Location.PRACTICE && !GameStateManager.pendingLocation.name().toLowerCase().startsWith("custom")) {
					Scoreboard.setGameState(GameProgress.QUEUE);
					GameStateManager.updateTime();
				}
			}
			return true;
		}

		phrase = "LogPMGameState: Display: APMGameState::PerformCurrentMatchPhaseEvents - Previous";
		if(logLine.startsWith(phrase)) {
			String[] gameStateInfo = logLine.replace(phrase, "").split(" ");
			String updatedGameState = gameStateInfo[gameStateInfo.length - 1];
			switch (updatedGameState) {
				case "Current[EMatchPhase::ArenaOverview]":
				case "Current[EMatchPhase::InGame]":
					if(Scoreboard.isInMenus()) {
						Scoreboard.resetScoreBoard();
						GameStateManager.location = GameStateManager.pendingLocation;
						Scoreboard.setGameState(GameStateManager.location == Location.PRACTICE ? GameProgress.PRACTICE : GameProgress.BEGINNING);
						System.out.println("Setting game phase to beginning");
						GameStateManager.updateTime();
						if(GameStateManager.location == Location.PRACTICE) { //Custom practice lobbies made with https://753.network/omega-customs don't play the VO clip when starting practice mode
							GameStateManager.ingameCharacter = Striker.JULIETTE;
						}
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

		phrase = "LogNet: Connection failed; returning to Entry";
		if(logLine.startsWith(phrase)) { //A "kickback" is how I refer to the bug when you are loading into a game and it kicks you back to the main menu
			System.out.println("Resetting game state to menu, kickback detected");
			GameStateManager.setInMenus();
			GameStateManager.ingameCharacter = Striker.NONE;
			return true;
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
				System.out.println("Choosing character in match: " + selectedStriker.getTooltip());
				return true;
			}
			return false;
		}

		if(GameStateManager.ingameCharacter == Striker.NONE && GameStateManager.location != Location.MENUS) {
			//If character isn't chosen, we'll set it when the server corrects the position of the character
			phrase = "LogPMCharacterMovementComponent: Warning: UPMCharacterMovementComponent::OnClientCorrectionReceived - *** Client: Error for ";
			if(logLine.startsWith(phrase)) {
				String correctionData = logLine.replace(phrase, "").replace("C_", "");
				correctionData = correctionData.substring(0, correctionData.indexOf("_"));
				GameStateManager.ingameCharacter = Striker.getFromInternalName(correctionData);
				System.out.println("Choosing character in match: " + GameStateManager.ingameCharacter.getTooltip());
				return true;
			}
		}

		phrase = "LogPMGameState: APMGameState::OnRep_CurrentTerrainData";
		if(logLine.startsWith(phrase)) {
			String[] stageInfo = logLine.replace("[", "").replace("]", "").replace("GTD_", "").split(" ");
			Arena arena = Arena.getFromInternalName(stageInfo[stageInfo.length - 1]);
			if(arena != GameStateManager.arena) {
				GameStateManager.arena = arena; //Stage name is at end of string
				System.out.println("Setting stage to: " + GameStateManager.arena.getTooltip());
				return true;
			}
			return false;
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
				int score = Integer.parseInt(updatedValue);
				String team = scoreInfo[0].replace("'s", "");
				Scoreboard.setScore(team, score);
			} else {
				if(updatedValue.contains("unset")) {
					return false;
				}
				updatedValue = updatedValue.replace("'", "").replace("EAssignedTeam::Team", "");
				if(logLine.contains("TeamThatWonMatch")) { //Winning team is in updatedValue position
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

}
