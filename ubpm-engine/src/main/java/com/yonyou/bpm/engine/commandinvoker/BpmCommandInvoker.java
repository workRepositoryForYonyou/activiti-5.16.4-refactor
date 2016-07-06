package com.yonyou.bpm.engine.commandinvoker;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandInvoker;
import org.activiti.engine.impl.javax.el.ELException;

import com.yonyou.bpm.multilang.LanguageFactory;

/**
 * 名称执行器，异常翻译处理
 * 
 * @author zhaohb
 *
 */
public class BpmCommandInvoker extends CommandInvoker {

	@Override
	public <T> T execute(CommandConfig config, Command<T> command) {
		T obj = null;
		try {
			obj = command.execute(Context.getCommandContext());
		} catch (ActivitiException activitiException) {
			dealException(activitiException);
		}

		return obj;
	}

	private void dealException(ActivitiException activitiException) {
		ELException eLException=getELException(activitiException);
		if(eLException!=null){
			Throwable cause=eLException.getCause();
			if(cause!=null&&cause instanceof ActivitiException){
				activitiException=(ActivitiException)cause;
			}
		}
		if (activitiException != null) {
			String msg = activitiException.getMessage();
			if (msg != null) {
				if (activitiException instanceof ActivitiObjectNotFoundException) {
					ActivitiObjectNotFoundException activitiObjectNotFoundException = (ActivitiObjectNotFoundException) activitiException;
					throw new ActivitiObjectNotFoundException(convert(msg),
							activitiObjectNotFoundException.getObjectClass(),
							activitiException);
				} else if (activitiException instanceof ActivitiIllegalArgumentException) {
					throw new ActivitiIllegalArgumentException(convert(msg),
							activitiException);
				} else {
					throw new ActivitiException(convert(msg), activitiException);
				}
			}
		}
	}
	private ELException getELException(Throwable activitiException){
		Throwable throwable=activitiException.getCause();
		if(throwable==null||throwable instanceof ELException){
			return (ELException)throwable;
		}
		return getELException(throwable);
		
	}
	private String convert(String msg) {
		String result = null;
		try {
			result = LanguageFactory.getInstance().getValue(msg);
		} catch (Exception e) {

		}
		if (result == null || "".equals(result.trim())) {
			result = "出错了";
		}
		return result;
	}

}
