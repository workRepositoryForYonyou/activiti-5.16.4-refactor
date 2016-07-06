package com.yonyou.bpm.engine.rules;

import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.activiti.engine.impl.persistence.entity.ResourceEntity;
import org.activiti.engine.repository.Deployment;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;

public class BpmRulesHelper {

	  public static KnowledgeBase findKnowledgeBaseByDeploymentId(String deploymentId) {
	    DeploymentCache<Object> knowledgeBaseCache = Context
	      .getProcessEngineConfiguration()
	      .getDeploymentManager()
	      .getKnowledgeBaseCache();
	  
	    KnowledgeBase knowledgeBase = (KnowledgeBase) knowledgeBaseCache.get(deploymentId);
	    if (knowledgeBase==null) {
	      DeploymentEntity deployment = Context
	        .getCommandContext()
	        .getDeploymentEntityManager()
	        .findDeploymentById(deploymentId);
	      if (deployment==null) {
	        throw new ActivitiObjectNotFoundException("无法找到部署器： "+deploymentId, Deployment.class);
	      }
	      Context
	        .getProcessEngineConfiguration()
	        .getDeploymentManager()
	        .deploy(deployment);
	      knowledgeBase = (KnowledgeBase) knowledgeBaseCache.get(deploymentId);
	      //缓存中找不到再去数据库中查找
	      if(knowledgeBase==null){
		      Map<String, ResourceEntity> resources= deployment.getResources();
		      KnowledgeBuilder knowledgeBuilder = null;
		      for (String resourceName : resources.keySet()) {
		          if (resourceName.endsWith(".drl")) { // is only parsing .drls sufficient? what about other rule dsl's? (@see ResourceType)
		            if (knowledgeBuilder==null) {
		              knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		            }
		            ResourceEntity resourceEntity = resources.get(resourceName);
		            byte[] resourceBytes = resourceEntity.getBytes();
		            Resource droolsResource = ResourceFactory.newByteArrayResource(resourceBytes);
		            knowledgeBuilder.add(droolsResource, ResourceType.DRL);
		          }
		        }
		        
		        if (knowledgeBuilder!=null) {
		          knowledgeBase = knowledgeBuilder.newKnowledgeBase();
		          knowledgeBaseCache.add(deployment.getId(), knowledgeBase);
		        }
	      }
	      if (knowledgeBase==null) {
	        throw new ActivitiException("部署器： "+deploymentId+" 中不存在规则。");
	      }
	    }
	    return knowledgeBase;
	  }


}
