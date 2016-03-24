package com.cybozu.garoon3.common;

import org.apache.axiom.om.OMElement;

/**
 * API に対応するアクションを表します。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public interface Action {
    /**
     * 各APIが実装するSOAPリクエストのパラメータ部分を返します。
     * 
     * @return OMElement パラメーター
     */
    OMElement getParameters();

    /**
     * 対応する API の名前を返します。
     * @return String API名
     */
    String getActionName();
    
    /**
     * 対応する API の種別を返します。
     * @return APIType
     */
    APIType getAPIType();
}
