package com.cybozu.garoon3.schedule;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
/**
 * 予定の公開先を表すクラスです。<br />
 * 公開先とは、公開方法で「公開先を設定する」を選択した場合に設定するユーザーを指します。<br />
 * 参加者は含みません。<br />
 * <br />
 * 従ってこのクラスは公開方法が「公開」または「非公開」の場合には使用されるべきではありません。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 * @see PublicType
 */
public class Observer {
	private int id;
	private int order;

	/**
	 * 公開先のIDを取得します。
	 * @param id
	 */
	public int getID() {
		return id;
	}

	/**
	 * 公開先のIDを設定します。
	 * @param id
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**
	 * 表示順序を取得します。
	 * @return 表示順序
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

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
