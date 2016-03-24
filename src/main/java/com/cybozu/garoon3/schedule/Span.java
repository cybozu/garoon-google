package com.cybozu.garoon3.schedule;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 予定の期間を表すクラスです。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class Span {
	private Date start;
	private Date end;

	/**
	 * 開始日時を取得します。
	 * @return 開始日時
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * 開始日時を設定します。
	 * @param start 開始日時
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * 終了日時を取得します。
	 * @return 終了日時
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * 終了日時を設定します。
	 * @param end 終了日時
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		if( this.start == null )
			builder.append("start=null");
		else
			builder.append("start="+DateFormatUtils.ISO_DATETIME_FORMAT.format(this.start));
		builder.append(", ");
		if( this.end == null )
			builder.append("end=null");
		else
			builder.append("end="+DateFormatUtils.ISO_DATETIME_FORMAT.format(this.end));
		builder.append("]");
		return builder.toString();
	}
}
