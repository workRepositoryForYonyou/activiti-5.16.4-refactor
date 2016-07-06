package com.yonyou.bpm.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.camunda.spin.xml.SpinXPathQuery;
import org.camunda.spin.xml.SpinXmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Xpath解析xml内容
 * 
 * @author zhaoywa
 *
 */
public class XpathUtilBean {

	private static Logger logger = LoggerFactory.getLogger(XpathUtilBean.class);

	public SpinXmlElement element;
	public SpinXPathQuery eleXPathQuery;
	public String inputStream;
	public String expression;
	public String attribute;
	public String prefix;
	public String namespace;

	public XpathUtilBean() {

	}

	/**
	 * 
	 * @param xmlStr String/byte[]/InputStream/Reader
	 * @param expression xpath表达式
	 * @return
	 */
	public SpinXPathQuery xpath(Object xmlStr, String expression) {
		if(xmlStr  instanceof byte[]){
			byte[] bytes=(byte[])xmlStr;
			ByteArrayInputStream byteArrayInputStream=null;
			try{
				byteArrayInputStream=new ByteArrayInputStream(bytes);
				InputStreamReader inputStreamReader=new InputStreamReader(byteArrayInputStream);
				element = org.camunda.spin.Spin.XML(inputStreamReader);
			}finally{
				if(byteArrayInputStream!=null)
					try {
						byteArrayInputStream.close();
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
					}
			}
		}else if(xmlStr instanceof InputStream){
			InputStream inputStream=(InputStream)xmlStr;
			InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
			try{
				element = org.camunda.spin.Spin.XML(inputStreamReader);
			}finally{
				if(inputStream!=null)
					try {
						inputStream.close();
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
					}
			}
		}else{
			element = org.camunda.spin.Spin.XML(xmlStr);
		}
		eleXPathQuery = element.xPath(expression);
		return eleXPathQuery;

	}

	/**
	 * @param prefix
	 *            ：如果xml有默认的命名空间，需要给命名空间加前缀
	 * @param namespace
	 *            ：命名空间
	 * @return
	 */
	public SpinXPathQuery ns(String prefix, String namespace) {
		return eleXPathQuery.ns(prefix, namespace);
	}
	/**
	 * 
	 * @return
	 */
	public String attribute() {
		attribute = eleXPathQuery.attribute().value();
		return attribute;
	}
	public String asString(){
		return attribute();
	}
	/**
	 * 
	 * @return
	 */
	public boolean asBoolean(){
		if(attribute==null){
			attribute();
		}
		return Boolean.valueOf(attribute);
	}
	/**
	 * 
	 * @return
	 */
	public double asNumber(){
		if(attribute==null){
			attribute();
		}
		return Double.valueOf(attribute);
	}
	/**
	 * yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public Date asDateTime(){
		if(attribute==null){
			attribute();
		}
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date=null;
		try {
			date = simpleDateFormat.parse(attribute);
		} catch (ParseException e) {
			logger.error(attribute+"不符合时间格式"+"yyyy-MM-dd hh:mm:ss");
		}  
		return date;
	}
}
