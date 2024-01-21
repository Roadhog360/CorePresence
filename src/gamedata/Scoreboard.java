package gamedata;

public class Scoreboard {
	private static int teamOneScore = 0;
	private static int teamOneSetsWon = 0;
	private static int teamTwoScore = 0;
	private static int teamTwoSetsWon = 0;
	private static boolean isAllyTeamOne = true;
	private static GameProgress progress = GameProgress.MENU;

	public static int getAllyScore() {
		return isAllyTeamOne ? teamOneScore : teamTwoScore;
	}

	public static int getEnemyScore() {
		return !isAllyTeamOne ? teamOneScore : teamTwoScore;
	}

	public static int getAllySetsWon() {
		return isAllyTeamOne ? teamOneSetsWon : teamTwoSetsWon;
	}

	public static int enemySetsWon() {
		return !isAllyTeamOne ? teamOneSetsWon : teamTwoSetsWon;
	}

	public static void setScore(String team, int score) {
		if(team.equalsIgnoreCase("one")) {
			teamOneScore = score;
		} else {
			teamTwoScore = score;
		}
	}

	public static void incrementSetsWon(String team) {
		setsSetsWon(team, (isAllyTeamOne ? teamOneScore : teamTwoScore) + 1);
	}

	public static void setsSetsWon(String team, int score) {
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
		return isAllyTeamOne;
	}

	public static void setIsAllyTeamOne(boolean teamOne) {
		isAllyTeamOne = teamOne;
	}
}
