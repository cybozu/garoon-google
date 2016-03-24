package com.cybozu.garoon3.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * IDを指定した予定の削除を行ないます。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class ScheduleRemoveEvents implements Action {

	private List<Integer> ids = new ArrayList<Integer>();

	/**
	 * 削除する予定IDを追加します。
	 * @param id
	 */
	public void addID( Integer id )	{
		this.ids.add(id);
	}
	
	@Override
	public APIType getAPIType() {
		return APIType.SCHEDULE;
	}

	@Override
	public String getActionName() {
		return "ScheduleRemoveEvents";
	}

	@Override
	public OMElement getParameters() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement parameters = omFactory.createOMElement("parameters", null);
        for( Integer id : ids ){
        	OMElement eventIdNode = omFactory.createOMElement("event_id", null);
        	eventIdNode.setText(id.toString());
        	parameters.addChild(eventIdNode);
        }
		return parameters;
	}
}
