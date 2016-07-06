package com.yonyou.bpm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassUtils {
	private static Logger logger = LoggerFactory.getLogger(ClassUtils.class);
	
	public static Class<?> forName(String className, ClassLoader loader) {
		try {
			return Class.forName(className, true, loader);
		} 
		catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static Class<?> forName(String className) {
		return forName(className, Thread.currentThread().getContextClassLoader());
	}
	
	public static Object newInstance(String className) {
		if(className == null || className.equals(""))
			throw new RuntimeException("classname 不能为空");
		return newInstance(forName(className));
	}
	
	public static <T>T newInstance(Class<T> c) {
		if(c==null)return null;
		try {
			return c.newInstance();
		}catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new RuntimeException("构造类"+c.getName()+"出错："+e.getMessage(), e);
		} 
	}

}
