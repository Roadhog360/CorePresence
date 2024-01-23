package corepresence.java.managers;

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
	public static Arena arena = Arena.MENU;
	public static Rank rank = Rank.NONE;

	public static String playerName;
	public static String playerID;
	public static int playerLevel;

	public static void setInMenus() {
		Scoreboard.resetScoreBoard();
		location = Location.MENUS;
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
		currPresence.largeImageText = arena.getTooltip();

		GameProgress state = Scoreboard.getGameState();

		currPresence.details = location.getDisplayName();
		if(location.equals(Location.COMPETITIVE) && rank.getTooltip() != null) {
			currPresence.details += " (" + rank.getTooltip() + ")";
		}

		if(state.getDisplayName() == null) {
			currPresence.state = null;
		} else {
			currPresence.state = String.format(state.getDisplayName(), Scoreboard.getScoreDisplay());
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
