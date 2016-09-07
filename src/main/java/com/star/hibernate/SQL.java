package com.star.hibernate;

/**
 * sql查用字符串
 * 
 * @author starhq
 *
 */
public enum SQL {

	AND("AND"), OR("OR"), EQUAL("="), NO_EQUAL("!="), IN("in"), DESC("DESC"), ASC("ASC"), ORDER("ORDER BY"), DELETE(
			"DELETE FROM "), COUNT("SELECT count(*) FROM "), SELECT("SELECT "), FROM("FROM ");

	/**
	 * sql用的字符串
	 */
	private String value;

	private SQL(final String value) {
		this.value = value;
	}

	/**
	 * 直接返回字符串
	 */
	@Override
	public String toString() {
		return value;
	}
}
