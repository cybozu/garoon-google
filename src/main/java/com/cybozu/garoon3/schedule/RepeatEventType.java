package com.cybozu.garoon3.schedule;
/**
 * 繰り返し予定の繰り返しパターンを表す列挙体です。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public enum RepeatEventType {
	/** 毎日 **/
	DAY,
	/** 毎日(土日を除く) **/
	WEEKDAY,
	/** 毎週 **/
	WEEK,
	/** 毎月第一週 **/
	WEEK_1ST,
	/** 毎月第二週 **/
	WEEK_2ND,
	/** 毎月第三週 **/
	WEEK_3RD,
	/** 毎月第四週 **/
	WEEK_4TH,
	/** 毎月第最終週 **/
	WEEK_LAST,
	/** 毎月 **/
	MONTH
}
