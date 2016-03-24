package com.cybozu.garoon3.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * ユーザーIDとバージョンを取得します。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class BaseGetUserVersions implements Action {
    private final Map<Integer, Integer> users = new HashMap<Integer, Integer>();

    public final OMElement getParameters() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();

        OMElement parameters = omFactory.createOMElement("parameters", null);

        for (Map.Entry<Integer, Integer> e : this.users.entrySet()) {
            OMElement user = omFactory.createOMElement("user_item", null);
            user.addAttribute("id", e.getKey().toString(), null);
            user.addAttribute("version", e.getValue().toString(), null);
            
            parameters.addChild(user);
        }
        return parameters;
    }

    /**
     * クライアント側が保持しているユーザー情報とバージョンを追加します。
     * @param userID ユーザーID
     * @param version バージョン
     */
    public void addUser(int userID, int version) {
        this.users.put(userID, version);
    }

    public final String getActionName() {
        return "BaseGetUserVersions";
    }
    
	public APIType getAPIType() {
		return APIType.BASE;
	}
}
