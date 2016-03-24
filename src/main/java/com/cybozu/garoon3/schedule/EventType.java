package com.cybozu.garoon3.schedule;

/**
 * 予定の種別を表す列挙体です。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public enum EventType {
	/** 通常予定 **/
	NORMAL,
	/** 繰り返し予定 **/
	REPEAT,
	/** 仮予定 **/
	TEMPORARY,
	/** 期間予定 **/
	BANNER
}
