package com.cybozu.garoon3.schedule;

import java.util.Date;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;

/**
 * 予定を検索します。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class ScheduleSearchEvents implements Action {

	private String text;
	private Date start;
	private Date end;
	private Date startForDaily;
	private Date endForDaily;
	private boolean searchTitle = true;
	private boolean searchCustomer = true;
	private boolean searchMemo = true;
	private boolean searchFollow = true;
	private boolean allRepeatEvents = false;

	/**
	 * 検索ワードは必須であるため、コンストラクタで指定します。
	 * @param text
	 */
	public ScheduleSearchEvents( String text ) {
		this.text = text;
	}

	/**
	 * 検索ワードを取得します。
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * 検索ワードを設定します。
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 検索期間の開始日時を取得します。
	 * @return
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * 検索期間の開始日時を設定します。
	 * @return
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * 検索期間の終了日時を取得します。
	 * @return
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * 検索期間の終了日時を設定します。
	 * @return
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	/**
	 * 時刻を含まない検索期間の開始日を取得します。
	 * @return 開始日
	 */
	public Date getStartForDaily() {
		return startForDaily;
	}

	/**
	 * 時刻を含まない検索期間の開始日を設定します。
	 * @param startForDaily 開始日
	 */
	public void setStartForDaily(Date startForDaily) {
		this.startForDaily = startForDaily;
	}

	/**
	 * 時刻を含まない検索期間の終了日を取得します。
	 * @return 終了日
	 */
	public Date getEndForDaily() {
		return endForDaily;
	}

	/**
	 * 時刻を含まない検索期間の終了日を設定します。
	 * @param endForDaily 終了日
	 */
	public void setEndForDaily(Date endForDaily) {
		this.endForDaily = endForDaily;
	}

	/**
	 * タイトルを検索対象に含めるかどうかを取得します。
	 * @return 検索対象に含める場合 true
	 */
	public boolean isSearchTitle() {
		return searchTitle;
	}

	/**
	 * タイトルを検索対象に含めるかどうかを設定します。
	 * @param 検索対象に含める場合 true
	 */
	public void setSearchTitle(boolean searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * 会社情報を検索対象に含めるかどうかを取得します。
	 * @return 検索対象に含める場合にtrue
	 */
	public boolean isSearchCustomer() {
		return searchCustomer;
	}

	/**
	 * 会社情報を検索対象に含めるかどうかを設定します。
	 * @param searchCustomer 検索対象に含める場合にtrue
	 */
	public void setSearchCustomer(boolean searchCustomer) {
		this.searchCustomer = searchCustomer;
	}

	/**
	 * メモを検索対象に含めるかどうかを取得します。
	 * @return 検索対象に含める場合にtrue
	 */
	public boolean isSearchMemo() {
		return searchMemo;
	}

	/**
	 * メモを検索対象に含めるかどうかを指定します。
	 * @param searchMemo 検索対象に含める場合にtrue
	 */
	public void setSearchMemo(boolean searchMemo) {
		this.searchMemo = searchMemo;
	}

	/**
	 * フォローを検索対象に含めるかどうかを指定します。
	 * @return 検索対象に含める場合に true
	 */
	public boolean isSearchFollow() {
		return searchFollow;
	}

	/**
	 * フォローを検索対象に含めるかどうかを設定します。
	 * @param searchFollow 検索対象に含める場合に true
	 */
	public void setSearchFollow(boolean searchFollow) {
		this.searchFollow = searchFollow;
	}

	public boolean isAllRepeatEvents() {
		return allRepeatEvents;
	}

	public void setAllRepeatEvents(boolean allRepeatEvents) {
		this.allRepeatEvents = allRepeatEvents;
	}

	@Override
	public APIType getAPIType() {
		return APIType.SCHEDULE;
	}

	@Override
	public String getActionName() {
		return "ScheduleSearchEvents";
	}

	@Override
	public OMElement getParameters() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement parameters = omFactory.createOMElement("parameters", null);

        parameters.addAttribute("text", this.text, null);
        parameters.addAttribute("title_search", String.valueOf(this.searchTitle), null);
        parameters.addAttribute("customer_search", String.valueOf(this.searchCustomer), null);
        parameters.addAttribute("memo_search", String.valueOf(this.searchMemo), null);
        parameters.addAttribute("follow_search", String.valueOf(this.searchFollow), null);
        parameters.addAttribute("all_repeat_events", String.valueOf(this.allRepeatEvents),null);

        if( this.start != null)
        {
        	String start = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(this.start);
            parameters.addAttribute("start", start, null);
        }

        if( this.end != null )
        {
	        String end = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(this.end);
	        parameters.addAttribute("end", end, null);
        }

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

        return parameters;
	}
}
