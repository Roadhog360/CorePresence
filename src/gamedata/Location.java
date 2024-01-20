package gamedata;

public enum Location {
	MENUS("Browsing Menus"),
	NORMAL("In Normals"),
	COOP_VS_AI("In Co-Op Vs. A.I."),
	QUICKPLAY("In Quickplay"),
	COMPETITIVE("In Competitive"),
	PRACTICE("Practicing"),
	CUSTOM("In a Custom Lobby"),
	;

	private final String name;

	Location(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return name;
	}
}
