package com.cybozu.garoon3.util;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMElement;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * XML メッセージを出力するクラス
 * 
 * @author $api_author Garoon Team@Cybozu$
 * @version $api_version ver 1.0.0$
 */
public class XMLDisplayUtility {
    private static int DEFAULT_INDENT_NUM = 2;

    /**
     * SOAP の XML データをコンソールに出力します。
     * 
     * @param elem SOAP の XML データ
     */
    public static void printSmartXML(OMElement elem) {
        try {
            // OMElement to Document
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(
                            new InputSource(new StringReader(elem.toString())));

            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();

            // Indent Setting
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(
                    OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, ""
                            + DEFAULT_INDENT_NUM);

            // Output
            Writer writer = new PrintWriter(System.out);
            transformer.transform(new DOMSource(document), new StreamResult(writer));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * インデートのスペース数を設定します。
     * @param num スペースの数
     */
    public static void setDefaultIndentNumber(int num) {
        DEFAULT_INDENT_NUM = num;
    }
}
