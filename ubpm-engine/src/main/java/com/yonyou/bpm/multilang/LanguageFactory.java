package com.yonyou.bpm.multilang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import com.yonyou.bpm.BpmException;

public class LanguageFactory {
	 private static final String PROPERTIES="lang/simpchn/engine/exception_zh.properties";
	 private static final LanguageFactory languageFactory=new LanguageFactory();
	 private  Properties properties=null;
	 
	 public static LanguageFactory getInstance() {
			return languageFactory;
	 }
	 
	 /**
	  * 
	  * @param str no processes deployed with key '1'
	  * @param properties  no processes deployed with key {0}=没有Key为{0}的流程定义
	  * @return  没有Key为{0}的流程定义
	  */
	 public  String getValue(String str){
		 Properties properties =getProperties();
		 if(properties==null||properties.size()==0)
			 return str;
		 String result=str;
		 //参数个数
		 int argLength=0;
		 List<String> argumentsList=null;
		 //替换
		 for (Entry<Object, Object> entry: properties.entrySet()) {
			 String key=(String)entry.getKey();
			 argLength=0;
			 if(key.indexOf("{0}")!=-1){
				 for(int i=0;i<10;i++){
					 String argTemp = "{"+i+"}";
					 if(key.indexOf(argTemp)==-1)break;
					 key = StringUtils.replace(key, argTemp, ".*");
					 argLength=i+1;
				 }
			 }
			 //特殊字符替换
			 key=key.replace("[", "\\[");
			 key=key.replace("]", "\\]");
			 
			 key=key.replace("(", "\\(");
			 
			 key=key.replace(")", "\\)");
			 
			 key=key.replace("|", "\\|");
			 Pattern pattern=Pattern.compile(key);
			 Matcher  matcher= pattern.matcher(str);
			 if(matcher.find()){
				 result= (String)entry.getValue();
				 if(argLength>0){
					argumentsList= getParams(str);
					if(argumentsList==null||argumentsList.size()==0){
						argumentsList=new ArrayList<String>();
						 key=StringUtils.replace(key, ".*", "(.*)");
						 pattern=Pattern.compile(key);
						 matcher= pattern.matcher(str);
						 
						 if(matcher.find()){
							 argumentsList.add(matcher.group(1));
						}
					}
				 }
				 break;
			 }
		}
		if(argLength==0)return result;
		//替换变量
		int count = argumentsList == null ? 0 : argumentsList.size();
		for (int i = 0; i < count; i++){
			String arg = "{"+i+"}";
			result = StringUtils.replace(result, arg, argumentsList.get(i)==null? "" : argumentsList.get(i));
		}
		 
		return result;
	 }

	private List<String> getParams(String str) {
		List<String> argumentsList = new ArrayList<String>();
		int temp = 0;
		int firstIndex = 0;
		for (int i = 0; i < str.length(); i++) {
			char a = str.charAt(i);

			if ('\'' == (a)) {
				if (temp % 2 == 0) {
					firstIndex = i;
				} else {
					String re = str.substring(firstIndex + 1, i);
					argumentsList.add(re);
				}
				temp++;
			}

		}
		return argumentsList;

	}
	 private  Properties getProperties(){
		 
		if(properties!=null)return properties;
		
		 Resource propertiesListResource = new DefaultResourceLoader() .getResource(PROPERTIES);
		 EncodedResource encRes=new EncodedResource(propertiesListResource,"UTF-8");  ;
		 try {
			 properties=PropertiesLoaderUtils.loadProperties(encRes);
		} catch (IOException e) {
			throw new BpmException("加载多语资源文件出错了");
		}
		 return properties;
	 }
}
