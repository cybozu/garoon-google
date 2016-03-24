package com.cybozu.garoon3.schedule;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
/**
 * 予定の参加者を表すクラスです。<br />
 * <br />
 * 参加者とは、ユーザーだけでなく組織、施設も含まれます。<br />
 * ユーザー、組織、施設を区別するためにこのクラスのインスタンスにはMemberType を指定する必要があります。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 * @see MemberType
 */
public class Member {
	private int id;
	private int order;
	private String name;
	private MemberType type;

	public Member( MemberType type, int id, int order, String name ) {
		this.type = type;
		this.id = id;
		this.order = order;
		this.name = name;
	}

	/**
	 * IDを取得します。
	 * @return ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * IDを設定します。
	 * @param id
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**
	 * 表示順序を取得します。
	 * @return 順序
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * 表示順序を設定します。
	 * @param order 順序
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * 参加者の種別を取得します。
	 * @return 種別
	 */
	public MemberType getType() {
		return type;
	}

	/**
	 * 参加者の種別を設定します。
	 * @param type 種別
	 */
	public void setType(MemberType type) {
		this.type = type;
	}
	
	/**
	 * 名前を取得します。
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 名前を設定します。
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}