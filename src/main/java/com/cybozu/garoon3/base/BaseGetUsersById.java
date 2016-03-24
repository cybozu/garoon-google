package com.cybozu.garoon3.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * ユーザーIDを元にユーザー情報を取得します。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class BaseGetUsersById implements Action {
	private List<Integer> userIdList = new ArrayList<Integer>();
	
	@Override
	public String getActionName() {
		return "BaseGetUsersById";
	}

	@Override
	public OMElement getParameters() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement parameters = omFactory.createOMElement("parameters", null);

        for (Integer userId : this.userIdList) {
            OMElement user = omFactory.createOMElement("user_id", null);
            user.setText(userId.toString());
            parameters.addChild(user);
        }
        return parameters;
	}
	
	/**
	 * 取得するユーザーのIDを追加します。
	 * 
	 * @param userId ユーザーID
	 */
	public void addUserID(int userId)
	{
		userIdList.add(userId);
	}
	
	@Override
	public APIType getAPIType() {
		return APIType.BASE;
	}
}
