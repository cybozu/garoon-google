package com.cybozu.garoon3.base;

import java.util.Date;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import com.cybozu.garoon3.util.DateUtil;

public class BaseChangeLogType {
    private final String elementName;
    private final int userId;
    private final String name;
    private final Date date;

    public BaseChangeLogType(String elementName, int userId, String name, Date date){
        this.elementName = elementName;
        this.userId = userId;
        this.name = name;
        this.date = (Date) date.clone();
    }

    public BaseChangeLogType(BaseChangeLogType creator) {
        this.elementName = creator.elementName;
        this.userId = creator.userId;
        this.name = creator.name;
        this.date = (Date) creator.date.clone();
    }

    public OMElement toOMElement(){
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement element = omFactory.createOMElement(this.elementName, null);

        element.addAttribute("user_id", String.valueOf(this.userId), null);
        element.addAttribute("name", this.name, null);
        element.addAttribute("date", DateUtil.dateToString(this.date), null);

        return element;
    }
}
