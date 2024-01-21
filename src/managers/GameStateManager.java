package managers;

import gamedata.*;
import logreader.LogManager;
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
		updateTime();
		Scoreboard.INSTANCE.resetScoreBoard();
		location = Location.MENUS;
		arena = Arena.MENU;
		updateStatus();
	}

	public static void updateTime() {
		currPresence.startTimestamp = System.currentTimeMillis();
		updateStatus();
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

		GameProgress state = Scoreboard.INSTANCE.getGameState();
		Scoreboard score = Scoreboard.INSTANCE;

		currPresence.details = location.getDisplayName();
		if(location.equals(Location.COMPETITIVE) && rank.getTooltip() != null) {
			currPresence.details += " (" + rank.getTooltip() + ")";
		}

		if(state.getDisplayName() == null) {
			currPresence.state = null;
		} else {
			currPresence.state = String.format(state.getDisplayName(),
					(score.getAllyScore() + "-" + score.getAllySetsWon() + " | " +
							score.getEnemyScore() + "-" + score.getEnemySetsWon()));
		}
		if(!LogManager.isClosed()) {
			DiscordRPC.discordUpdatePresence(currPresence);
			DiscordRPC.discordRunCallbacks();
		}
	}

	public static void clearPresence() {
		Scoreboard.INSTANCE.resetScoreBoard();
		GameStateManager.setInMenus();
		GameStateManager.ingameCharacter = GameStateManager.menuCharacter = Striker.NONE;
	}
}
