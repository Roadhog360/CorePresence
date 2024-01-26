package corepresence.java.gamedata;

import jdk.internal.util.xml.impl.Pair;

public class Scoreboard {
	private static int teamOneScore = 0;
	private static int teamOneSetsWon = 0;
	private static int teamTwoScore = 0;
	private static int teamTwoSetsWon = 0;
	private static boolean allyTeamOne = true;
	private static GameProgress progress = GameProgress.MENU;

	private static int maxScore = 3;
	private static int maxSets = 3;

	public static int getAllyScore() {
		return allyTeamOne ? teamOneScore : teamTwoScore;
	}

	public static int getEnemyScore() {
		return !allyTeamOne ? teamOneScore : teamTwoScore;
	}

	public static int getAllySetsWon() {
		return allyTeamOne ? teamOneSetsWon : teamTwoSetsWon;
	}

	public static int getEnemySetsWon() {
		return !allyTeamOne ? teamOneSetsWon : teamTwoSetsWon;
	}

	public static String getScoreDisplay() {
		StringBuilder scorecard = new StringBuilder();
		for(int i = getMaxSets(); i > 0; i--) {
			scorecard.append(i > getAllySetsWon() ? "_" : "x");
		}
		scorecard.append(getAllyScore());
		scorecard.append(" | ");
		scorecard.append(getEnemyScore());
		for(int i = 0; i < getMaxSets(); i++) {
			scorecard.append(i >= getEnemySetsWon() ? "_" : "x");
		}
		return scorecard.toString();
	}

	public static int getMaxScore() {
		return maxScore;
	}

	public static int getMaxSets() {
		return maxSets;
	}

	public static void setMaxValues(int score, int sets) {
		maxScore = score;
		maxSets = sets;
	}

	public static void setScore(String team, int score) {
		boolean ally = team.equalsIgnoreCase("one") == isAllyTeamOne();
		System.out.println("Changing score for " + (ally ? "ally" : "enemy") + " team to " + score);
		if(team.equalsIgnoreCase("one")) {
			teamOneScore = score;
		} else {
			teamTwoScore = score;
		}
	}

	public static void incrementSetsWon(String team) {
		setsSetsWon(team, (team.equalsIgnoreCase("one") ? teamOneSetsWon : teamTwoSetsWon) + 1);
	}

	public static void setsSetsWon(String team, int score) {
		System.out.println("Changing sets won for " + (team.equalsIgnoreCase("one") == isAllyTeamOne() ? "ally" : "enemy") + " team to " + score);
		if(team.equalsIgnoreCase("one")) {
			teamOneSetsWon = score;
		} else {
			teamTwoSetsWon = score;
		}
	}

	public static void resetScoreBoard() {
		progress = GameProgress.MENU;
		teamOneScore = teamOneSetsWon = teamTwoScore = teamTwoSetsWon = 0;
	}

	public static GameProgress getGameState() {
		return progress;
	}

	public static void setGameState(GameProgress state) {
		progress = state;
	}

	public static boolean isAllyTeamOne() {
		return allyTeamOne;
	}

	public static void setAllyTeamOne(boolean teamOne) {
		System.out.println("Setting ally team to team " + (teamOne ? "one" : "two"));
		allyTeamOne = teamOne;
	}

	public static boolean isInMenus() {
		return progress == GameProgress.QUEUE || progress == GameProgress.MENU;
	}
}
