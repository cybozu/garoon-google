package com.cybozu;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.cybozu.garoon3.schedule.Facility;
import com.cybozu.garoon3.schedule.Follow;
import com.cybozu.garoon3.schedule.Member;
import com.cybozu.garoon3.schedule.MemberType;
import com.cybozu.garoon3.schedule.ScheduleGetFacilitiesById;
import com.cybozu.garoon3.schedule.ScheduleUtil;

public class GaroonSchedular {
	private static final String CRLF = System.getProperty("line.separator");

	public ScheduleGetFacilitiesById getFacilitiesId(List<Member> members) {
		ScheduleGetFacilitiesById scheduleGetFacilitiesById = new ScheduleGetFacilitiesById();
		Iterator<Member> m = members.iterator();
		while (m.hasNext()) {
			Member member = m.next();
			if (member.getType() == MemberType.FACILITY) {
				scheduleGetFacilitiesById.addID(member.getID());
			}
		}

		return scheduleGetFacilitiesById;
	}

	public String getFacilitiesInfo(OMElement facilitiesElement) {
		String facilityInfo;

		List<Facility> facilities = ScheduleUtil.getFacilityList(facilitiesElement);
		ArrayList<String> facilitiesName = new ArrayList<String>();
		Iterator<Facility> f = facilities.iterator();
		while (f.hasNext()) {
			Facility facility = f.next();
			facilitiesName.add(facility.getName());
		}				  
		facilityInfo = StringUtils.join(facilitiesName, ",");

		return facilityInfo;
	}
	
	public String getFollowsInfo(List<Follow> follows) {
		String followsInfo = "";
		
		Iterator<Follow> f = follows.iterator();
		
		if (follows.size() > 0) {
			followsInfo += "▽ コメント ▽";
		}

		while (f.hasNext()) {
			Follow follow = f.next();
			followsInfo += CRLF;
			followsInfo += "▼" + follow.getCreatorName() + " (" + follow.getDatetime().toString() + ")" + CRLF;
			followsInfo += follow.getText() + CRLF;
		}
		
		return followsInfo;
	}
	
	public String getMembersInfo(int loginId, int maxCount, List<Member> members) {
		String membersInfo = "";
		
		Iterator<Member> m = members.iterator();
		ArrayList<String> memberNames = new ArrayList<String>();

		if (members.size() > 0) {
			while (m.hasNext()) {
				Member member = m.next();
				if ((member.getType() == MemberType.USER) && (member.getID() != loginId)) {
					memberNames.add(member.getName());
				}
			}
		}

		if (memberNames.size() > 0) {
			membersInfo += "▽ 自分以外の参加者 ▽" + CRLF;
			Integer max;
			if (memberNames.size() > maxCount) {
				max = maxCount;
			} else {
				max = memberNames.size();
			}
			membersInfo += String.join(", ",  memberNames.subList(0, max)) + CRLF + CRLF;			
		}
		
		return membersInfo;
	}

	/**
	 * BaseGetUsersByLOGGERinNameのレスポンスからUID（ログインアカウントとは異なる）を取得する
	 * @param node
	 * @return
	 */
	public int getUid (OMElement node) throws Exception {
		Integer uid;
		Document document = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(new InputSource(new StringReader(node.toString())));


		DTMNodeList nodeList = null;
		String expression = "BaseGetUsersByLoginNameResponse/returns/user";

		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression xPathExpression = xPath.compile(expression);
		nodeList = (DTMNodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);

		Node data = nodeList.item(0);
		uid = Integer.parseInt(data.getAttributes().getNamedItem("key").getNodeValue());

		return uid;
	}

}