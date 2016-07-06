package com.yonyou.bpm.participant;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.impl.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.participant.adapter.ParticipantAdapter;
import com.yonyou.bpm.participant.config.ParticipantConfig;
import com.yonyou.bpm.participant.config.ParticipantFilterConfig;

/**
 * 参与者配置工厂类
 * 
 * @author zhaohb
 *
 */
public class ParticipantFactory {
	private static Logger log = LoggerFactory
			.getLogger(ParticipantFactory.class);
	private Map<String, ParticipantConfig> participantConfigs;
	private Map<String, ParticipantFilterConfig> participantFilterConfigs;

	private static ParticipantFactory inst = new ParticipantFactory();

	public static ParticipantFactory getInstance() {
		return inst;
	}

	public synchronized Map<String, ParticipantFilterConfig> getParticipantFilterConfigs() {
			initConfig();
		return participantFilterConfigs;
	}

	public synchronized Map<String, ParticipantConfig> getParticipantConfigs() {
			initConfig();
		return participantConfigs;
	}

	public void setParticipantConfigs(
			Map<String, ParticipantConfig> participantConfigs) {
		this.participantConfigs = participantConfigs;
	}

	public void setParticipantFilterConfigs(
			Map<String, ParticipantFilterConfig> participantFilterConfigs) {
		this.participantFilterConfigs = participantFilterConfigs;
	}

	private void initParticipantConfigs(Resource springResource) {
		DefaultListableBeanFactory defaultListableBeanFactory = getDefaultListableBeanFactory(springResource);
		if (defaultListableBeanFactory != null) {
			Map<String, ParticipantConfig> beanMap = defaultListableBeanFactory
					.getBeansOfType(ParticipantConfig.class);
			if (beanMap != null && beanMap.size() > 0) {
				for (Map.Entry<String,ParticipantConfig> participantConfigTemp: beanMap.entrySet()) {
					ParticipantConfig participantConfig=participantConfigs.get(participantConfigTemp.getKey());
					if(participantConfig==null){
						participantConfigs.put(participantConfigTemp.getKey(), participantConfigTemp.getValue());
					}else{
						int tempP1=participantConfigTemp.getValue().getPriority();
						int tempP2=participantConfig.getPriority();
						ParticipantAdapter  participantAdapter1 =participantConfig.getAdapter();
						ParticipantAdapter  participantAdapter2 =participantConfigTemp.getValue().getAdapter();
						System.out.println(participantAdapter1+"   "+participantAdapter2);
						log.info("参与者配置相中存在配置编码相同的两项：编码为："+participantConfigTemp.getKey()+";优先级分别为："+tempP1+"、"+tempP2+"");
						log.info(";查询实现分别为："+participantConfigTemp.getValue().getAdapterClazz()+"、"+participantConfig.getAdapterClazz()+"");
						//已经存在的参与者配置，比较优先级大小,保留优先级比较大的
						if(tempP1>tempP2){
							participantConfigs.put(participantConfigTemp.getKey(), participantConfigTemp.getValue());
						}
					}
				}
//				participantConfigs.putAll(beanMap);
			}
			Map<String, ParticipantFilterConfig> filterBeanMap = defaultListableBeanFactory
					.getBeansOfType(ParticipantFilterConfig.class);
			if (filterBeanMap != null && filterBeanMap.size() > 0) {
				participantFilterConfigs.putAll(filterBeanMap);
			}
		}
	}
	

	
	private void initConfig() {
		if (participantConfigs != null) {
			return;
		}else{
			participantConfigs=new HashMap<String, ParticipantConfig>(8);
		}
		ClassLoader classLoader = ReflectUtil.getClassLoader();
		Enumeration<URL> resources = null;
		try {
			resources = classLoader.getResources("yonyou.participant.cfg.xml");
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"problem retrieving yonyou.participant.cfg.xml resources on the classpath: "
							+ System.getProperty("java.class.path"), e);
		}

		Set<URL> configUrls = new HashSet<URL>();
		while (resources.hasMoreElements()) {
			configUrls.add(resources.nextElement());
		}
		for (Iterator<URL> iterator = configUrls.iterator(); iterator.hasNext();) {
			URL resource = iterator.next();
			log.info(
					"Initializing participantFactory using configuration '{}'",
					resource.toString());
			InputStream inputStream = null;
			try {
				inputStream = resource.openStream();
				Resource springResource = new InputStreamResource(inputStream);
				initParticipantConfigs(springResource);
			} catch (IOException e) {
				throw new IllegalArgumentException(
						"couldn't open resource stream: " + e.getMessage(), e);
			} finally {
				IoUtil.closeSilently(inputStream);
			}

		}
	}
	private DefaultListableBeanFactory getDefaultListableBeanFactory(
			Resource springResource) {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(
				beanFactory);
		xmlBeanDefinitionReader
				.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
		xmlBeanDefinitionReader.loadBeanDefinitions(springResource);
		return beanFactory;
	}

}
