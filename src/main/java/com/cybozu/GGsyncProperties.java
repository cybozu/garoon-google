package com.cybozu;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

public class GGsyncProperties {
	private String GOOGLE_OAUTH_TYPE, GOOGLE_CALENDAR_ID;
	private String GOOGLE_OAUTH_MAIL, GOOGLE_OAUTH_P12KEY;
	private String GOOGLE_OAUTH_CREDENTIAL_FILE, GOOGLE_OAUTH_STORE_DIR;
	private String GOOGLE_CALENDAR_NORMAL_COLOR, GOOGLE_CALENDAR_BANNER_COLOR;
	private String GAROON_URL, GAROON_ACCOUNT, GAROON_PASSWORD;
	private String EXECUTION_LEVEL;
	private Date SYNC_START_DATE, SYNC_END_DATE;
	private Date SYNC_START_DATE_UTC, SYNC_END_DATE_UTC;
	private Integer GAROON_MEMBER_LIMIT;

	GGsyncProperties (String property) throws Exception {
		try {
			Properties prop = new Properties();

			File file = new File(property);
			FileInputStream fis = new FileInputStream(file);
			prop.load(new InputStreamReader(fis, "UTF-8"));

			this.GOOGLE_OAUTH_TYPE = prop.getProperty("google.oauth.type", "").trim();
			if (this.GOOGLE_OAUTH_TYPE.isEmpty()) {
				this.GOOGLE_OAUTH_TYPE = GoogleCalendar.CredentialConfig.AuthType.P12KEY.name();
			}
			this.GOOGLE_OAUTH_MAIL = prop.getProperty("google.oauth.mail").trim();
			this.GOOGLE_OAUTH_P12KEY = prop.getProperty("google.oauth.p12key").trim();
			this.GOOGLE_OAUTH_CREDENTIAL_FILE = prop.getProperty("google.oauth.credential.file", "").trim();
			this.GOOGLE_OAUTH_STORE_DIR = prop.getProperty("google.oauth.credential.storedir", "").trim();
			this.GOOGLE_CALENDAR_ID = prop.getProperty("google.calendar.id").trim();
			this.GOOGLE_CALENDAR_NORMAL_COLOR = prop.getProperty("google.calendar.normal.color", "5").trim();
			this.GOOGLE_CALENDAR_BANNER_COLOR = prop.getProperty("google.calendar.banner.color", "7").trim();

			this.GAROON_URL = prop.getProperty("garoon.url").trim();
			this.GAROON_ACCOUNT = prop.getProperty("garoon.account").trim();
			this.GAROON_PASSWORD = prop.getProperty("garoon.password").trim();

			long syncBeforeDays = Long.parseLong(prop.getProperty("sync.before.days", "1").trim());
			long syncAfterDays = Long.parseLong(prop.getProperty("sync.after.days", "7").trim());

			Date currentDate = new Date();
			this.SYNC_START_DATE = new Date(currentDate.getTime() - 60 * 60 * 24 * 1000 * syncBeforeDays);
			this.SYNC_END_DATE = new Date(currentDate.getTime() + 60 * 60 * 24 * 1000 * syncAfterDays);
			
			this.GAROON_MEMBER_LIMIT = Integer.parseInt(prop.getProperty("garoon.member.limit", "0").trim());

			/**
			 * Garoon APIはUTC以外のタイムゾーンを指定した場合もUTCとして扱われるため、タイムゾーンはJSTで構わない
			 */
			this.SYNC_START_DATE_UTC = new Date(currentDate.getTime() - 60 * 60 * 24 * 1000 * syncBeforeDays - 60 * 60 * 9 * 1000);
			this.SYNC_END_DATE_UTC = new Date(currentDate.getTime() + 60 * 60 * 24 * 1000 * syncAfterDays - 60 * 60 * 9 * 1000);

			this.EXECUTION_LEVEL = prop.getProperty("execution.level", "0").trim();
		} catch(Exception e) {
			throw new Exception("Syntax error: " + property + e);
		}
	}

	public String getGoogleOauthType() {
		return GOOGLE_OAUTH_TYPE;
	}

	public String getGoogleOauthMail() {
		return this.GOOGLE_OAUTH_MAIL;
	}

	public String getGoogleOauthP12key() {
		return this.GOOGLE_OAUTH_P12KEY;
	}

	public String getGoogleOauthCredentialFile() {
		return GOOGLE_OAUTH_CREDENTIAL_FILE;
	}

	public String getGoogleOauthStoreDir() {
		return GOOGLE_OAUTH_STORE_DIR;
	}

	public String getGoogleCalendarId() {
		return this.GOOGLE_CALENDAR_ID;
	}

	public String getGoogleCalendarNormalColor() {
		return this.GOOGLE_CALENDAR_NORMAL_COLOR;
	}

	public String getGoogleCalendarBannerColor() {
		return this.GOOGLE_CALENDAR_BANNER_COLOR;
	}

	public String getGaroonUrl() {
		return this.GAROON_URL;
	}

	public String getGaroonAccount() {
		return this.GAROON_ACCOUNT;
	}

	public String getGaroonPassword() {
		return this.GAROON_PASSWORD;
	}

	public Date getSyncStartDate() {
		return this.SYNC_START_DATE;
	}

	public Date getSyncEndDate() {
		return this.SYNC_END_DATE;
	}

	public Date getSyncStartDateUtc() {
		return this.SYNC_START_DATE_UTC;
	}

	public Date getSyncEndDateUtc() {
		return this.SYNC_END_DATE_UTC;
	}

	public String getExecutionLevel() {
		return this.EXECUTION_LEVEL;
	}
	
	public Integer getGaroonMemberLimit() {
		return this.GAROON_MEMBER_LIMIT;
	}
}