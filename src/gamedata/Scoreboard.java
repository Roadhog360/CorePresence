package gamedata;

public class Scoreboard {
	private int allyScore = 0;
	private int allySetsWon = 0;
	private int enemyScore = 0;
	private int enemySetsWon = 0;
	private GameProgress progress = GameProgress.MENU;

	public static final Scoreboard INSTANCE = new Scoreboard();

	private Scoreboard() {}

	public void setAllyScore(int allyScore) {
		this.allyScore = allyScore;
	}

	public int getAllyScore() {
		return allyScore;
	}

	public void setEnemyScore(int enemyScore) {
		this.enemyScore = enemyScore;
	}

	public int getEnemyScore() {
		return enemyScore;
	}

	public void setAllySetsWon(int allySetsWon, boolean sanitizeScore) {
		if(sanitizeScore) {
			allyScore = 0;
		}
		this.allySetsWon = allySetsWon;
	}

	public int getAllySetsWon() {
		return allySetsWon;
	}

	public void setEnemySetsWon(int enemySetsWon, boolean sanitizeScore) {
		if(sanitizeScore) {
			enemyScore = 0;
		}
		this.enemySetsWon = enemySetsWon;
	}

	public int getEnemySetsWon() {
		return enemySetsWon;
	}

	public void resetScoreBoard() {
		progress = GameProgress.MENU;
		allyScore = allySetsWon = enemyScore = enemySetsWon = 0;
	}

	public GameProgress getGameState() {
		return progress;
	}

	public void setGameState(GameProgress state) {
		progress = state;
	}
}
