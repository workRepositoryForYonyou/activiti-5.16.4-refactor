package com.yonyou.bpm.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.junit.Test;

import com.yonyou.bpm.webservice.AddSOAPHeader;

public class WebserviceTest {
	@Test
	public void test(){
		//加入header
		String wsdlString="http://20.12.13.110/uapws/service/nc.pub.pu.m21.bpm.OrderApproveService?wsdl";
		String operationStr="approveOrder";
		ArrayList<Object> paramStrings =new ArrayList<Object>();
		paramStrings.add("1001ZZ100000003Y6IZ6");
		paramStrings.add("CD1504160013");
		
		
		QName headQname=new QName("http://ws.uap.nc/lang", "Urc", "ns0");
		Map<String, String> headvalues=new HashMap<String, String>();
		headvalues.put("datasource", "bgy");
		headvalues.put("langCode", "simpchn");
		headvalues.put("userCode", "pxc");
		
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsdlString);
		client.getOutInterceptors().add(new AddSOAPHeader(headQname,headvalues));
		
		
		try {
			Object response =client.invoke(operationStr, paramStrings.toArray(new Object[0]));
			if(response!=null){
				System.out.print("111");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

}
