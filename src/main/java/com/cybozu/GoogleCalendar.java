package com.cybozu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;


public class GoogleCalendar {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private GoogleCredential GOOGLE_CREDENTIAL;
	private Calendar CALENDAR;
	private String CALENDAR_NAME = "";

	private SimpleDateFormat RECURRENCE_SDF = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

	GoogleCalendar(String mail, String P12key) throws Exception {
		this.GOOGLE_CREDENTIAL = new GoogleCredential.Builder()
		.setTransport(HTTP_TRANSPORT)
		.setJsonFactory(JSON_FACTORY)
		.setServiceAccountId(mail)
		.setServiceAccountScopes(Arrays.asList(CalendarScopes.CALENDAR))
		.setServiceAccountPrivateKeyFromP12File(new File(P12key))
		.build();

		this.CALENDAR = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, GOOGLE_CREDENTIAL)
		.setApplicationName("GGsync")
		.build();
		
		this.RECURRENCE_SDF.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public void setCalendarName (String name) {
		this.CALENDAR_NAME = name;
	}

	public String getCalendarName () {
		return this.CALENDAR_NAME;
	}

	public String addSchedule(Date start, Date end, String title, String description, String location, String color, ArrayList<String> recurrence, TimeZone timezone) throws Exception {
		String id = null;

		Event googleSchedule = new Event();
		googleSchedule.setStart(new EventDateTime().setTimeZone(timezone.getID()).setDateTime(new DateTime(start)));
		googleSchedule.setEnd(new EventDateTime().setTimeZone(timezone.getID()).setDateTime(new DateTime(end)));
		googleSchedule.setRecurrence(null);
		googleSchedule.setSummary(title.trim());
		googleSchedule.setDescription(description.trim());
		googleSchedule.setLocation(location.trim());
		googleSchedule.setColorId(color);

		googleSchedule.setRecurrence(recurrence);

		Event createdEvent = this.CALENDAR.events().insert(this.CALENDAR_NAME, googleSchedule).execute();
		id = createdEvent.getId();

		return id;
	}

	public boolean delSchedule(String id) throws Exception {
		this.CALENDAR.events().delete(this.CALENDAR_NAME, id).execute();

		return true;
	}

	/**
	 * iCal形式の繰り返し情報を取得
	 * @param end
	 * @return
	 */
	public String getRecurrenceListDaily(Date end) {
		return "RRULE:FREQ=DAILY;UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	public String getRecurrenceListWeekday(Date end) {
		return "RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR;UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	public String getRecurrenceListWeekly(Date end) {
		return "RRULE:FREQ=WEEKLY;UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	public String getRecurrenceList1stweek(Date end, int num) {
		return "RRULE:FREQ=MONTHLY;BYDAY=1" + this.getWday(num) + ";UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	public String getRecurrenceList2ndweek(Date end, int num) {
		return "RRULE:FREQ=MONTHLY;BYDAY=2" + this.getWday(num) + ";UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	public String getRecurrenceList3rdweek(Date end, int num) {
		return "RRULE:FREQ=MONTHLY;BYDAY=3" + this.getWday(num) + ";UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	public String getRecurrenceList4thweek(Date end, int num) {
		return "RRULE:FREQ=MONTHLY;BYDAY=4" + this.getWday(num) + ";UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	public String getRecurrenceListLastweek(Date end, int num) {
		return "RRULE:FREQ=MONTHLY;BYDAY=-1" + this.getWday(num) + ";UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	public String getRecurrenceListMonthly(Date end) {
		return "RRULE:FREQ=MONTHLY;UNTIL=" + this.RECURRENCE_SDF.format(end);
	}

	/**
	 * iCal形式の繰り返し除外情報を取得
	 * @param date
	 * @return
	 */
	public String getExcludeDate(Date date) {
		return this.RECURRENCE_SDF.format(date);
	}
	
	/**
	 * iCalの繰り返しで表現できない候補日（TEMPORARY）情報を取得
	 * @param date
	 * @return
	 */
	public String getTemporaryDate(Date sdate, Date edate) {
		return this.RECURRENCE_SDF.format(sdate) + "/" + this.RECURRENCE_SDF.format(edate);
	}

	/**
	 * repeatInfoのweek値からiCal形式の曜日を取得
	 * @param num
	 * @return
	 */
	private String getWday(int num) {
		String wday = "SU";

		switch (num) {
		case 0:
			wday = "SU";
			break;
		case 1:
			wday = "MO";
			break;
		case 2:
			wday = "TU";
			break;
		case 3:
			wday = "WE";
			break;
		case 4:
			wday = "TH";
			break;
		case 5:
			wday = "FR";
			break;
		case 6:
			wday = "SA";
			break;
		}

		return wday;
	}

}