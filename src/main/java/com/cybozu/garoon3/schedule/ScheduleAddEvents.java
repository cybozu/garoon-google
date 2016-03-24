package com.cybozu.garoon3.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * 予定の登録を行います。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class ScheduleAddEvents implements Action {

	private List<Event> events = new ArrayList<Event>();
	
	/**
	 * 登録する予定を追加します。
	 * @param event
	 */
	public void addEvent( Event event ) {
		this.events.add( event );
	}
	
	@Override
	public APIType getAPIType() {
		return APIType.SCHEDULE;
	}

	@Override
	public String getActionName() {
		return "ScheduleAddEvents";
	}

	@Override
	public OMElement getParameters() {
		return ScheduleUtil.toParameter(events);
	}
}
