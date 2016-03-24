package com.cybozu.garoon3.common;

import java.util.Date;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP12Constants;

import com.cybozu.garoon3.util.DateUtil;

/**
 * APIとのやり取り行うSOAPのヘッダを生成するファクトリクラスです。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class HeaderFactory {

    /**
     * デフォルトコンストラクタ
     */
    private HeaderFactory() {
    }

    /**
     * ヘッダ全体を表すノードを生成します。
     * @return org.apache.axiom.om.OMElement ヘッダ
     */
    private static OMElement getHeaderElement() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace headerNs = omFactory.createOMNamespace(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI, "soapenv");
        OMElement soapHeader = omFactory.createOMElement("Header", headerNs);

        return soapHeader;
    }

    /**
     * ヘッダ内のAPIに関するノードを生成します。
     * @return org.apache.axiom.om.OMElement API情報
     */
    private static OMElement getActionElement(String actionName) {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace actionNs = omFactory.createOMNamespace("http://schemas.xmlsoap.org/ws/2003/03/addressing", "");
        OMElement actionElement = omFactory.createOMElement("Action", actionNs);
        actionElement.addChild(omFactory.createOMText(actionElement, actionName));

        return actionElement;
    }

    /**
     * ヘッダ内のログイン情報に関するノードを生成します。
     * @return org.apache.axiom.om.OMElement ログイン情報
     */
    private static OMElement getSecurityElement(String username, String password) {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace securityNs = omFactory.createOMNamespace("http://schemas.xmlsoap.org/ws/2002/12/secext", "");
        OMElement securityElement = omFactory.createOMElement("Security", securityNs);

        OMElement usernameTokenElement = omFactory.createOMElement("UsernameToken", securityNs);

        OMElement usernameElement = omFactory.createOMElement("Username", securityNs);
        usernameElement.addChild(omFactory.createOMText(usernameElement, username));

        OMElement passwordElement = omFactory.createOMElement("Password", securityNs);
        passwordElement.addChild(omFactory.createOMText(passwordElement, password));

        usernameTokenElement.addChild(usernameElement);
        usernameTokenElement.addChild(passwordElement);
        securityElement.addChild(usernameTokenElement);

        return securityElement;
    }

    /**
     * ヘッダ内のタイムスタンプに関するノードを生成します。
     * @return org.apache.axiom.om.OMElement タイムスタンプ情報
     */
    private static OMElement getTimestampElement(Date createdTime, Date expiredTime) {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace timestampNs = omFactory.createOMNamespace("http://schemas.xmlsoap.org/ws/2002/07/utility", "");
        OMElement timestampElement = omFactory.createOMElement("Timestamp", timestampNs);

        OMElement createdElement = omFactory.createOMElement("Created", timestampNs);
        createdElement.addChild(omFactory.createOMText(createdElement, DateUtil.dateToString(createdTime)));

        OMElement expiresElement = omFactory.createOMElement("Expires", timestampNs);
        expiresElement.addChild(omFactory.createOMText(expiresElement, DateUtil.dateToString(expiredTime)));

        timestampElement.addChild(createdElement);
        timestampElement.addChild(expiresElement);

        return timestampElement;
    }

    /**
     * SOAPリクエストで送るヘッダを作成します。
     * 
     * @param actionName 利用するAPI名
     * @param username APIを利用するユーザー名
     * @param password APIを利用するユーザーのパスワード
     * @param createdTime SOAPメッセージの作成日時
     * @param expiredTime SOAPメッセージの有効期限
     * @return org.apache.axiom.om.OMElement 生成されたヘッダ
     */
    public static OMElement create(String actionName, String username, String password, Date createdTime,
            Date expiredTime) {
        OMElement soapHeader = getHeaderElement();

        OMElement actionElement = getActionElement(actionName);
        soapHeader.addChild(actionElement);

        OMElement securityElement = getSecurityElement(username, password);
        soapHeader.addChild(securityElement);

        OMElement timestampElement = getTimestampElement(createdTime, expiredTime);
        soapHeader.addChild(timestampElement);

        return soapHeader;
    }
}
