package com.yonyou.bpm.delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.webservice.AddSOAPHeader;
/**
 * WebService实现
 * @author zhaohb
 *
 */
public class DefaultWebServiceDelegate implements JavaDelegate {
	private static final String WSDL="wsdl";
	private static final String OPERATION="operation";
	private static final String PARAMETERS="parameters";
	private static final String RETURNVALUE="returnValue";
	
	private QName headQname;
	private Map<String, String> headvalues;
	
	private Expression wsdl;
	private Expression operation; 
	//"a","b" "${apram}" "${getAraay("a","b")}"
	private Expression parameters;
	private Expression returnValue;
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String activityId=execution.getCurrentActivityId();
		String processDefinitionId=execution.getProcessDefinitionId();
		BpmnModel bpmnModel=execution.getEngineServices().getRepositoryService().getBpmnModel(processDefinitionId);
		FlowElement  flowElement =bpmnModel.getFlowElement(activityId);
		ServiceTask serviceTask=(ServiceTask)flowElement;
		
		List<FieldExtension> fieldExtensions=serviceTask.getFieldExtensions();
		if(getValue(fieldExtensions,WSDL)==null||getValue(fieldExtensions,OPERATION)==null){
			throw new BpmException("'wsdl' and 'operation' can not be null");
		}
		init(fieldExtensions);
		
		String wsdlString = (String)wsdl.getValue(execution);
		
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsdlString);
		if(headQname!=null&&headvalues!=null){
			client.getOutInterceptors().add(new AddSOAPHeader(headQname,headvalues));
		}
		
		String operationStr=(String)operation.getValue(execution);
		
		 
		ArrayList<Object> paramStrings =new ArrayList<Object>();
	    if (parameters!=null) {     
	    	Object parametersObj=parameters.getValue(execution);
	    	if(parametersObj!=null){
	    		if(parametersObj instanceof Collection){
	    			@SuppressWarnings("unchecked")
					Collection<Object> collection=(Collection<Object>)parametersObj;
	    			for (Object object : collection) {
	    				paramStrings.add(object);
					}
	    		}
	    	}
	    }   
        Object response =null;
        response=client.invoke(operationStr, paramStrings.toArray(new Object[0]));
	    
	    
	    if(returnValue!=null){
	    	 String returnVariableName = (String) returnValue.getValue(execution);
	         execution.setVariable(returnVariableName, response);
	    }
		
	}
	private void init(List<FieldExtension> fieldExtensions){
		wsdl=createExpression(getValue(fieldExtensions, WSDL).getExpression());
		operation=createExpression(getValue(fieldExtensions, OPERATION).getExpression());
		FieldExtension parametersFieldExtension= getValue(fieldExtensions, PARAMETERS);
		if(parametersFieldExtension!=null){
			String parametersStr=parametersFieldExtension.getExpression();
			if(parametersStr!=null&&!"".equals(parametersStr.trim())){
				parameters=createExpression(parametersStr);
			}
			
		}
		FieldExtension returnvalueFieldExtension= getValue(fieldExtensions, RETURNVALUE);
		if(returnvalueFieldExtension!=null){
			String returnvalueStr=returnvalueFieldExtension.getExpression();
			if(returnvalueStr!=null&&!"".equals(returnvalueStr.trim())){
				returnValue=createExpression(returnvalueStr);
			}
			
		}
		
	}
	private FieldExtension getValue(List<FieldExtension> fieldExtensions,String key){
		if(fieldExtensions==null||fieldExtensions.size()==0)return null;
		if(key==null||"".equals(key.trim())){
			throw new BpmException("'key' can not be null!");
		}
		for (FieldExtension fieldExtension : fieldExtensions) {
			if(key.equals(fieldExtension.getFieldName())){
				return fieldExtension;
			}
		}
		return null;
	}
	
	private Expression createExpression(String expressionStr){
		ExpressionManager  expressionManager =Context.getProcessEngineConfiguration().getExpressionManager();
		Expression expression=expressionManager.createExpression(expressionStr);
		return expression;
	}
	
	private void addHeaders(QName qname, Map<String, String> values,SoapMessage message){
		if (values == null || values.size() == 0)
			return;
		if (qname == null)
			return;

		Document doc = DOMUtils.createDocument();
		Element root = doc.createElementNS(qname.getNamespaceURI(),
				qname.getPrefix() + ":" + qname.getLocalPart());
		for (Map.Entry<String, String> valueEntryTemp : values.entrySet()) {
			String key = valueEntryTemp.getKey();
			String value = valueEntryTemp.getValue();
			Element elementTemp = doc.createElementNS(qname.getNamespaceURI(),
					qname.getPrefix() + ":" + key);
			elementTemp.setTextContent(value);
			root.appendChild(elementTemp);
		}
		SoapHeader head = new SoapHeader(qname, root);
		List<Header> headers = message.getHeaders();
		headers.add(head);
	}
}
