package com.cybozu.garoon3.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * ログイン名を元にユーザー情報の取得を行います。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class BaseGetUsersByLoginName implements Action {
	private List<String> loginNames = new ArrayList<String>();
	
	@Override
	public String getActionName() {
		return "BaseGetUsersByLoginName";
	}

	@Override
	public OMElement getParameters() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement parameters = omFactory.createOMElement("parameters", null);

        for (String loginName : this.loginNames) {
            OMElement user = omFactory.createOMElement("login_name", null);
            user.setText(loginName);
            parameters.addChild(user);
        }
        return parameters;
	}

	/**
	 * ログイン名を追加します。
	 * 
	 * @param loginName ログイン名
	 */
	public void addLoginName(String loginName)
	{
		loginNames.add( loginName );
	}
	
	@Override
	public APIType getAPIType() {
		return APIType.BASE;
	}
}
