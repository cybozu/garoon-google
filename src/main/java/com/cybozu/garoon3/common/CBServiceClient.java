package com.cybozu.garoon3.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.httpclient.cookie.CookiePolicy;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



/**
 * API とのデータの送受信を行うクラスです。
 *
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class CBServiceClient {
	/** メッセージ有効期限の初期値 **/
	private static final Date DEFAULT_EXPIRED_TIME = new GregorianCalendar(2037, 11, 31).getTime();

	/** ユーザー名 **/
	private String username = "";

	/** パスワード **/
	private String password = "";

	/** ガルーンのURL **/
	private URI garoonURI;

	/** メッセージの作成日時 **/
	private Date createdTime = new GregorianCalendar().getTime();

	/** メッセージの有効期限 **/
	private Date expiredTime = DEFAULT_EXPIRED_TIME;

	/** スキーマ **/
	private String scheme = Constants.TRANSPORT_HTTP;

	private final ServiceClient serviceClient = new ServiceClient();
	private final OMFactory omFactory = OMAbstractFactory.getOMFactory();
	private final OMNamespace omNs = this.omFactory.createOMNamespace("http://wsdl.cybozu.co.jp/api/2008", "tns");

	/**
	 * 新しいクライアントインスタンスを生成します。
	 * @throws AxisFault サーバーと接続エラー
	 */
	public CBServiceClient() throws AxisFault {
	}

	/**
	 * ユーザーを設定します。<br />
	 * ユーザー名、パスワード共に非nullである必要があります。
	 * @param username ユーザー名
	 * @param password パスワード
	 */
	public void setUser(String username, String password) {
		if (username != null && password != null) {
			this.username = username;
			this.password = password;
		}
	}

	/**
	 * メッセージの作成日時を設定します。<br />
	 * null が渡された場合、値は無視されます。
	 * @param createdTime 作成日時
	 */
	public void setCreatedTime(Date createdTime) {
		if (createdTime != null)
			this.createdTime = createdTime;
	}

	/**
	 * メッセージの有効期限を設定します。
	 * null が渡された場合、値は無視されます。
	 * @param expiredTime 有効期限
	 */
	public void setExpiredTime(Date expiredTime) {
		if (expiredTime != null)
			this.expiredTime = expiredTime;
	}

	/**
	 * スキーマを設定します。<br />
	 * 例：http, https<br />
	 * null が渡された場合、値は無視されます。
	 * @param scheme スキーマ
	 * @see org.apache.axis2.Constants
	 */
	public void setScheme(String scheme) {
		if (scheme != null)
			this.scheme = scheme;
	}

	/**
	 * ガルーンの URL を設定します。
	 * null が渡された場合、値は無視されます。
	 *
	 * @param uri URI
	 */
	public void setGaroonURI(URI uri) {
		if (uri != null)
			this.garoonURI = uri;
	}

	/**
	 * 受け取ったアクションを送信し、結果を受け取ります。
	 *
	 * @param action SOAP アクション
	 * @return org.apache.axiom.om.OMElement レスポンス
	 * @throws AxisFault サーバーと接続エラー
	 */
	public OMElement sendReceive(Action action) throws AxisFault {
		this.serviceClient.removeHeaders();

		// Set URL
		URI uri;

		if (this.garoonURI.toString().indexOf(".cgi") > -1 || this.garoonURI.toString().indexOf(".exe") > -1) {
			try {
				if (this.garoonURI.getQuery() == null) {
					uri = new URI(this.garoonURI.toString() + action.getAPIType().getPath());
				}
				else {	// GR Liteの場合
					uri = new URI(this.garoonURI.getScheme() + "://" + this.garoonURI.getHost() + this.garoonURI.getPath() + action.getAPIType().getPath() + "?" + this.garoonURI.getQuery());
				}
			} catch (URISyntaxException e) {
				throw new AxisFault("Invalid URL:" + this.garoonURI.toString() + action.getAPIType().getPath());
			}
		}
		else {
			try {
	//			uri = new URI(this.garoonURI.toString() + action.getAPIType().getPath());
				uri = new URI(this.garoonURI.toString() + action.getAPIType().getPath() + ".csp");
			} catch (URISyntaxException e) {
	//			throw new AxisFault("Invalid URL:" + this.garoonURI.toString() + action.getAPIType().getPath());
				throw new AxisFault("Invalid URL:" + this.garoonURI.toString() + action.getAPIType().getPath() + ".csp");
			}
		}

		// Add Header
		OMElement header = HeaderFactory.create(action.getActionName(), this.username, this.password, this.createdTime,
				this.expiredTime);
		this.serviceClient.addHeader(header);

		// Set Options
		Options options = OptionsFactory.create(uri, this.scheme, action.getActionName());
		options.setProperty(HTTPConstants.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		this.serviceClient.setOptions(options);

		OMElement request = getRequest(action);
		OMElement result = this.serviceClient.sendReceive(request);

		return result;
	}

	public Integer getApiVersion() throws Exception {
		String header = this.serviceClient.getLastOperationContext()
			.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope().getHeader().toString();

		InputStream stream = new ByteArrayInputStream(header.toString().getBytes());
		Document document= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
		NodeList nodes = document.getElementsByTagName("apiversion");
		String apiVersion = nodes.item(0).getTextContent();

		// 特定バージョン以降で分岐させたいのでif文で使える形式で返す
		String apiNumber = apiVersion.replace(".", "");
		return Integer.parseInt(apiNumber);
	}

	private OMElement getRequest(Action action) {
		OMElement request = this.omFactory.createOMElement(action.getActionName(), this.omNs);
		OMElement parameters = action.getParameters();
		request.addChild(parameters);
		return request;
	}

	/**
	 * アカウントファイルを元に設定を反映させます。<br />
	 * このメソッドは次のメソッドで設定できる値を反映させます。<br />
	 * {@link #setGaroonURI(URI)},{@link #setUser(String, String)},{@link #setScheme(String)},
	 * {@link #setCreatedTime(Date)},{@link #setExpiredTime(Date)}
	 * @param config ガルーンの設定情報
	 */
	public void load(Config config) {
		setGaroonURI(config.getGaroonURL());
		setUser(config.getUsername(), config.getPassword());
		setScheme(config.getScheme());
		setCreatedTime(config.getCreatedTime());
		setExpiredTime(config.getExpiredTime());
	}

	/**
	 * サーバーとのコネクションを切断します。<br />
	 * コネクションを切断せずに、ひとつの CBServiceClient インスタンスを用いて複数回
	 * APIとのやりとりを行うと org.apache.commons.httpclient.ConnectionPoolTimeoutException がスローされます。<br />
	 *
	 */
	public void cleanupTransport()
	{
		try {
			this.serviceClient.cleanupTransport();
		} catch (AxisFault e) {
		}
	}
}
