package com.yonyou.bpm.engine.service;

import org.activiti.engine.impl.ProcessEngineImpl;

import com.yonyou.bpm.core.category.CategoryService;
import com.yonyou.bpm.core.user.IUserService;
import com.yonyou.bpm.engine.BpmProcessEngine;
import com.yonyou.bpm.engine.conf.BpmEngineConfiguration;

public class BpmProcessEngineImpl extends ProcessEngineImpl implements BpmProcessEngine{
	
	protected CategoryService categoryService;
	
	protected IUserService userService;

	public BpmProcessEngineImpl(
			BpmEngineConfiguration processEngineConfiguration) {
		super(processEngineConfiguration);
		this.categoryService = processEngineConfiguration.getCategoryService();
		this.userService = processEngineConfiguration.getUserService();
	}

	@Override
	public CategoryService getCategoryService() {
		return categoryService;
	}

	@Override
	public IUserService getUserService() {
		return userService;
	}

}
