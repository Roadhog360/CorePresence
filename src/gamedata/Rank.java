package gamedata;

public enum Rank {
	NONE,

	LOW_ROOKIE("Low Rookie"),
	MID_ROOKIE("Mid Rookie"),
	HIGH_ROOKIE("High Rookie"),
	;

	private final String tooltip;

	Rank(String tooltip) {
		this.tooltip = tooltip;
	}

	Rank() {
		this(null);
	}

	public String getTooltip() {
		return tooltip;
	}
}
