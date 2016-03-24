package com.cybozu.garoon3.schedule;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
/**
 * 予定のフォローを表すクラスです。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class Follow {
	private int id;
	private long version;
	private String text;

	private int creatorId;
	private String creatorName;
	private Date datetime;

	/**
	 * フォローIDを取得します。
	 * @return フォローID
	 */
	public int getId() {
		return id;
	}

	/**
	 * フォローIDを設定します。
	 * @param id フォローID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * バージョンを取得します。
	 * @return バージョン
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * バージョンを設定します。
	 * @param version バージョン
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * 本文を取得します。
	 * @return 本文
	 */
	public String getText() {
		return text;
	}

	/**
	 * 本文を設定します。
	 * @param text 本文
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 登録者IDを取得します。
	 * @return 登録者ID
	 */
	public int getCreatorId() {
		return creatorId;
	}

	/**
	 * 登録者IDを設定します。
	 * @param creatorId 登録者ID
	 */
	public void setCreatorId( int creatorId ) {
		this.creatorId = creatorId;
	}

	/**
	 * 登録者名を取得します。
	 * @return 登録者名
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * 登録者名を設定します。
	 * @param creatorName 登録者名
	 */
	public void setCreatorName( String creatorName ) {
		this.creatorName = creatorName;
	}

	/**
	 * 書き込まれた日付を取得します。
	 * @return 書込み日時
	 */
	public Date getDatetime() {
		return datetime;
	}

	/**
	 * 書き込まれた日付を設定します。
	 * @param datetime 書込み日時
	 */
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
