package com.cybozu.garoon3.base;

import org.apache.axiom.om.OMAbstractFactory;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * ガルーンで動作している各アプリケーションの状態を取得します。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class BaseGetApplicationStatus implements Action {
    private final OMFactory omFactory = OMAbstractFactory.getOMFactory();

    public final OMElement getParameters() {
        OMElement parameters = this.omFactory.createOMElement("parameters", null);
        return parameters;
    }

    public final String getActionName() {
        return "BaseGetApplicationStatus";
    }
    
	@Override
	public APIType getAPIType() {
		return APIType.BASE;
	}
}
