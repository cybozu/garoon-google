package com.cybozu.garoon3.common;

import java.net.URI;

import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;

/**
 * APIの接続に関する設定を生成するファクトリクラスです。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class OptionsFactory {
    private OptionsFactory() {
    }

    /**
     * パラメータを元に設定インスタンスを生成します。
     * 
     * @param uri URL
     * @param scheme スキーマ (ex:http,https,...)
     * @param actionName API名
     * @return org.apache.axis2.client.Options パラメータを元に生成された設定
     */
    public static Options create(URI uri, String scheme, String actionName) {
        Options options = new Options();
        EndpointReference targetEPR = new EndpointReference(uri.toString());
        options.setTo(targetEPR);
        options.setTransportInProtocol(scheme);
        options.setProperty(HTTPConstants.CHUNKED, Constants.VALUE_FALSE);
        options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        options.setAction(actionName);

        return options;
    }
}
