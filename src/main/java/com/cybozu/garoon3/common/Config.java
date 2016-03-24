package com.cybozu.garoon3.common;

import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * アカウントファイルを読み込むクラスです。
 * このクラスは java.util.Properties クラスのラッパークラスであり、
 * ファイルの読み込み、解析は java.util.Properties クラスが行っています。<br />
 * 従ってアカウントファイルは java.util.Properties クラスが読み込み可能な形式で記述しなければなりません。<br />
 * <br />
 * このクラスはアカウントファイルに対して行う操作は読み込みのみです。
 * 書き込みを行うことはありません。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 * @see java.util.Properties
 */
public class Config {
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private Properties properties = null;

    /**
     * 指定されたアカウントファイルを利用する新しいインスタンスを生成します。
     *
     * @param filepath アカウントファイル名
     * @throws IOException 入出力エラーが発生した場合
     * @throws FileNotFoundException 指定されたファイルが見つからない場合
     */
    public Config(String filepath) throws FileNotFoundException,IOException {
        this.properties = new Properties();

        FileReader reader = new FileReader(filepath);
        this.properties.load(reader);
        reader.close();
    }

    /**
     * アカウントファイルの username キーで指定された値を返します。<br />
     * キーが存在しない場合は null.
     * @return java.lang.String ユーザー名
     */
    public String getUsername() {
        return getValue( ConfigKeys.USERNAME );
    }

    /**
     * アカウントファイルの password キーで指定された値を返します。<br />
     * キーが存在しない場合は null.
     * @return java.lang.String パスワード
     */
    public String getPassword() {
        return getValue( ConfigKeys.PASSWORD );
    }

    /**
     * アカウントファイルの garoonURL キーで指定された値を返します。<br />
     * キーが存在しない場合、またはURLの形式が不正な場合は null を返します。
     * @return java.net.URI URI
     */
    public URI getGaroonURL() {
		try {
			return new URI( getValue(ConfigKeys.GRNURL) );
		} catch (Exception e) {
			return null;
		}
    }

    /**
     * アカウントファイルの schema キーで指定された値を返します。<br />
     * キーが存在しない場合は null.
     * @return java.lang.String スキーマ
     */
    public String getScheme() {
        return getValue( ConfigKeys.SCHEME );
    }

    /**
     * アカウントファイルの created キーで指定された値を返します。<br />
     * キーが存在しない場合、または日付の形式が不正な場合は null を返します。
     * @return java.util.Date 作成日
     */
    public Date getCreatedTime() {
        try {
            return FORMATTER.parse( getValue(ConfigKeys.CREATEDTIME) );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * アカウントファイルの expired キーで指定された値を返します。<br />
     * キーが存在しない場合、または日付の形式が不正な場合は null を返します。
     * @return java.util.Date 期限日
     */
    public Date getExpiredTime() {
        try {
            return FORMATTER.parse( getValue(ConfigKeys.EXPIREDTIME) );
        } catch (Exception e) {
            return null;
        }
    }

    private String getValue(ConfigKeys key)
    {
    	return this.properties.getProperty( key.getKey() );
    }

    private enum ConfigKeys {
        USERNAME("username"),
        PASSWORD("password"),
        GRNURL("garoonURL"),
        SCHEME("scheme"),
        CREATEDTIME("created"),
        EXPIREDTIME("expires");

        private final String key;

        private ConfigKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}
