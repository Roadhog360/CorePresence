package corepresence.java.gamedata;

public enum Arena {
	MENU("", "core", "Menus"),

	UNKNOWN_UNREGISTERED("", "unknown_stage", "Unknown"),

	AHTEN_CITY("GTD_AhtenCity", "ahten_city", "Ahten City"),
	AIMI_APP("GTD_DigitalWorld", "aimi_s_app", "Ai.Mi's App"),
	NIGHT_MARKET("GTD_NightMarket", "night_market", "Night Market"),
	ATLAS_LAB("GTD_Lab", "atlas_lab", "Atlas's Lab"),
	DEMON_DAIS("GTD_MusicStage", "demon_dais", "Demon Dais"),
	ONI_VILLAGE("GTD_OniVillage", "oni_village", "Oni Village"),
	INKY_SPLASH_ZONE("GTD_SummerSplash", "inky_s_splash_zone", "Inky's Splash Zone"),
	GATES_OF_OBSCURA("GTD_Obscura", "gates_of_obscura", "Gates of Obscura"),
	CLARION_TEST_CHAMBER("GTD_ClarionCorpDefault", "clarion_test_chamber_normal", "Clarion Test Chamber"),
	TAIKO_TEMPLE("GTD_Drums", "taiko_temple", "Taiko Temple"),

	CLARION_TEST_CHAMBER_BASSDROP("GTD_ClarionCorpProjectThunderstruck", "clarion_test_chamber_project_bassdrop", "Clarion Test Chamber | Project: Bassdrop"),
	CLARION_TEST_CHAMBER_EXPANSE("GTD_ClarionCorpProjectExpanse", "clarion_test_chamber_project_expanse", "Clarion Test Chamber | Project: Expanse"),
	CLARION_TEST_CHAMBER_MAELSTROM("GTD_ClarionCorpProjectMaelstrom", "clarion_test_chamber_project_maelstrom", "Clarion Test Chamber | Project: Maelstrom"),
	CLARION_TEST_CHAMBER_OBSCURA("GTD_ClarionCorpProjectObscura", "clarion_test_chamber_project_obscura", "Clarion Test Chamber | Project: Obscura"),
	CLARION_TEST_CHAMBER_XENO("GTD_ClarionCorpProjectXeno", "clarion_test_chamber_project_xeno", "Clarion Test Chamber | Project: Xeno"),

	ABOUT_FACE("GTD_AboutFaceXL", "about_face", "About Face"),
	CORNER_POCKET("GTD_CornerGoalXL", "corner_pocket", "Corner Pocket"),
	MAP_FLIPPED("GTD_FlippedXL", "map_flipped", "Map Flipped"),
	BACK_TO_BACK("GTD_BackToBackXL", "back_to_back", "Back to Back"),
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
			if(arena.getInternalName().replace("GTD_", "").equals(name)) {
				return arena;
			}
		}
		return UNKNOWN_UNREGISTERED;
	}
}
