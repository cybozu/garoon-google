package com.cybozu.garoon3.schedule;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 予定に付属する会社情報を表すクラスです。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class Customer {
	private String name;
	//private String nameReading;
	//private String department;
	private String zipCode;
	private String address;
	private String map;
	private String route;
	private String routeTime;
	private String routeFare;
	private String phone;
	//private String fax;
	//private String url;

	/**
	 * 会社名を取得します。
	 * @return 会社名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 会社名を設定します。
	 * @param name 会社名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 郵便番号を取得します。
	 * @return 郵便番号
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * 郵便番号を設定します。
	 * @param zipCode 郵便番号
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * 住所を取得します。
	 * @return 住所
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 住所を設定します。
	 * @param address 住所
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 地図のURLを取得します。
	 * @return 地図URL
	 */
	public String getMap() {
		return map;
	}

	/**
	 * 地図のURLを設定します。
	 * @param map 地図URL
	 */
	public void setMap(String map) {
		this.map = map;
	}

	/**
	 * 路線情報の経路を取得します。
	 * @return 経路
	 */
	public String getRoute() {
		return route;
	}

	/**
	 * 路線情報の経路を設定します。
	 * @param route 経路
	 */
	public void setRoute(String route) {
		this.route = route;
	}

	/**
	 * 路線情報の所要時間を取得します。
	 * @return 所要時間
	 */
	public String getRouteTime() {
		return routeTime;
	}

	/**
	 * 路線情報の所要時間を設定します。
	 * @param routeTime 所要時間
	 */
	public void setRouteTime(String routeTime) {
		this.routeTime = routeTime;
	}

	/**
	 * 路線情報の運賃を取得します。
	 * @return 運賃
	 */
	public String getRouteFare() {
		return routeFare;
	}

	/**
	 * 路線情報の運賃を設定します。
	 * @param routeFare 運賃
	 */
	public void setRouteFare(String routeFare) {
		this.routeFare = routeFare;
	}

	/**
	 * 電話番号を取得します。
	 * @return 電話番号
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 電話番号を設定します。
	 * @param phone 電話番号
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
