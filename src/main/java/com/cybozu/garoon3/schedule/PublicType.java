package com.cybozu.garoon3.schedule;

/**
 * 予定の公開方法を表す列挙体です。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public enum PublicType {
	/** 公開 **/
	PUBLIC,
	/** 非公開 **/
	PRIVATE,
	/** 公開先を設定 **/
	QUALIFIED
}
