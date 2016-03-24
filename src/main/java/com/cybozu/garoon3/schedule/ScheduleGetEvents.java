package com.cybozu.garoon3.schedule;

import java.util.Date;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.cybozu.garoon3.common.APIType;
import com.cybozu.garoon3.common.Action;
import com.cybozu.garoon3.util.DateUtil;
/**
 * 期間を指定して予定を取得します。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class ScheduleGetEvents implements Action {

    private Date start;
    private Date end;
    private Date startForDaily;
    private Date endForDaily;

    public ScheduleGetEvents( Date start, Date end ) {
        this.start = start;
        this.end = end;
    }

    /**
     * 検索期間の開始日時を取得します。
     * @return 開始日時
     */
    public Date getStart() {
        return this.start;
    }

    /**
     * 検索期間の開始日時を設定します。
     * @param start 開始日時
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * 検索期間の終了日時を取得します。
     * @return 終了日時
     */
    public Date getEnd() {
        return this.end;
    }

    /**
     * 検索期間の終了日時を設定します。
     * @param end 終了日時
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * 時刻を持たない検索期間の開始日を取得します。
     * @return 開始日
     */
    public Date getStartForDaily() {
        return this.startForDaily;
    }

    /**
     * 時刻を持たない検索期間の開始日を設定します。
     * @param startForDaily 開始日
     */
    public void setStartForDaily(Date startForDaily) {
        this.startForDaily = startForDaily;
    }

    /**
     * 時刻を持たない検索期間の終了日を取得します。
     * @return 終了日
     */
    public Date getEndForDaily() {
        return this.endForDaily;
    }

    /**
     * 時刻を持たない検索期間の終了日を設定します。
     * @param endForDaily 終了日
     */
    public void setEndForDaily(Date endForDaily) {
        this.endForDaily = endForDaily;
    }

    @Override
    public APIType getAPIType() {
        return APIType.SCHEDULE;
    }

    @Override
    public String getActionName() {
        return "ScheduleGetEvents";
    }

    @Override
    public OMElement getParameters() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();

        OMElement parameters = omFactory.createOMElement("parameters", null);
        parameters.addAttribute("start", DateUtil.dateToString(this.start), null);
        parameters.addAttribute("end", DateUtil.dateToString(this.end), null);

        if( this.startForDaily != null )
        {
            String startForDaily = DateFormatUtils.ISO_DATE_FORMAT.format(this.startForDaily);
            parameters.addAttribute("start_for_daily", startForDaily, null);
        }

        if( this.endForDaily != null )
        {
            String endForDaily = DateFormatUtils.ISO_DATE_FORMAT.format(this.endForDaily);
            parameters.addAttribute("end_for_daily", endForDaily, null);
        }

        return parameters;
    }
}
