package com.yonyou.bpm.engine;

import org.activiti.engine.EngineServices;

import com.yonyou.bpm.core.category.CategoryService;
import com.yonyou.bpm.core.user.IUserService;

public interface BpmEngineServices extends EngineServices{
	
	public CategoryService getCategoryService();
	
	public IUserService getUserService();
	
}
