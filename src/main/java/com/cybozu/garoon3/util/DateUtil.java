package com.cybozu.garoon3.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * APIで日時を扱うユーティリティです。
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class DateUtil {
    private static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * DateインスタンスをAPIリクエスト/レスポンスで使用する形式で文字列に変換します。
     * @param date 日時
     * @return 日時の文字列
     */
    public static String dateToString(Date date){
        return FORMATTER.format(date);
    }

    /**
     * APIで使用する日時を表す文字列をDate型に変換します。
     * @param str 日時を表す文字列("yyyy-MM-dd'T'HH:mm:ss'Z'")
     * @return Dateインスタンス
     * @throws ParseException
     */
    public static Date stringToDate(String str) throws ParseException{
        return FORMATTER.parse(str);
    }
}
