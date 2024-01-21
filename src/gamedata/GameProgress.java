package gamedata;

public enum GameProgress {
	MENU(null),
	PRACTICE("In practice mode"),
	BANNING("Choosing a Striker to ban"),
	BEGINNING("Choosing an Awakening & Gear"),
	IN_GAME("Score: %s"),
	AWAKENING("Choosing an Awakening"),
	VICTORY("Victory!"),
	DEFEAT("Defeat..."),
	;

	private final String name;

	GameProgress(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return name;
	}
}
