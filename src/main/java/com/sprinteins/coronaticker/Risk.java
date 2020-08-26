package com.sprinteins.coronaticker;

public enum Risk {

	LOW("ff0000", "Normalbetrieb"), MEDIUM("ebb434", "'Erhöhter Hygiene'-Modus"), HIGH("34eb37", "Büro geschlossen!");

	private String hexColor;
	private String message;

	private Risk(String hexColor, String message) {
		this.hexColor = hexColor;
		this.message = message;
	}

	public String getHexColor() {
		return hexColor;
	}

	public String getMessage() {
		return message;
	}

}
