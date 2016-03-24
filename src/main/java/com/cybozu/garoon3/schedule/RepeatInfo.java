package com.cybozu.garoon3.schedule;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 予定の繰り返し情報を表すクラスです。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class RepeatInfo {
	private RepeatEventType type;
	private Date startDate;
	private Date endDate;
	private String startTime;
	private String endTime;
	private int day;
	private int week;

	private List<Span> exclusiveDateTimes;

	/**
	 * 繰り返し種別を取得します。
	 * @return 繰り返し種別
	 */
	public RepeatEventType getType() {
		return type;
	}

	/**
	 * 繰り返し種別を設定します。
	 * @param type 繰り返し種別
	 */
	public void setType(RepeatEventType type) {
		this.type = type;
	}

	/**
	 * 開始日を取得します。
	 * @return 開始日
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * 開始日を設定します。
	 * @param startDate 開始日
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * 終了日を取得します。
	 * @return 終了日
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * 終了日を設定します。
	 * @param endDate 終了日
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * 開始時刻を取得します。
	 * @return 開始時刻
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * 開始時刻を設定します。
	 * @param startTime 開始時刻
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * 終了時刻を取得します。
	 * @return 終了時刻
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * 終了時刻を設定します。
	 * @param endTime 終了時刻
	 * @see RepeatEventType
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * 日を取得します。<br />
	 * この日は、繰り返し種別で「毎月」を指定した場合に利用される日を指します。
	 * @return
	 * @see RepeatEventType
	 */
	public int getDay() {
		return day;
	}

	/**
	 * 日を設定します。<br />
	 * この日は、繰り返し種別で「毎月」を指定した場合に利用される日を指します。
	 * @return
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * 曜日を取得します。<br />
	 * この曜日は、繰り返し種別で「毎週」「毎月第n週」を指定した場合に利用される曜日を指します。
	 * @return
	 * @see RepeatEventType
	 */
	public int getWeek() {
		return week;
	}

	/**
	 * 曜日を設定します。<br />
	 * この曜日は、繰り返し種別で「毎週」「毎月第n週」を指定した場合に利用される曜日を指します。
	 * @return
	 * @see RepeatEventType
	 */
	public void setWeek(int week) {
		this.week = week;
	}

	/**
	 * 除外される期間を取得します。<br />
	 * 繰り返し予定の特定の回が削除されたなどの場合はこの除外期間が指定されます。
	 * @return
	 */
	public List<Span> getExclusiveDateTimes() {
		return exclusiveDateTimes;
	}

	/**
	 * 除外する期間を設定します。
	 * @param exclusiveDateTimes
	 */
	public void setExclusiveDateTimes(List<Span> exclusiveDateTimes) {
		this.exclusiveDateTimes = exclusiveDateTimes;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
