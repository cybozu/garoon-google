package com.cybozu.garoon3.schedule;

import java.util.Date;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * 期間と参加者を指定して予定を取得します。<br />
 * 参加者とはユーザーだけに限らず、組織、施設を指定することができます。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class ScheduleGetEventsByTarget implements Action {

	private int id;
	private MemberType type;
	private Date start;
	private Date end;
	private Date startForDaily;
	private Date endForDaily;

	/**
	 * 時刻を持たない検索期間の開始日を取得します。
	 * @return
	 */
	public Date getStartForDaily() {
		return startForDaily;
	}

	/**
	 * 時刻を持たない検索期間の開始日を設定します。
	 * @param startForDaily
	 */
	public void setStartForDaily(Date startForDaily) {
		this.startForDaily = startForDaily;
	}

	/**
	 * 時刻を持たない検索期間の終了日を取得します。
	 * @return
	 */
	public Date getEndForDaily() {
		return endForDaily;
	}

	/**
	 * 時刻を持たない検索期間の終了日を設定します。
	 * @param endForDaily
	 */
	public void setEndForDaily(Date endForDaily) {
		this.endForDaily = endForDaily;
	}

	/**
	 * 参加者を指定します。参加者はユーザー以外にも、組織、施設を指定することができます。
	 * @param type 種別
	 * @param id
	 */
	public void setMember( MemberType type, int id )
	{
		this.id = id;
		this.type = type;
	}

	/**
	 * 検索期間の開始日を取得します。
	 * @return
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * 検索期間の開始日を設定します。
	 * @param start
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * 検索期間の終了日を取得します。
	 * @return
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * 検索期間の終了日を設定します。
	 * @param end
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public APIType getAPIType() {
		return APIType.SCHEDULE;
	}

	@Override
	public String getActionName() {
		return "ScheduleGetEventsByTarget";
	}

	@Override
	public OMElement getParameters() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement parameters = omFactory.createOMElement("parameters", null);

        String start = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(this.start);
        String end = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(this.end);

        parameters.addAttribute("start", start, null);
        parameters.addAttribute("end", end, null);

        if( this.startForDaily != null )
        {
        	String startForDaily = DateFormatUtils.ISO_DATE_FORMAT.format(this.startForDaily);
        	parameters.addAttribute("startForDaily", startForDaily, null);
        }

        if( this.endForDaily != null )
        {
        	String endForDaily = DateFormatUtils.ISO_DATE_FORMAT.format(this.endForDaily);
        	parameters.addAttribute("endForDaily", endForDaily, null);
        }

        //set id
        OMElement memberNode = omFactory.createOMElement(this.type.toString().toLowerCase(), null);
        memberNode.addAttribute("id", String.valueOf(this.id), null);
        parameters.addChild( memberNode );

        return parameters;
	}
}
