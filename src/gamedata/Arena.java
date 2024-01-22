package gamedata;

public enum Arena {
	MENU("", "core", "Menus"),

	UNKNOWN_UNREGISTERED("", "unknown_stage", "Unknown"),

	AHTEN_CITY("GameMapAhtenCity", "ahten_city", "Ahten City"),
	AIMI_APP("GameMapDigitalWorld", "aimi_s_app", "Ai.Mi's App"),
	NIGHT_MARKET("GameMapNightMarket", "night_market", "Night Market"),
	ATLAS_LAB("GameMapAtlasLab", "atlas_lab", "Atlas's Lab"),
	DEMON_DAIS("GameMapMusicStage", "demon_dais", "Demon Dais"),
	ONI_VILLAGE("GameMapOniVillage", "oni_village", "Oni Village"),
	INKY_SPLASH_ZONE("GameMapSummerSplash", "inky_s_splash_zone", "Inky's Splash Zone"),
	GATES_OF_OBSCURA("GameMapObscura", "gates_of_obscura", "Gates of Obscura"),
	CLARION_TEST_CHAMBER("GameMapClarionCorpDefault", "clarion_test_chamber_normal", "Clarion Test Chamber"),
	TAIKO_TEMPLE("GameMapDrums", "taiko_temple", "Taiko Temple"),

	CLARION_TEST_CHAMBER_BASSDROP("GameMapClarionCorpProjectThunderstruck", "clarion_test_chamber_project_bassdrop", "Clarion Test Chamber | Project: Bassdrop"),
	CLARION_TEST_CHAMBER_EXPANSE("GameMapClarionCorpProjectExpanse", "clarion_test_chamber_project_expanse", "Clarion Test Chamber | Project: Expanse"),
	CLARION_TEST_CHAMBER_MAELSTROM("GameMapClarionCorpProjectMaelstrom", "clarion_test_chamber_project_maelstrom", "Clarion Test Chamber | Project: Maelstrom"),
	CLARION_TEST_CHAMBER_OBSCURA("GameMapClarionCorpProjectObscura", "clarion_test_chamber_project_obscura", "Clarion Test Chamber | Project: Obscura"),
	CLARION_TEST_CHAMBER_XENO("GameMapClarionCorpProjectXeno", "clarion_test_chamber_project_xeno", "Clarion Test Chamber | Project: Xeno"),

	ABOUT_FACE("GameMapAboutFaceXL", "about_face", "About Face"),
	CORNER_POCKET("GameMapCornerGoalXL", "corner_pocket", "Corner Pocket"),
	MAP_FLIPPED("GameMapFlippedXL", "map_flipped", "Map Flipped"),
	BACK_TO_BACK("GameMapBackToBackXL", "back_to_back", "Back to Back"),
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
		if(name.equals("Practice")) {
			return AHTEN_CITY;
		}
		for(Arena arena : values()) {
			if(arena.getInternalName().replace("GameMap", "").equals(name)) {
				return arena;
			}
		}
		return UNKNOWN_UNREGISTERED;
	}
}
