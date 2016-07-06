package com.yonyou.bpm.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.util.StringUtils;

public class ExceptionTest {
	@Test
	public void test(){
		String key="Cannot start process instance. Process definition {0} (id = {1}) is suspended";
		String str="Cannot start process instance. Process definition 'pppp' (id = '3333') is suspended";
		String value="流程定义{0}已经被挂起，无法创建流程实例！";
		String result=null;
		 List<String> argumentsList=null;

		 int argLength=0;
		 //特殊字符替换
		 key=key.replace("[", "\\[");
		 key=key.replace("]", "\\]");
		 
		 key=key.replace("(", "\\(");
		 
		 key=key.replace(")", "\\)");
		 
		 key=key.replace("|", "\\|");
		 if(key.indexOf("{0}")!=-1){
			 for(int i=0;i<10;i++){
				 String argTemp = "{"+i+"}";
				 if(key.indexOf(argTemp)==-1)break;
				 key = StringUtils.replace(key, argTemp, "'.*'");
				 argLength=i+1;
			 }
		 }
		
		 Pattern pattern=Pattern.compile(key);
		 Matcher  matcher= pattern.matcher(str);
		 if(matcher.find()){
			 result= value;
			 if(argLength>0){
				 argumentsList=new ArrayList<String>();
				 key=StringUtils.replace(key, ".*", "(.*)");
				 pattern=Pattern.compile(key);
				 matcher= pattern.matcher(str);
				 if(matcher.find()){
					 int count=matcher.groupCount();
					 for(int countIndex=0;countIndex<count;countIndex++){
						 try{
							 String matcherGroup=matcher.group(countIndex);
							 System.out.print(countIndex+":"+matcherGroup);
							 argumentsList.add(matcherGroup); 
						 }catch(Exception e){
							 
						 }
					 }
					 
				}
			 }
		 }
		 System.out.print(result);
		
		
	}
	@Test
	public void test21(){
		ArrayList argumentsList=new ArrayList<String>();
		String testStr = "this is '111' and '222'";  
		String testStrKey="this is {0} and {1}";
		
	}
	@Test
	public void test2(){
		ArrayList argumentsList=new ArrayList<String>();
		String testStr = "this is '111' and '222'";  
		String testStrKey="this is {0} and {1}";
		int temp=0;
		int firstIndex=0;
		for (int i=0;i<testStr.length();i++) {
			char a=testStr.charAt(i);
			
			if('\''==(a)){
				if(temp%2==0){
					firstIndex=i;
				}else{
					String re=testStr.substring(firstIndex+1,i);
					System.out.println(re);  
					argumentsList.add(re);
				}
				temp++;
			}
			
		}
	}
}
