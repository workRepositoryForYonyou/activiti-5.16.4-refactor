package com.yonyou.bpm.webservice;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddSOAPHeader extends AbstractSoapInterceptor {
	private QName qname;
	private Map<String, String> values;

	public AddSOAPHeader(QName qname, Map<String, String> values) {
		super(Phase.WRITE);
		this.qname = qname;
		this.values = values;
	}

	public AddSOAPHeader() {
		super(Phase.WRITE);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
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
