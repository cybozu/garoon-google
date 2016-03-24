package com.cybozu.garoon3.common;

/**
 * APIのタイプを表す enum です。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public enum APIType {
	/** システム **/
    ADMIN,
    /** ベース **/
    BASE,
    /** スケジュール **/
    SCHEDULE,
    /** アドレス帳 **/
    ADDRESS,
    /** ワークフロー **/
    WORKFLOW,
    /** メール **/
    MAIL,
    /** メッセージ **/
    MESSAGE,
    /** 通知 **/
    NOTIFICATION,
    /** マルチレポート **/
    REPORT,
    /** ファイル管理 **/
    CABINET,
    /** スター **/
    STAR,
    /** ネット連携 **/
    CBWEBSRV,
    /** ユーティリティ **/
    UTIL;
    
    /**
     * 各 API はタイプによって URL が異なるため、
     * API に接続するクライアントはこのメソッドで必要なパスを取得します。
     * 
     * @return java.lang.String API送受信を行うパス
     */
    public String getPath()
    {
    	String path = "";
        switch(this)
        {
        case ADMIN:
            path = "/sysapi";
            break;
        case UTIL:
            path = "/util_api";
            break;
        default:
            path = "/cbpapi";
            break;
        }
        
        return path + "/" + name().toLowerCase() + "/api";
    }
}
