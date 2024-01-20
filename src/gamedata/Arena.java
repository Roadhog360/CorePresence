package gamedata;

public enum Arena {
	MENU("", "core", "Menus"),

	UNKNOWN_UNREGISTERED("", "unknown_stage", "Unknown"),

	AHTEN_CITY("GameMapAhtenCity", "ahten_city", "Ahten City"),
	AIMI_APP("GameMapDigitalWorld", "aimi_s_app", "Aimi's App"),
	NIGHT_MARKET("GameMapNightMarket", "night_market", "Night Market"),
	ATLAS_LAB("GameMapAtlasLab", "atlas_lab", "Atlas's Lab"),
	DEMON_DAIS("GameMapMusicStage", "demon_dais", "Demon Dais"),
	ONI_VILLAGE("GameMapOniVillage", "oni_village", "Oni Village"),
	INKY_SPLASH_ZONE("GameMapSummerSplash", "inky_s_splash_zone", "Inky's Splash Zone"),
	GATES_OF_OBSCURA("GameMapObscura", "gates_of_obscura", "Gates of Obscura"),
	CLARION_TEST_CHAMBER("GameMapClarionCorp", "clarion_test_chamber_normal", "Clarion Test Chamber"),
	TAIKO_TEMPLE("GameMapDrums", "taiko_temple", "Taiko Temple"),

	CLARION_TEST_CHAMBER_BASSDROP("GameMapClarionCorp", "clarion_test_chamber_project_bassdrop", "Clarion Test Chamber | Project: Bassdrop"),
	CLARION_TEST_CHAMBER_EXPANSE("GameMapClarionCorp", "clarion_test_chamber_project_expanse", "Clarion Test Chamber | Project: Expanse"),
	CLARION_TEST_CHAMBER_MAELSTROM("GameMapClarionCorp", "clarion_test_chamber_project_maelstrom", "Clarion Test Chamber | Project: Maelstrom"),
	CLARION_TEST_CHAMBER_OBSCURA("GameMapClarionCorp", "clarion_test_chamber_project_obscura", "Clarion Test Chamber | Project: Obscura"),
	CLARION_TEST_CHAMBER_XENO("GameMapClarionCorp", "clarion_test_chamber_project_xeno", "Clarion Test Chamber | Project: Xeno"),
	;

	private final String internalName;
	private final String assetKey;
	private final String tooltip;

	Arena(String internalName, String assetKey, String tooltip) {
		this.internalName = internalName;
		this.assetKey = assetKey;
		this.tooltip = tooltip;
	}

	public String getAssetKey() {
		return assetKey;
	}

	public String getTooltip() {
		return tooltip;
	}

	public String getInternalName() {
		return internalName;
	}

	public static Arena getFromInternalName(String name) {
		if(name.equals("GameMapPractice")) {
			return AHTEN_CITY;
		}
		for(Arena arena : values()) {
			if(arena.getInternalName().equals(name)) {
				return arena;
			}
		}
		return UNKNOWN_UNREGISTERED;
	}

	public static Arena getFromInternalNameNoPrefix(String name) {
		if(name.equals("Practice")) {
			return AHTEN_CITY;
		}
		for(Arena arena : values()) {
			if(arena.getInternalName() == null) {
				continue;
			}
			if(arena.getInternalName().replace("GameMap", "").equals(name)) {
				return arena;
			}
		}
		return UNKNOWN_UNREGISTERED;
	}
}
