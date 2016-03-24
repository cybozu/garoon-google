package com.cybozu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class GGsyncDb {
	private Connection CON;
	private Statement STMT;
	private PreparedStatement PS;
	private String TABLE_NAME = "";

	GGsyncDb(String dbName) throws Exception {
		Class.forName("org.sqlite.JDBC");

		this.CON = DriverManager.getConnection("jdbc:sqlite:" + dbName);
		this.STMT = this.CON.createStatement();
		this.STMT.setQueryTimeout(5);
	}

	public void setTable(String tableName) {
		this.TABLE_NAME = tableName;
	}

	public String getTableName() {
		return this.TABLE_NAME;
	}

	public boolean createTable() throws Exception {
		String sql = "CREATE TABLE " + TABLE_NAME
				+ " (garoon_id int primary key, garoon_ver text, google_id text, end_date date)";

		this.STMT.execute(sql);

		return true;
	}

	public boolean existsTable() throws Exception {
		boolean flg = false;

		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
		this.PS = this.CON.prepareStatement(sql);
		this.PS.setString(1, this.TABLE_NAME);

		ResultSet rs = this.PS.executeQuery();
		if (rs.next()) {
			flg = true;
		}

		return flg;
	}

	public boolean addScheduleInfo(int garoonId, long garoonVer, String googleId, Date endTime) throws Exception {
		String sql = "INSERT INTO " + TABLE_NAME + " (garoon_id, garoon_ver, google_id, end_date) VALUES(?, ?, ?, ?)";

		this.PS = this.CON.prepareStatement(sql);
		this.PS.setInt(1, garoonId);
		this.PS.setLong(2, garoonVer);
		this.PS.setString(3, googleId);
		this.PS.setDate(4, new java.sql.Date(endTime.getTime()));

		this.PS.executeUpdate();

		return true;
	}

	public boolean delScheduleInfo(int garoonId) throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE garoon_id=?";

		this.PS = this.CON.prepareStatement(sql);
		this.PS.setInt(1, garoonId);

		this.PS.executeUpdate();

		return true;
	}

	public boolean delScheduleInfoByGoogleId(String googleId) throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE google_id=?";

		this.PS = this.CON.prepareStatement(sql);
		this.PS.setString(1, googleId);

		this.PS.executeUpdate();

		return true;
	}

	/**
	 * グーグルカレンダーから削除すべき（ガルーンのスケジュールから削除された）スケジュールのIDを返す
	 * @param existsScheduleList
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public ArrayList<String> getNeedDeleteGoogleScheduleIdList(ArrayList<Integer> existsScheduleList) throws Exception {
		ArrayList<String> needDeleteGoogleScheduleIdList = new ArrayList<String>();

		String sql = "SELECT google_id FROM " + TABLE_NAME + " WHERE garoon_id NOT IN(";
		sql += StringUtils.join(existsScheduleList, ",") + ")";

		this.PS = this.CON.prepareStatement(sql);

		ResultSet rs = this.PS.executeQuery();
		while (rs.next()) {
			needDeleteGoogleScheduleIdList.add(rs.getString("google_id"));
		}

		return needDeleteGoogleScheduleIdList;
	}

	/**
	 * SQListから古いデータを削除
	 * @param long
	 * @return
	 */
	public boolean delOldScheduleInfo(long timestamp) throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE end_date<?";

		this.PS = this.CON.prepareStatement(sql);
		this.PS.setLong(1, timestamp);

		this.PS.executeUpdate();	

		return true;
	}


	/**
	 * ガルーンのスケジュールがグーグルカレンダーに登録済みか確認する
	 * 登録済みの場合はグーグルカレンダーのIDを返す
	 * @param garoonId
	 * @return
	 */
	public String existsScheduleInfo(int garoonId) throws Exception {
		String google_id = null;

		String sql = "SELECT google_id FROM " + TABLE_NAME + " WHERE garoon_id=?";
		this.PS = this.CON.prepareStatement(sql);
		this.PS.setInt(1, garoonId);

		ResultSet rs = this.PS.executeQuery();
		if (rs.next()) {
			google_id = rs.getString("google_id");
		}

		return google_id;
	}

	/**
	 * ガルーンのスケジュールが更新されているか確認する
	 * 更新されている場合はtrueを返す
	 * @param garoonId
	 * @param garoonVer
	 * @return
	 */
	public boolean confirmUpdatedScheduleInfo(int garoonId, long garoonVer) throws Exception {
		boolean flg = false;

		String sql = "SELECT garoon_ver FROM " + TABLE_NAME + " WHERE garoon_id=? AND garoon_ver<>?";
		this.PS = this.CON.prepareStatement(sql);
		this.PS.setInt(1, garoonId);
		this.PS.setLong(2, garoonVer);

		ResultSet rs = this.PS.executeQuery();
		if (rs.next()) {
			flg = true;
		}

		return flg;
	}

}