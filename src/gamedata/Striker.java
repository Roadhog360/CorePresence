package gamedata;

public enum Striker {
	NONE,

	UNKNOWN_UNREGISTERED("", "unknown_striker", "Unknown"),

	JULIETTE("C_FlexibleBrawler_C", "juliette", "Juliette"),
	KAI("C_SpeedySkirmisher_C", "kai", "Kai"),
	DUBU("C_StalwartProtector_C", "dubu", "Dubu"),
	ESTELLE("C_TempoSniper_C", "estelle", "Estelle"),
	ATLAS("C_AngelicSupport_C", "atlas", "Atlas"),
	JUNO("C_CleverSummoner_C", "juno", "Juno"),
	DREKAR("C_NimbleBlaster_C", "drekar", "Drek'ar"),
	RUNE("C_ManipulatingMastermind_C", "rune", "Rune"),
	X("C_HulkingBeast_C", "x", "X"),
	ERA("C_EmpoweringEnchanter_C", "era", "Era"),
	LUNA("C_ChaoticRocketeer_C", "luna", "Luna"),
	ASHER("C_Shieldz_C", "asher", "Asher"),
	AIMI("C_MagicalPlaymaker_C", "aimi", "Ai.Mi"),
	ZENTARO("C_FlashySwordsman_C", "zentaro", "Zentaro"),
	RASMUS("C_WhipFighter_C", "rasmus", "Rasmus"),
	OCTAVIA("C_EDMOni_C", "octavia", "Octavia"),
	VYCE("C_RockOni_C", "vyce", "Vyce"),
	FINII("C_GravityMage_C", "finii", "Finii"),
	KAZAN("C_UmbrellaUser_C", "kazan", "Kazan"),
	NAO("C_Healer_C", "nao", "Nao"),
	MAKO("C_DrumOni_C", "mako", "Mako"),
	NORK("C_SupercalifragilisticexpialidociousUltraMagneticBlobboSupremeRobot_C", "nork", "Nork"),
	;

	private final String internalName;
	private final String assetKey;
	private final String tooltip;

	Striker(String internalName, String assetKey, String tooltip) {
		this.internalName = internalName;
		this.assetKey = assetKey;
		this.tooltip = tooltip;
	}

	Striker() {
		this("", null, null);
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

	public static Striker getFromInternalName(String name) {
		for(Striker striker : values()) {
			if(striker.getInternalName().replace("C_", "").replace("_C", "").equals(name)) {
				return striker;
			}
		}
		return UNKNOWN_UNREGISTERED;
	}
}
