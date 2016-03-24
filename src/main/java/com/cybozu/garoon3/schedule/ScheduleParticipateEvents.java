package com.cybozu.garoon3.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;
/**
 * 予定に参加するAPIです。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class ScheduleParticipateEvents implements Action {

	private List<Integer> eventIds = new ArrayList<Integer>();
	
	/**
	 * 参加する予定IDを追加します。
	 * @param id
	 */
	public void addID( Integer id ){
		this.eventIds.add(id);
	}
	
	@Override
	public APIType getAPIType() {
		return APIType.SCHEDULE;
	}

	@Override
	public String getActionName() {
		return "ScheduleParticipateEvents";
	}

	@Override
	public OMElement getParameters() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement parameters = omFactory.createOMElement("parameters", null);
        
        for( Integer id : eventIds )
        {
	        OMElement eventNode = omFactory.createOMElement("event_id", null);
	        eventNode.setText( String.valueOf(id) );
	        parameters.addChild( eventNode );
        }
		
		return parameters;
	}
}
