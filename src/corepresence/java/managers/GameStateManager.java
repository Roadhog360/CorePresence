package corepresence.java.managers;

import corepresence.java.Main;
import corepresence.java.gamedata.Arena;
import corepresence.java.gamedata.GameProgress;
import corepresence.java.gamedata.Location;
import corepresence.java.gamedata.Rank;
import corepresence.java.gamedata.Scoreboard;
import corepresence.java.gamedata.Striker;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class GameStateManager { //1869590 OS Steamapp ID

	private static final DiscordRichPresence currPresence = new DiscordRichPresence();

	public static Striker ingameCharacter = Striker.NONE;
	public static Striker menuCharacter = Striker.NONE;

	public static Location location = Location.MENUS;
	public static Location pendingLocation = Location.MENUS;
	public static Arena arena = Arena.MENU;
	public static Rank rank = Rank.NONE;

	public static String playerName;
	public static String playerID;
	public static int playerLevel;

	public static void setInMenus() {
		Scoreboard.resetScoreBoard();
		location = pendingLocation = Location.MENUS;
		arena = Arena.MENU;
		updateTime();
	}

	public static void updateTime() {
		currPresence.startTimestamp = System.currentTimeMillis();
	}

	public static void updateStatus() {
		Striker chara = location == Location.MENUS ? menuCharacter : ingameCharacter;
		currPresence.smallImageKey = chara.getAssetKey();
		if(chara == Striker.NONE) {
			currPresence.smallImageText = null;
		} else {
			StringBuilder charaTooltip = new StringBuilder();
			charaTooltip.append(chara.getTooltip());
			if (playerLevel > 0) {
				charaTooltip.append(" | Lv. ").append(playerLevel);
			}
			currPresence.smallImageText = charaTooltip.toString();
		}

		currPresence.largeImageKey = arena.getAssetKey();
		currPresence.largeImageText = arena.getTooltip() + " (CorePresence " + Main.version + ")";

		if(Scoreboard.getGameState() == GameProgress.QUEUE) {
			StringBuilder queueStatus = new StringBuilder(Scoreboard.getGameState().getDisplayName());
			if(pendingLocation.getDisplayName().isEmpty()) {
				queueStatus.append(" (").append(pendingLocation.getDisplayName()).append(")");
			}
			currPresence.details = String.format(Scoreboard.getGameState().getDisplayName());
		} else {
			currPresence.details = location.getStatus();
		}
		if(!rank.getTooltip().isEmpty() && (location == Location.COMPETITIVE || (pendingLocation == Location.COMPETITIVE && Scoreboard.getGameState() == GameProgress.QUEUE))) {
			currPresence.details += " (" + rank.getTooltip() + ")";
		}

		if(Scoreboard.getGameState().getDisplayName().isEmpty() || Scoreboard.getGameState() == GameProgress.QUEUE) {
			currPresence.state = null;
		} else {
			String state = Scoreboard.getGameState().getDisplayName();
			if(Scoreboard.getGameState() == GameProgress.IN_GAME) {
				state = String.format(state, Scoreboard.getScoreDisplay());
			}
			currPresence.state = state;
		}
		DiscordRPC.discordUpdatePresence(currPresence);
		DiscordRPC.discordRunCallbacks();
	}

	public static void resetValues() {
		Scoreboard.resetScoreBoard();
		GameStateManager.setInMenus();
		GameStateManager.ingameCharacter = GameStateManager.menuCharacter = Striker.NONE;
	}
}
