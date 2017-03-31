package com.cybozu.garoon3.schedule;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * スケジュールAPI関連で利用するユーティリティクラスです。<br />
 * <br />
 * このクラスは二つのメソッドを提供します。<br />
 * ひとつはAPIから取得したレスポンスを com.cybozu.garoon3.schedule.Event にパースする getEventList メソッドです。<br />
 * もうひとつは、作成した Event インスタンスをリクエストで利用する形に変換する toParameter メソッドです。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class ScheduleUtil {

	/**
	 * OMElement を Event インスタンスのリストに変換します。<br />
	 * input が想定した形式でない場合、空のリストを返します。
	 *
	 * @param input
	 * @return 予定一覧
	 */
	public static List<Event> getEventList( OMElement input, Integer apiVersion )
	{
		List<Event> events = new ArrayList<Event>();

		input = (OMElement) input.getChildElements().next();
		Document document = getDocument(input);
		String inputString = input.toString();

		DTMNodeList eventNodes = null;
		try{
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression xPathExpression = xPath.compile( "returns/schedule_event" );
			eventNodes = (DTMNodeList)xPathExpression.evaluate(document, XPathConstants.NODESET);
		} catch(XPathExpressionException e) {
			return Collections.emptyList();
		}

		for (int i = 0; i < eventNodes.getLength(); i++){
			TimeZone timezone = null;
			
			Event event = new Event();
			Node eventNode = eventNodes.item(i);
			NamedNodeMap attrs = eventNode.getAttributes();

			//ID
			Node attr = attrs.getNamedItem("id");
			event.setId( Integer.valueOf(attr.getNodeValue()) );

			//Version
			attr = attrs.getNamedItem("version");
			event.setVersion( Long.valueOf(attr.getNodeValue()) );

			//Event Type
			attr = attrs.getNamedItem("event_type");
			if( attr != null )
				event.setEventType( EventType.valueOf(attr.getNodeValue().toUpperCase()) );

			//Public Type
			attr = attrs.getNamedItem("public_type");
			if( attr != null )
				event.setPublicType( PublicType.valueOf(attr.getNodeValue().toUpperCase()) );

			//Plan
			attr = attrs.getNamedItem("plan");
			if( attr != null )
				event.setPlan( attr.getNodeValue() );

			//Detail
			attr = attrs.getNamedItem("detail");
			if( attr != null )
				event.setDetail( attr.getNodeValue() );

			//Description
			attr = attrs.getNamedItem("description");
			if( attr != null )
				event.setDescription( attr.getNodeValue() );

			//Description(改行対応）
			String regex = "id=\"" + event.getId() + "\" [^<>]*description=\"([^\"]*)\"";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(inputString);
			if( m.find() ) {
				event.setDescription( m.group(1) );
			}

			//TimeZone
			attr = attrs.getNamedItem("timezone");
			if( attr != null ){
				timezone = TimeZone.getTimeZone(attr.getNodeValue());
				event.setTimezone( timezone );
			}

			//StartOnly
			attr = attrs.getNamedItem("start_only");
			if (attr != null) {
				event.setStartOnly(Boolean.parseBoolean(attr.getNodeValue()));
			}

			//AllOnly
			attr = attrs.getNamedItem("allday");
			if (attr != null) {
				event.setAllDay(Boolean.parseBoolean(attr.getNodeValue()));
			}

			event.setRepeatInfo( getRepeatInfo(eventNode, timezone) );
			event.setMembers( getMemberList(eventNode) );
			event.setSpans( getSpanList(eventNode, timezone) );
			event.setFollows( getFollowList(eventNode, inputString, timezone) );
			event.setCustomer( getCustomer(eventNode) );
			event.setObservers( getObserverList(eventNode, apiVersion) );
			events.add( event );
			//System.out.println(event);
		}
		return events;
	}

	private static RepeatInfo getRepeatInfo( Node eventNode, TimeZone timezone )
	{
		NodeList children = eventNode.getChildNodes();

		Node repeatInfoNode = null;
		for( int i=0; i<children.getLength(); i++)
			if( children.item(i).getNodeName().equals("repeat_info") )
				repeatInfoNode = children.item(i);

		if( repeatInfoNode == null )
			return null;

		RepeatInfo info = new RepeatInfo();

		//get condition node
		Node conditionNode = repeatInfoNode.getChildNodes().item(1);

		String repeatEventType = getAttribute(conditionNode, "type").toUpperCase();
		if (repeatEventType.equals("1STWEEK")) {
			info.setType(RepeatEventType.WEEK_1ST);
		}
		else if (repeatEventType.equals("2NDWEEK")) {
			info.setType(RepeatEventType.WEEK_2ND);
		}
		else if (repeatEventType.equals("3RDWEEK")) {
			info.setType(RepeatEventType.WEEK_3RD);
		}
		else if (repeatEventType.equals("4THWEEK")) {
			info.setType(RepeatEventType.WEEK_4TH);
		}
		else if (repeatEventType.equals("LASTWEEK")) {
			info.setType(RepeatEventType.WEEK_LAST);
		}
		else {
			info.setType( RepeatEventType.valueOf(getAttribute(conditionNode, "type").toUpperCase()) );
		}
		info.setDay( Integer.valueOf(getAttribute(conditionNode, "day")));

		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try{
			//info.setStartDate( formatter.parse(getAttribute(conditionNode,"start_date")) );
			info.setStartDate( parseDate(getAttribute(conditionNode,"start_date"), timezone) );
		}catch(ParseException e)
		{}

		try{
			//info.setEndDate( formatter.parse(getAttribute(conditionNode,"end_date")) );
			info.setEndDate( parseDate(getAttribute(conditionNode,"end_date"), timezone) );
		}catch(ParseException e)
		{}

		info.setStartTime( getAttribute(conditionNode, "start_time") );
		info.setEndTime( getAttribute(conditionNode, "end_time") );
		info.setWeek( Integer.valueOf(getAttribute(conditionNode,"week")));

		// get exclusive datetimes
		Node exDateTimesNode = repeatInfoNode.getChildNodes().item(3);
		NodeList exDateTimeList = exDateTimesNode.getChildNodes();
		List<Span> exclusiveDateTimes = new ArrayList<Span>();
		for( int i=0; i<exDateTimeList.getLength(); i++ )
		{
			Node item = exDateTimeList.item(i);

			if( !"exclusive_datetime".equals(item.getNodeName()))
				continue;


			Span span = new Span();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			Date start, end;
			
			
			//Date start = parseISO8601( getAttribute(item, "start") );
			Date start_date_only = null, end_date_only = null;
			try {
				//start_date_only = parseISO8601( getAttribute(item, "start") );
				start_date_only = parseWhenDatetime( getAttribute(item, "start"), timezone );
			} catch (ParseException e) {
				try {
					//start_date_only = parseISO8601_3( getAttribute(item, "start") );
					start_date_only = parseExclusiveDatetime( getAttribute(item, "start"), timezone );
				} catch (ParseException e1) {

				}
			}			
			try {
				Date time_only = sdf.parse(getAttribute(conditionNode, "start_time"));
				//start = new Date(start_date_only.getTime() + time_only.getTime());
				start = new Date(start_date_only.getTime() + time_only.getTime() + TimeZone.getDefault().getRawOffset());
			} catch(Exception e) {
				start = new Date(start_date_only.getTime());
			}
			span.setStart(start);


			//Date end = parseISO8601( getAttribute(item, "end") );
			try {
				//end_date_only = parseISO8601( getAttribute(item, "end") );
				end_date_only = parseWhenDatetime( getAttribute(item, "end"), timezone );
			} catch (ParseException e) {
				try {
					//end_date_only = parseISO8601_3( getAttribute(item, "end") );
					end_date_only = parseExclusiveDatetime( getAttribute(item, "end"), timezone );
				} catch (ParseException e1) {
				}
			}
			try {
				Date time_only = sdf.parse(getAttribute(conditionNode, "end_time"));
				//end = new Date(end_date_only.getTime() + time_only.getTime());
				end = new Date(end_date_only.getTime() + time_only.getTime() + TimeZone.getDefault().getRawOffset());
			} catch(Exception e) {
				end = new Date(end_date_only.getTime());
			}
			span.setEnd(end);


			exclusiveDateTimes.add( span );
		}

		info.setExclusiveDateTimes(exclusiveDateTimes);
		return info;
	}

	private static List<Follow> getFollowList( Node eventNode, String inputString, TimeZone timezone )
	{
		NodeList children = eventNode.getChildNodes();

		Node followsNode = null;
		for( int i=0; i<children.getLength(); i++)
			if( children.item(i).getNodeName().equals("follows") )
				followsNode = children.item(i);

		if( followsNode == null )
			return Collections.emptyList();

		children = followsNode.getChildNodes();
		List<Follow> follows = new ArrayList<Follow>();
		for( int i=0; i<children.getLength(); i++)
		{
			if( !children.item(i).getNodeName().equals("follow") )
				continue;

			Node followNode = children.item(i);

			int id = Integer.valueOf( getAttribute(followNode, "id") );
			long version = Long.valueOf( getAttribute(followNode, "version") );
			String text = getAttribute(followNode, "text");

			//follow(改行対応）
			String regex = "id=\"" + id + "\" [^<>]*text=\"([^\"]*)\"";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(inputString);
			if( m.find() ) {
				text = m.group(1);
			}

			Node c = followNode.getChildNodes().item(1);
			Date date = null;
			try {
				//date = parseISO8601( getAttribute(c, "date") );
				date = parseWhenDatetime( getAttribute(c, "date"), timezone );
			} catch (ParseException e) {
			}

			// 削除されたユーザーの場合、user_idが取得できないので0をセットする
			int userId = 0;
			try {
				userId = Integer.valueOf( getAttribute(c, "user_id") );
			} catch (NumberFormatException e) {
			}

			String name = getAttribute(c, "name");

			Follow follow = new Follow();
			follow.setId(id);
			follow.setVersion(version);
			follow.setText(text);
			follow.setCreatorId(userId);
			follow.setCreatorName(name);
			follow.setDatetime(date);
			follows.add( follow );
		}
		return follows;
	}

	private static Customer getCustomer( Node eventNode )
	{
		NodeList children = eventNode.getChildNodes();

		Node customerNode = null;
		for( int i=0; i<children.getLength(); i++)
			if( children.item(i).getNodeName().equals("customer") )
				customerNode = children.item(i);

		if( customerNode == null )
			return new Customer();

		String name 		= getAttribute(customerNode, "name");
		String address 		= getAttribute(customerNode, "address");
		String phone 		= getAttribute(customerNode, "phone");
		String map	 		= getAttribute(customerNode, "map");
		String route 		= getAttribute(customerNode, "route");
		String routeFare	= getAttribute(customerNode, "route_fare");
		String routeTime	= getAttribute(customerNode, "route_time");
		String zipCode		= getAttribute(customerNode, "zipcode");

		Customer customer = new Customer();
		customer.setName(name);
		customer.setAddress( address );
		customer.setPhone(phone);
		customer.setRoute(route);
		customer.setMap(map);
		customer.setRouteFare(routeFare);
		customer.setRouteTime(routeTime);
		customer.setZipCode(zipCode);
		return customer;
	}

	private static List<Observer> getObserverList(Node eventNode, Integer apiVersion) {
		NodeList children = eventNode.getChildNodes();

		Node observersNode = null;
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("observers")) {
				observersNode = children.item(i);
			}
		}

		if (observersNode == null) {
			return Collections.emptyList();
		}

		children = observersNode.getChildNodes();
		List<Observer> observers = new ArrayList<Observer>();
		for (int i = 0; i < children.getLength(); i++) {
			if (!children.item(i).getNodeName().equals("observer")) {
				continue;
			}

			Node observerNode = children.item(i);

			Integer observerId, observerOrder;
			// APIバージョン180から構成が変わったため
			if (apiVersion >= 180) {
				Node c = observerNode.getChildNodes().item(1);
				observerId = Integer.valueOf(getAttribute(c, "id" ));
				observerOrder = Integer.valueOf(getAttribute(c, "order"));
			} else {
				observerId = Integer.valueOf(getAttribute(observerNode, "id" ));
				observerOrder = Integer.valueOf(getAttribute(observerNode, "order"));
			}

			Observer observer = new Observer();
			observer.setID(observerId);
			observer.setOrder(observerOrder);

			observers.add(observer);
		}

		return observers;
	}

	private static List<Span> getSpanList( Node eventNode, TimeZone timezone )
	{
		NodeList children = eventNode.getChildNodes();

		Node whenNode = null;
		for( int i=0; i<children.getLength(); i++)
			if( children.item(i).getNodeName().equals("when") )
				whenNode = children.item(i);

		if( whenNode == null )
			return null;

		children = whenNode.getChildNodes();
		List<Span> spans = new ArrayList<Span>();
		for( int i=0; i<whenNode.getChildNodes().getLength(); i++)
		{
			if( !children.item(i).getNodeName().equals("datetime") && !children.item(i).getNodeName().equals("date") )
				continue;

			Node datetimeNode = children.item(i);

			String start = getAttribute(datetimeNode, "start");
			String end   = getAttribute(datetimeNode, "end");

			Span span = new Span();

			try {
				if( start != null){
					//span.setStart( parseISO8601(start) );
					span.setStart( parseWhenDatetime(start, timezone) );
				}
			} catch (ParseException e) {
				try {
					//span.setStart( parseISO8601_2(start) );
					span.setStart( parseDate(start, timezone) );
				} catch (ParseException e1) {
				}
			}
			try {
				if( end != null ){
					//span.setEnd( parseISO8601(end) );
					span.setEnd( parseWhenDatetime(end, timezone) );
				}
			} catch (ParseException e) {
				try {
					//span.setEnd( parseISO8601_2(end) );
					span.setEnd( parseDate(end, timezone) );
				} catch (ParseException e1) {
				}
			}
			spans.add(span);
		}
		return spans;
	}

	private static List<Member> getMemberList( Node eventNode )
	{
		NodeList children = eventNode.getChildNodes();

		Node membersNode = null;
		for( int i=0; i<children.getLength(); i++)
			if( children.item(i).getNodeName().equals("members") )
				membersNode = children.item(i);

		if( membersNode == null )
			return Collections.emptyList();

		children = membersNode.getChildNodes();
		List<Member> members = new ArrayList<Member>();
		for( int i=0; i<children.getLength(); i++)
		{
			if( children.item(i).getNodeType() == Node.TEXT_NODE)
				continue;

			Node memberNode = children.item(i);
			Node c = memberNode.getChildNodes().item(1);

			MemberType type = MemberType.valueOf( c.getNodeName().toUpperCase() );
			int id 	  = Integer.valueOf( getAttribute(c, "id") );
			int order = Integer.valueOf( getAttribute(c, "order" ) );
			String name = String.valueOf( getAttribute(c, "name" ) );

			Member member = new Member( type, id, order, name );
			members.add( member );
		}
		return members;
	}

	private static Document getDocument( OMElement input )
	{
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(input.toString())));
		} catch(IOException e) {
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		}

		return document;
	}

	/*
	private static Date parseISO8601(String source) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'", Locale.JAPAN);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.parse(source);
	}

	private static Date parseISO8601_2(String source) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd", Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.parse(source);
	}

	private static Date parseISO8601_3(String source) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss", Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.parse(source.replaceAll(":([0-9]{2})$", "$1"));
	}
	*/
	
	private static Date parseWhenDatetime(String source, TimeZone timezone) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.parse(source);
	}
	
	private static Date parseDate(String source, TimeZone timezone) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd");
		formatter.setTimeZone(timezone);
		return formatter.parse(source);
	}
	
	private static Date parseExclusiveDatetime(String source, TimeZone timezone) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ssZ");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.parse(source.replaceAll(":([0-9]{2})$", "$1"));
	}

	private static String getAttribute( Node node, String name)
	{
		try{
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}catch( Exception e ) {
			return "";
		}
	}

	/**
	 * 予定一覧を API のパラメーターとして利用可能な形式に変換します。
	 *
	 * @param events 予定一覧
	 * @return パラメーター
	 */
	public static OMElement toParameter( List<Event> events )
	{
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement parameters = omFactory.createOMElement("parameters", null);
		for( Event event : events )
		{
			OMElement eventNode = omFactory.createOMElement("schedule_event", null);
			eventNode.addAttribute("id", String.valueOf(event.getId()), null);
			eventNode.addAttribute("version", String.valueOf(event.getVersion()), null);
			eventNode.addAttribute("event_type", event.getEventType().toString().toLowerCase(), null);
			eventNode.addAttribute("public_type", event.getPublicType().toString().toLowerCase(), null);
			eventNode.addAttribute("plan", event.getPlan(), null);
			eventNode.addAttribute("detail", event.getDetail(), null);
			eventNode.addAttribute("description", event.getDescription(), null);
			eventNode.addAttribute("timezone", event.getTimezone().getID(), null);
			eventNode.addAttribute("allday", String.valueOf(event.isAllDay()), null);
			eventNode.addAttribute("start_only", String.valueOf(event.isStartOnly()), null);

			//Members node
			OMElement membersNode = omFactory.createOMElement("members", null);
			eventNode.addChild( membersNode );
			for( Member member : event.getMembers() )
			{
				OMElement memberNode = omFactory.createOMElement("member", null);
				OMElement userNode = omFactory.createOMElement(member.getType().toString().toLowerCase(), null);
				userNode.addAttribute("id", String.valueOf(member.getID()), null);
				userNode.addAttribute("order", String.valueOf(member.getOrder()), null);
				memberNode.addChild( userNode );
				membersNode.addChild( memberNode );
			}

			//When node
			if( event.getSpans().get(0) != null )
			{
				OMElement whenNode = omFactory.createOMElement("when", null);

				OMElement datetimeNode = omFactory.createOMElement("datetime", null);
				String start = DateFormatUtils.ISO_DATETIME_FORMAT.format( event.getSpans().get(0).getStart() );
				String end   = DateFormatUtils.ISO_DATETIME_FORMAT.format( event.getSpans().get(0).getEnd() );
				datetimeNode.addAttribute("start", start, null);
				datetimeNode.addAttribute("end", end, null);

				whenNode.addChild( datetimeNode );
				eventNode.addChild( whenNode );
			}

			//Observers Node
			if( event.getObservers() != null )
			{
				OMElement observersNode = omFactory.createOMElement("observers", null);
				for( Observer observer : event.getObservers() )
				{
					OMElement observerNode = omFactory.createOMElement("observer", null);
					observerNode.addAttribute("id", String.valueOf(observer.getID()), null);
					observerNode.addAttribute("order", String.valueOf(observer.getOrder()), null);

					observersNode.addChild(observerNode);
				}
				eventNode.addChild(observersNode);
			}

			//Repeat info
			if( event.getRepeatInfo() != null )
			{
				RepeatInfo info = event.getRepeatInfo();

				OMElement repeatInfoNode = omFactory.createOMElement("repeat_info", null);

				//Condition node
				OMElement conditionNode = omFactory.createOMElement("condition", null);
				conditionNode.addAttribute("day", String.valueOf(info.getDay()), null);
				conditionNode.addAttribute("week", String.valueOf(info.getWeek()), null);
				conditionNode.addAttribute("type", info.getType().toString().toLowerCase(), null);

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				conditionNode.addAttribute("start_date", formatter.format(info.getStartDate()), null);
				conditionNode.addAttribute("start_time", info.getStartTime(), null);

				conditionNode.addAttribute("end_date", formatter.format(info.getEndDate()), null);
				conditionNode.addAttribute("end_time", info.getEndTime(), null);

				repeatInfoNode.addChild(conditionNode);

				//Exclusive datetime node
				if( info.getExclusiveDateTimes() != null )
				{
					OMElement exclusiveDatetimesNode = omFactory.createOMElement("exclusive_datetimes", null);
					eventNode.addChild(exclusiveDatetimesNode);

					for( Span span : info.getExclusiveDateTimes())
					{
						OMElement exDateNode = omFactory.createOMElement("exclusive_datetime", null);
						String start = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(span.getStart());
						String end   = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(span.getEnd());

						exDateNode.addAttribute("start", start, null);
						exDateNode.addAttribute("end", end, null);

						exclusiveDatetimesNode.addChild(exDateNode);
					}
				}

				eventNode.addChild(repeatInfoNode);
			}

			parameters.addChild( eventNode );
		}
		return parameters;
	}

	public static List<Facility> getFacilityList( OMElement input )
	{
		List<Facility> facilities = new ArrayList<Facility>();

		input = (OMElement) input.getChildElements().next();
		Document document = getDocument(input);

		DTMNodeList facilityNodes = null;
		try{
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression xPathExpression = xPath.compile( "returns/facility" );
			facilityNodes = (DTMNodeList)xPathExpression.evaluate(document, XPathConstants.NODESET);
		} catch(XPathExpressionException e) {
			return Collections.emptyList();
		}

		for (int i = 0; i < facilityNodes.getLength(); i++){
			Facility facility = new Facility();
			Node failityNode = facilityNodes.item(i);
			NamedNodeMap attrs = failityNode.getAttributes();

			//ID
			Node attr = attrs.getNamedItem("key");
			facility.setKey(Integer.valueOf(attr.getNodeValue()));

			//name
			attr = attrs.getNamedItem("name");
			facility.setName(attr.getNodeValue());

			//code
			attr = attrs.getNamedItem("facility_code");
			facility.setCode(attr.getNodeValue());

			//version
			attr = attrs.getNamedItem("version");
			facility.setVersion(Long.parseLong(attr.getNodeValue()));

			//order
			attr = attrs.getNamedItem("order");
			facility.setOrder(Long.parseLong(attr.getNodeValue()));

			//desciption
			attr = attrs.getNamedItem("description");
			if (attr != null) {
				facility.setDescription(attr.getNodeValue());
			}
			else {
				facility.setDescription("");
			}

			//ID
			attr = attrs.getNamedItem("belong_facility_group");
			if (attr != null) {
				facility.setBelongFacilityGroup(Integer.valueOf(attr.getNodeValue()));
			}

			facilities.add(facility);
		}

		return facilities;
	}
}
