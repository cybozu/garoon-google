package com.cybozu;

import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;
import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;

import com.cybozu.garoon3.base.BaseGetUsersByLoginName;
import com.cybozu.garoon3.common.CBServiceClient;
/**
 * グーグルカレンダーのクラス名と重複しているためインポートしない
 * import com.cybozu.garoon3.schedule.Event;
 */
import com.cybozu.garoon3.schedule.EventType;
import com.cybozu.garoon3.schedule.Follow;
import com.cybozu.garoon3.schedule.Member;
import com.cybozu.garoon3.schedule.MemberType;
import com.cybozu.garoon3.schedule.ScheduleGetEventsByTarget;
import com.cybozu.garoon3.schedule.ScheduleGetFacilitiesById;
import com.cybozu.garoon3.schedule.ScheduleUtil;
import com.cybozu.garoon3.schedule.RepeatInfo;
import com.cybozu.garoon3.schedule.Span;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import com.cybozu.GoogleCalendar.CredentialConfig.AuthType;

public class GGsync {
	private static final String GGSYNC_TABLE_NAME = "ggsync";
	private static final String CRLF = System.getProperty("line.separator");

	private static final Logger LOGGER = LogManager.getLogger(GGsync.class);

	public static void main(String[] args) throws ParseException {
		if(args.length!=1 || !new File(args[0]).isDirectory()) {
			System.err.println("Usage) java -jar GGsync.jar PROPERITIES_DIR");
			System.err.println("       java -jar GGsync.jar C:" + File.separator + "GGsync" + File.separator);
			System.exit(1);
		}

		try {
			File ggsyncDir = new File(args[0]);

			/** ログファイルの出力先を指定 **/
			ThreadContext.put("filepath", ggsyncDir.getPath());

			String ggsyncPropertiesFile = ggsyncDir.getPath() + File.separator + "GGsync.properties";
			String ggsyncDbFile = ggsyncDir.getPath() + File.separator + "GGsync.db";

			if(!new File(ggsyncPropertiesFile).isFile()) {
				throw new Exception("Can't read file: " + ggsyncPropertiesFile);
			}

			LOGGER.debug("GGsync config file: " + ggsyncPropertiesFile);
			LOGGER.debug("GGsync sqlite file: " + ggsyncDbFile);

			/** 設定ファイルから値を取得 **/
			GGsyncProperties ggsyncProperties = new GGsyncProperties(ggsyncPropertiesFile);

			if(Integer.parseInt(ggsyncProperties.getExecutionLevel())==0) {
				/** 通常モード（INFO） **/
				LOGGER.info("The sync beginning with normal mode.");
			} else {
				/** デバッグモード（DEBUG） **/
				LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
				Configuration conf = ctx.getConfiguration();
				conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.DEBUG);
				conf.getLoggerConfig("com.cybozu").setLevel(Level.DEBUG);
				conf.getLoggerConfig("com.cybozu").setAdditive(false);
				ctx.updateLoggers(conf);

				LOGGER.info("The sync beginning with debug mode.");
			}



			String googleAuthType = ggsyncProperties.getGoogleOauthType();
			String googleOauthMail = ggsyncProperties.getGoogleOauthMail();
			String googleOauthP12key = ggsyncProperties.getGoogleOauthP12key();
			String googleCredentialFile = ggsyncProperties.getGoogleOauthCredentialFile();
			String googleCredentialStoreDir = ggsyncProperties.getGoogleOauthStoreDir();
			String googleCalendarId = ggsyncProperties.getGoogleCalendarId();
			String googleCalendarNormalColor = ggsyncProperties.getGoogleCalendarNormalColor();
			String googleCalendarBannerColor = ggsyncProperties.getGoogleCalendarBannerColor();
			String garoonUrl = ggsyncProperties.getGaroonUrl();
			String garoonAccount = ggsyncProperties.getGaroonAccount();
			String garoonPassword = ggsyncProperties.getGaroonPassword();
			Date syncStartDate = ggsyncProperties.getSyncStartDate();
			Date syncEndDate = ggsyncProperties.getSyncEndDate();
			Date syncStartDateUtc = ggsyncProperties.getSyncStartDateUtc();
			Date syncEndDateUtc = ggsyncProperties.getSyncEndDateUtc();
			Integer garoonMemberLimit = ggsyncProperties.getGaroonMemberLimit();

			LOGGER.debug("グーグルカレンダーの認証方式: " + googleAuthType);
			LOGGER.debug("サービスアカウントのメールアドレス: " + googleOauthMail);
			LOGGER.debug("サービスアカウントのP12キーファイルの絶対パス: " + googleOauthP12key);
			LOGGER.debug("OAuth2のclient_idファイルの絶対パス: " + googleCredentialFile);
			LOGGER.debug("OAuth2の認証情報保存先の絶対パス: " + googleCredentialStoreDir);
			LOGGER.debug("グーグルカレンダーID: " + googleCalendarId);
			LOGGER.debug("グーグルカレンダーに登録する通常予定の色: " + googleCalendarNormalColor);
			LOGGER.debug("グーグルカレンダーに登録する期間予定の色: " + googleCalendarBannerColor);
			LOGGER.debug("ガルーンのURL: " + garoonUrl);
			LOGGER.debug("ガルーンのアカウント: " + garoonAccount);
			//LOGGER.debug("ガルーンのパスワード: " + garoonPassword);
			LOGGER.debug("SYNC対象の開始時間: " + syncStartDate);
			LOGGER.debug("SYNC対象の終了時間: " + syncEndDate);

			GoogleCalendar.CredentialConfig config =
					new GoogleCalendar.CredentialConfig();
			config.setAuthType(AuthType.valueOf(googleAuthType));
			config.setMail(googleOauthMail);
			config.setP12key(googleOauthP12key);
			config.setCredentialFile(googleCredentialFile);
			config.setCredentialStoreDir(googleCredentialStoreDir);


			/** SQLiteの利用設定 **/
			GGsyncDb ggsyncDb = new GGsyncDb(ggsyncDbFile);
			ggsyncDb.setTable(GGSYNC_TABLE_NAME);

			/** * SQLiteにテーブルが存在しない場合、テーブルを作成 **/
			if(!ggsyncDb.existsTable()) {
				ggsyncDb.createTable();
			}



			int garoonUid;
			GaroonSchedular garoonSchedular = new GaroonSchedular();



			/** ガルーンから削除されたスケジュールをグーグルカレンダーからも削除するために利用 **/
			ArrayList<Integer> garoonScheduleIdList = new ArrayList<Integer>();

			GoogleCalendar googleCalendar = new GoogleCalendar(config);
			googleCalendar.setCalendarName(googleCalendarId);


			CBServiceClient cbClient = new CBServiceClient();
			cbClient.setUser(garoonAccount, garoonPassword);
			cbClient.setGaroonURI(new URI(garoonUrl));

			/** ガルーンのログイン名と紐付ぐUIDを取得 **/
			BaseGetUsersByLoginName baseGetUsers = new BaseGetUsersByLoginName();
			baseGetUsers.addLoginName(garoonAccount);
			OMElement garoonUserInfo = cbClient.sendReceive(baseGetUsers);
			garoonUid = garoonSchedular.getUid(garoonUserInfo);
			Integer apiVersion = cbClient.getApiVersion();

			/** ガルーンのスケジュールを取得 **/
			ScheduleGetEventsByTarget scheduleGetEvents = new ScheduleGetEventsByTarget();
			scheduleGetEvents.setStart(syncStartDateUtc);
			scheduleGetEvents.setStartForDaily(syncStartDateUtc);
			scheduleGetEvents.setEnd(syncEndDateUtc);
			scheduleGetEvents.setEndForDaily(syncEndDateUtc);
			scheduleGetEvents.setMember(MemberType.USER, garoonUid);

			OMElement scheduleEvents = cbClient.sendReceive(scheduleGetEvents);
			List<com.cybozu.garoon3.schedule.Event> garoonSchedules = ScheduleUtil.getEventList(scheduleEvents, apiVersion);
			for (Iterator<com.cybozu.garoon3.schedule.Event> i = garoonSchedules.iterator(); i.hasNext();) {
				int garoonScheduleId;
				String googleScheduleId, scheduleTitle, scheduleLocation = "", scheduleColor = "1";
				String scheduleDescription = "", scheduleMemo = "", scheduleMembers = "", scheduleComments = "";
				Date scheduleStart = null, scheduleEnd = null;
				Date scheduleAllEnd = null; // 繰り返し予定も含めた最後の時間
				long garoonScheduleVersion;
				ArrayList<String> recurrenceList = new ArrayList<String>();
				TimeZone scheduleTimezone = null;

				/** ガルーンのスケジュール情報 **/
				com.cybozu.garoon3.schedule.Event garoonSchedule = i.next();
				garoonScheduleId = garoonSchedule.getId();
				garoonScheduleIdList.add(garoonScheduleId);
				garoonScheduleVersion = garoonSchedule.getVersion();
				if (garoonSchedule.getPlan().isEmpty()) {
					scheduleTitle = garoonSchedule.getDetail();
				} else {
					scheduleTitle = garoonSchedule.getPlan() + ": " + garoonSchedule.getDetail();
				}				
				scheduleMemo = "▽ メモ ▽" + CRLF + garoonSchedule.getDescription();
				scheduleTimezone = garoonSchedule.getTimezone();



				/** スケジュールが登録済みか確認。登録済みの場合、戻り値はグーグルカレンダーのID **/
				String registeredGoogleScheduleId = ggsyncDb.existsScheduleInfo(garoonScheduleId);
				if (registeredGoogleScheduleId != null && !registeredGoogleScheduleId.isEmpty()) {
					if (!ggsyncDb.confirmUpdatedScheduleInfo(garoonScheduleId, garoonScheduleVersion)) {
						/** グーグルカレンダーに登録済みで、更新の無いスケジュール **/
						//LOGGER.debug("[NOTHING for registered schedule] GaroonId:{} GoogleId:{} Title:{}", 
						//		garoonScheduleId, registeredGoogleScheduleId, scheduleTitle);
						continue;
					} else {
						/** グーグルカレンダーに登録済みで、更新されたスケジュール **/
						if(LOGGER.isDebugEnabled()) {
							LOGGER.debug("[DEL] for updated garoon schedule. GaroonId:{} GoogleId:{} Title:{}",
									garoonScheduleId, registeredGoogleScheduleId, scheduleTitle);
						} else if (LOGGER.isInfoEnabled()) {
							LOGGER.info("[DEL] for updated garoon schedule. Title:{}", scheduleTitle);
						}

						/** グーグルカレンダーのスケジュールを削除 **/
						try {
							googleCalendar.delSchedule(registeredGoogleScheduleId);
						} catch (GoogleJsonResponseException e) {
							LOGGER.info("[FAILED] google schedule is already deleted. GoogleId:{}, Message:{}", registeredGoogleScheduleId, e.getMessage());
						}

						/** SQLiteのレコードを削除 **/
						ggsyncDb.delScheduleInfo(garoonScheduleId);
					}
				}



				if (garoonSchedule.getEventType() == EventType.NORMAL) {
					/**
					 * 通常予定
					 */
					Span span = garoonSchedule.getSpans().get(0);
					if (!garoonSchedule.isAllDay()) {
						scheduleStart = new Date(span.getStart().getTime());
						if (garoonSchedule.isStartOnly()) {
							/** 開始時間のみのスケジュールの場合は終了時間に開始時間をセット **/
							scheduleEnd = scheduleStart;
						} else {
							scheduleEnd = new Date(span.getEnd().getTime());
						}
					} else if (garoonSchedule.isAllDay()) {
						scheduleStart = new Date(span.getStart().getTime());
						scheduleEnd = new Date(span.getEnd().getTime() + 24 * 60 * 60 * 1000);
					}
					scheduleColor = googleCalendarNormalColor;
					scheduleAllEnd = scheduleEnd;
				} else if (garoonSchedule.getEventType() == EventType.BANNER) {
					/**
					 * 期間予定
					 */
					Span span = garoonSchedule.getSpans().get(0);
					scheduleStart = new Date(span.getStart().getTime());
					scheduleEnd = new Date(span.getEnd().getTime() + 24 * 60 * 60 * 1000);
					scheduleColor = googleCalendarBannerColor;
					scheduleAllEnd = scheduleEnd;
				} else if (garoonSchedule.getEventType() == EventType.TEMPORARY) {
					/**
					 * 仮予定
					 * 
					 * TEMPORARYの場合はSpanが複数
					 */
					Span span = garoonSchedule.getSpans().get(0);
					scheduleStart = new Date(span.getStart().getTime());
					scheduleEnd = new Date(span.getEnd().getTime());
					scheduleAllEnd = scheduleEnd;

					Iterator<Span> e = garoonSchedule.getSpans().iterator();
					ArrayList<String> alternativeList = new ArrayList<String>();
					while (e.hasNext()) {
						Span n = e.next();
						alternativeList.add(
								googleCalendar.getTemporaryDate(new Date(n.getStart().getTime()), new Date(n.getEnd().getTime())));
					}
					if (alternativeList.size() > 0) {
						recurrenceList.add("RDATE;VALUE=PERIOD:" + StringUtils.join(alternativeList, ","));
					}
				} else if (garoonSchedule.getEventType() == EventType.REPEAT) {
					/**
					 * 繰り返し予定
					 */
					Date until = null;
					RepeatInfo repeatInfo = garoonSchedule.getRepeatInfo();

					Span span = garoonSchedule.getSpans().get(0);
					if (!garoonSchedule.isAllDay()) {
						scheduleStart = new Date(span.getStart().getTime());
						scheduleEnd = new Date(span.getEnd().getTime());

						DateFormat startTimeFormatter = new SimpleDateFormat("HH:mm:ss");
						Date startTime = (Date)startTimeFormatter.parse(repeatInfo.getStartTime());
						/** starttime.getTime()はタイムゾーンが考慮されるため、TimeZone.getDefault().getRawOffset()を追加 **/
						until = new Date(repeatInfo.getEndDate().getTime() + startTime.getTime() + TimeZone.getDefault().getRawOffset());
					} else if (garoonSchedule.isAllDay()) {
						scheduleStart = new Date(span.getStart().getTime());
						scheduleEnd = new Date(span.getEnd().getTime() + 24 * 60 * 60 * 1000);
						until = new Date(repeatInfo.getEndDate().getTime());
					}
					scheduleColor = googleCalendarNormalColor;
					scheduleAllEnd = until;



					switch (repeatInfo.getType()) {
					case DAY: /** 毎日 **/
						recurrenceList.add(googleCalendar.getRecurrenceListDaily(until));
						break;
					case WEEK: /** 毎週 **/
						recurrenceList.add(googleCalendar.getRecurrenceListWeekly(until));
						break;
					case WEEKDAY: /** 毎日（土日を除く） **/
						recurrenceList.add(googleCalendar.getRecurrenceListWeekday(until));
						break;
					case WEEK_1ST: /** 毎月第一週 **/
						recurrenceList.add(googleCalendar.getRecurrenceList1stweek(until, repeatInfo.getWeek()));
						break;
					case WEEK_2ND: /** 毎月第二週 **/
						recurrenceList.add(googleCalendar.getRecurrenceList2ndweek(until, repeatInfo.getWeek()));
						break;
					case WEEK_3RD: /** 毎月第三週 **/
						recurrenceList.add(googleCalendar.getRecurrenceList3rdweek(until, repeatInfo.getWeek()));
						break;
					case WEEK_4TH: /** 毎月第四週 **/
						recurrenceList.add(googleCalendar.getRecurrenceList4thweek(until, repeatInfo.getWeek()));
						break;
					case WEEK_LAST: /** 毎月最終週 **/
						recurrenceList.add(googleCalendar.getRecurrenceListLastweek(until, repeatInfo.getWeek()));
						break;
					case MONTH: /** 毎月 **/
						recurrenceList.add(googleCalendar.getRecurrenceListMonthly(until));
						break;
					default:
						throw new Exception("Undefined error: " + repeatInfo.getType());
					}



					/**
					 * 繰り返し予定の除外設定
					 */
					Iterator<Span> e = repeatInfo.getExclusiveDateTimes().iterator();
					ArrayList<String> excludeList = new ArrayList<String>();
					while (e.hasNext()) {
						excludeList.add(googleCalendar.getExcludeDate(new Date(e.next().getStart().getTime())));
					}
					if (excludeList.size() > 0) {
						recurrenceList.add("EXDATE;TZID=UTC;VALUE=DATE-TIME:" + StringUtils.join(excludeList, ","));
					}
				}

				if (garoonSchedule.isStartOnly()) {
					/** 開始時間のみのスケジュールの場合は終了時間に開始時間をセット **/
					scheduleEnd = scheduleStart;
				}



				/** 施設の取得 **/
				ScheduleGetFacilitiesById scheduleGetFacilitiesById = garoonSchedular.getFacilitiesId(garoonSchedule.getMembers());
				if (scheduleGetFacilitiesById.size() > 0) {
					OMElement facilitiesElement = cbClient.sendReceive(scheduleGetFacilitiesById);
					scheduleLocation = garoonSchedular.getFacilitiesInfo(facilitiesElement);
				}

				/** 参加者の取得 **/
				List<Member> members = garoonSchedule.getMembers();
				if ((members.size() > 0) && (garoonMemberLimit > 0)) {
					scheduleMembers += garoonSchedular.getMembersInfo(garoonUid, garoonMemberLimit, members);		
				}
				
				/** コメントの取得 **/
				List<Follow> follows = garoonSchedule.getFollows();
				if (follows.size() > 0) {
					scheduleComments += garoonSchedular.getFollowsInfo(follows);
				}
				
				scheduleDescription = scheduleMembers + scheduleMemo + CRLF + CRLF + scheduleComments;



				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("[ADD] GaroonId:{} Title:{}", garoonScheduleId, scheduleTitle);
				} else if (LOGGER.isInfoEnabled()) {
					LOGGER.info("[ADD] Title:{}", scheduleTitle);
				}

				//LOGGER.info(garoonScheduleId);
				//LOGGER.info(scheduleTitle);
				//LOGGER.info(scheduleStart);
				//LOGGER.info(scheduleEnd);
				//LOGGER.info(recurrenceList);
				//System.exit(0);

				try {
					/** グーグルカレンダーに登録 **/
					googleScheduleId = googleCalendar.addSchedule(scheduleStart, scheduleEnd, 
							scheduleTitle, scheduleDescription, scheduleLocation, scheduleColor, recurrenceList, scheduleTimezone);

					/** グーグルカレンダーへの登録情報をSQLiteに登録 **/
					ggsyncDb.addScheduleInfo(garoonScheduleId, garoonScheduleVersion, googleScheduleId, scheduleAllEnd);
				} catch (GoogleJsonResponseException e) {
					LOGGER.error("[FAILED] registration google calendar. GaroonId:{}, Message:{}", garoonScheduleId, e.getMessage());
					System.exit(1);
				} catch (SQLException e) {
					LOGGER.error("[FAILED] registration local db. GaroonId:{}, Message:{}", garoonScheduleId, e.getMessage());
					System.exit(1);
				}
			}



			/** ガルーンから削除されたスケジュールをグーグルカレンダーからも削除 **/
			ArrayList<String> needDeleteGoogleScheduleIdList = ggsyncDb.getNeedDeleteGoogleScheduleIdList(garoonScheduleIdList);
			Iterator<String> i = needDeleteGoogleScheduleIdList.iterator();
			while (i.hasNext()) {
				String tid = i.next();

				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("[DEL for deleted garoon schedule] GoogleId:{}", tid);
				} else if (LOGGER.isInfoEnabled()) {
					LOGGER.info("[DEL for deleted garoon schedule] GoogleId:{}", tid);
				}

				try {
					googleCalendar.delSchedule(tid);
				} catch (GoogleJsonResponseException e) {
					LOGGER.info("[FAILED] google schedule is already deleted. GoogleId:{}, Message:{}", tid, e.getMessage());
				}
				ggsyncDb.delScheduleInfoByGoogleId(tid);
			}



			/** SYNC開始日より30日以前の古いデータをSQLiteから削除 **/
			ggsyncDb.delOldScheduleInfo(ggsyncProperties.getSyncStartDate().getTime() - (long)30 * 24 * 60 * 60 * 1000);

			LOGGER.info("The sync completed.");
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return;
	}

}
