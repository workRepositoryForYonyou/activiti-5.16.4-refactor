package com.yonyou.bpm.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.junit.Test;

import com.yonyou.bpm.utils.XpathUtilBean;

public class XmlPathTest {
	
	@Test
	public void test(){
		
		File file=new File("C:\\Users\\zhaohb.PDOMAIN\\Downloads\\zhaohb_multi_sequence.bpmn20.xml");
		try {
			FileInputStream fi=new FileInputStream(file);
			
			XpathUtilBean xpathUtilBean=new XpathUtilBean();
			InputStreamReader inputStreamReader=new java.io.InputStreamReader(fi);
			String a=xpathUtilBean.xpath(inputStreamReader, "/definitions/process/id").attribute().toString();
			if(a!=null){
				System.out.print(a);
			}
		} catch (Exception e) {
		}
		
	}

}
