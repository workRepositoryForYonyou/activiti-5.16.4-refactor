package com.yonyou.bpm.engine.core;
/**
 * 流程实例状态
 * @author zhaohb
 *
 */
public enum ProcessInstanceState {
	/**
	 * 运行中
	 */
	RUN,
	/**
	 * 结束
	 */
	END,
	/**
	 * 终止
	 */
	DELETE,
	/**
	 * 挂起
	 */
	SUSPENDED;
	
	@Override
	public String toString() {
		String str=super.toString();
		return str.toLowerCase();
	}

}
