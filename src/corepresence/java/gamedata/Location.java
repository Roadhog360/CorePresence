package corepresence.java.gamedata;

public enum Location {
	MENUS("", "", "In Menus"),
	NORMAL("queue:3v3", "Normal", "Playing Normal"),
	COOP_VS_AI("queue:coopvsai", "Co-Op Vs. AI", "Playing Co-Op Vs. AI"),
	QUICKPLAY("queue:quickplay", "Quickplay", "Playing Quickplay"),
	COMPETITIVE("queue:ranked:3v3", "Competitive", "Playing Competitive"),
	PRACTICE("queue:practice", "Practice", "Practicing"),
	CUSTOM_NORMAL("queue:custom", "Custom: Competitive", "In a Custom Game: Competitive"),
	CUSTOM_QUICKPLAY("queue:custom", "Custom: Quickplay", "In a Custom Game: Quickplay"),
	CUSTOM_TEATIME("queue:custom", "Custom: Tea Time Tussle", "In a Custom Game: Tea Time Tussle"),
	CUSTOM_1V1("queue:custom", "Custom: 1v1 Tournament", "In a Custom Game: 1v1 Tournament"),//Only available using 753.network
	;

	private final String key;
	private final String displayName;
	private final String status;

	Location(String key, String name, String status) {
		this.key = key;
		this.displayName = name;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getDisplayName() {
		return displayName;
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
