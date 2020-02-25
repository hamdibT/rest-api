package com.innso.restapi.utils;

/**
 * Enum for canal types
 */
public enum Canal {
	MAIL("MAIL"), SMS("SMS"), FACEBOOK("FACEBOOK"), TWITTER("TWITTER");

	private String canal;

	Canal(String canal) {
		this.canal = canal;
	}

	public String getCanal() {
		return canal;
	}
}
