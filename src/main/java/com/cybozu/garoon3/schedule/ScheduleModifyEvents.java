package com.cybozu.garoon3.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * 予定の変更を行ないます。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class ScheduleModifyEvents implements Action {

	private List<Event> events = new ArrayList<Event>();
	
	/**
	 * 変更する予定を追加します。
	 * @param event
	 */
	public void addModifyEvent( Event event ) {
		this.events.add( event );
	}
	
	@Override
	public APIType getAPIType() {
		return APIType.SCHEDULE;
	}

	@Override
	public String getActionName() {
		return "ScheduleModifyEvents";
	}

	@Override
	public OMElement getParameters() {
		return ScheduleUtil.toParameter(events);
	}
}
