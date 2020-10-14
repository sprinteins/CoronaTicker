package com.sprinteins.coronaticker;

public enum Risk {

	LOW("34eb37", "\u2705", "Normalbetrieb"), MEDIUM("ebb434", "\u26A0\uFE0F",
			"'Erhöhter Hygiene'-Modus"), HIGH("ff0000", "\u26D4\uFE0F", "Büro geschlossen!");

	private String hexColor;
	private String icon;
	private String message;

	private Risk(String hexColor, String icon, String message) {
		this.hexColor = hexColor;
		this.icon = icon;
		this.message = message;
	}

	public String getHexColor() {
		return hexColor;
	}

	public String getIcon() {
		return icon;
	}

	public String getMessage() {
		return message;
	}

}
