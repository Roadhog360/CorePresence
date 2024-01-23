package corepresence.java.gamedata;

public enum Location {
	MENUS("", "Browsing Menus"),
	NORMAL("queue:3v3", "In Normals"),
	COOP_VS_AI("queue:coopvsai", "In Co-Op Vs. AI"),
	QUICKPLAY("queue:quickplay", "In Quickplay"),
	COMPETITIVE("queue:ranked:3v3", "In Competitive"),
	PRACTICE("queue:practice", "Practicing"),
	CUSTOM_NORMAL("queue:custom", "In a Custom Game: Normal"),
	CUSTOM_QUICKPLAY("queue:custom", "In a Custom Game: Quickplay"),
	CUSTOM_TEATIME("queue:custom", "In a Custom Game: Tea Time Tussle"),
	;

	private final String key;
	private final String name;

	Location(String key, String name) {
		this.key = key;
		this.name = name;
	}

	public String getDisplayName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public static Location getFromKey(String key) {
		for(Location loc : values()) {
			if(loc.getKey().equals(key)) {
				return loc;
			}
		}
		return MENUS;
	}
}
