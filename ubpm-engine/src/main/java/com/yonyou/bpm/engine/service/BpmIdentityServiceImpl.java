package com.yonyou.bpm.engine.service;

import org.activiti.engine.identity.User;
import org.activiti.engine.impl.IdentityServiceImpl;

import com.yonyou.bpm.trace.TraceKeys;
import com.yonyou.bpm.trace.TracePoint;
import com.yonyou.bpm.trace.TraceTarget;
import com.yonyou.bpm.trace.TraceValue;

@TraceTarget("BpmIdentityService")
public class BpmIdentityServiceImpl extends IdentityServiceImpl{
	
//	private UserAuthenticator authenticator;
	
//	public BpmIdentityServiceImpl(UserAuthenticator authenticator) {
//		if (authenticator == null) {
//			this.authenticator = new DefaultUserAuthenticator();
//		}else{
//			this.authenticator = authenticator;
//		}
//	}
	
	public BpmIdentityServiceImpl() {
	}
	
	@TracePoint
	@Override
	public User newUser(@TraceValue(TraceKeys.USER_ID) String userId) {
		return super.newUser(userId);
	}
	
	@TracePoint
	@Override
	public void setUserInfo(@TraceValue(TraceKeys.USER_ID)String userId, String key, String value) {
		super.setUserInfo(userId, key, value);
	}
	
	@TracePoint
	@Override
	public void deleteUser(@TraceValue(TraceKeys.USER_ID) String userId) {
		super.deleteUser(userId);
	}
	
	@Override
	public boolean checkPassword(String userId, String password) {
		return super.checkPassword(userId, password);
//		return authenticator.check(userId, password);
	}
	
//	private boolean doCheckPassword(String userId, String password) {
//		return super.checkPassword(userId, password);
//	}
//	
//	
//	private class DefaultUserAuthenticator implements UserAuthenticator{
//
//		@Override
//		public boolean check(String userId, String password) {
//			return true;// doCheckPassword(userId, password);
//		}
//		
//	}
}
