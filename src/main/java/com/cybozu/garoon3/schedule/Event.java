package com.cybozu.garoon3.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 予定を表すクラスです。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class Event {
	private int id;
	private long version;
	private EventType eventType;
	private PublicType publicType;
	private String plan;
	private String detail;
	private String description;
	private TimeZone timezone;
	private boolean isAllDay;
	private boolean isStartOnly;

	private List<Member> members;

	/**
	 * 参加者を取得します。
	 * @return 参加者のリスト
	 * @see Member
	 */
	public List<Member> getMembers() {
		return this.members;
	}

	/**
	 * 参加者を設定します。
	 * @param members 参加者のリスト
	 */
	public void setMembers(List<Member> members) {
		this.members = members;
	}

	/**
	 * 公開先を取得します。
	 * @return 公開先のリスト
	 */
	public List<Observer> getObservers() {
		return this.observers;
	}

	/**
	 * 公開先を設定します。
	 * @param observers 公開先のリスト
	 */
	public void setObservers(List<Observer> observers) {
		this.observers = observers;
	}

	/**
	 * 会社情報を取得します。
	 * @return 会社情報
	 */
	public Customer getCustomer() {
		return this.customer;
	}

	/**
	 * 会社情報を設定します。
	 * @param customer
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * 繰り返し情報を取得します。
	 * @return 繰り返し情報
	 */
	public RepeatInfo getRepeatInfo() {
		return this.repeatInfo;
	}

	/**
	 * 繰り返し情報を設定します。
	 * @param repeatInfo
	 */
	public void setRepeatInfo(RepeatInfo repeatInfo) {
		this.repeatInfo = repeatInfo;
	}

	/**
	 * 期間を取得します。
	 * @return 期間
	 */
	public List<Span> getSpans() {
		return this.spans;
	}

	/**
	 * 期間を設定します。
	 * @param span 期間
	 */
	public void setSpans(List<Span> spans) {
		this.spans = spans;
	}

	/**
	 * フォロー一覧を取得します。
	 * @return フォロー一覧
	 */
	public List<Follow> getFollows() {
		return this.follows;
	}

	/**
	 * フォロー一覧を設定します。
	 * @param follows フォロー一覧
	 */
	public void setFollows(List<Follow> follows) {
		this.follows = follows;
	}

	private List<Observer> observers;
	private Customer customer;
	private RepeatInfo repeatInfo;
	private List<Span> spans;
	private List<Follow> follows;

	public Event() {
		this.plan = "";
		this.detail = "";
		this.description = "";
		this.eventType = EventType.NORMAL;
		this.publicType = PublicType.PUBLIC;
		this.timezone = TimeZone.getDefault();

		this.members = new ArrayList<Member>();
		this.observers = new ArrayList<Observer>();
		this.follows = new ArrayList<Follow>();
	}

	/**
	 * 予定IDを取得します。
	 * @return 予定ID
	 */
	public int getId() { return this.id; }

	/**
	 * 予定IDを設定します。
	 * @param id 予定ID
	 */
	public void setId(int id) { this.id = id; }

	/**
	 * バージョンを取得します。
	 * @return バージョン
	 */
	public long getVersion() { return this.version; }

	/**
	 * バージョンを設定します。
	 * @param version バージョン
	 */
	public void setVersion(long version) { this.version = version; }

	/**
	 * 予定の種別を取得します。
	 * @return 予定の種別
	 */
	public EventType getEventType() { return this.eventType; }

	/**
	 * 予定の種別を設定します。
	 * @param eventType 予定の種別
	 */
	public void setEventType(EventType eventType) { this.eventType = eventType; }

	/**
	 * 公開方法を取得します。
	 * @return 公開方法
	 */
	public PublicType getPublicType() { return this.publicType; }

	/**
	 * 公開方法を設定します。
	 * @param publicType 公開方法
	 */
	public void setPublicType(PublicType publicType) { this.publicType = publicType; }

	/**
	 * 予定メニューを取得します。
	 * @return 予定メニュー
	 */
	public String getPlan() { return this.plan; }

	/**
	 * 予定メニューを設定します。
	 * @param plan 予定メニュー
	 */
	public void setPlan(String plan) { this.plan = plan; }

	/**
	 * タイトルを取得します。
	 * @return 予定タイトル
	 */
	public String getDetail() { return this.detail; }

	/**
	 * タイトルを設定します。
	 * @param detail 予定タイトル
	 */
	public void setDetail(String detail) { this.detail = detail; }

	/**
	 * メモを取得します。
	 * @return メモ
	 */
	public String getDescription() { return this.description; }

	/**
	 * メモを設定します。
	 * @param description メモ
	 */
	public void setDescription(String description) { this.description = description; }

	/**
	 * タイムゾーンを取得します。
	 * @return TimeZone タイムゾーン
	 */
	public TimeZone getTimezone() { return this.timezone; }

	/**
	 * タイムゾーンを設定します。
	 * @param timezone タイムゾーン
	 */
	public void setTimezone(TimeZone timezone) { this.timezone = timezone; }

	/**
	 * 終日予定かどうかを設定します。
	 * @param isAllDay
	 */
	public void setAllDay(boolean isAllDay) { this.isAllDay = isAllDay; }

	/**
	 * 終日予定かどうかを返します。
	 * @return 終日予定の場合にtrue
	 */
	public boolean isAllDay() { return this.isAllDay; }

	/**
	 *
	 * @param isStartOnly
	 */
	public void setStartOnly(boolean isStartOnly) { this.isStartOnly = isStartOnly; }

	/**
	 *
	 * @return
	 */
	public boolean isStartOnly() { return this.isStartOnly; }

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.MULTI_LINE_STYLE);
	}
}
