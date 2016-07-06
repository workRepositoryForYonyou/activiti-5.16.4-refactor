/**
 * 
 */
package com.yonyou.bpm.core.user;

import java.util.List;

import org.activiti.engine.impl.Page;

import com.yonyou.bpm.core.user.entity.UserEntity;
import com.yonyou.bpm.core.user.impl.UserQueryParam;

/**
 * @author chouhl
 *
 * 2015年5月21日 下午3:04:06
 */
public interface IUserService {

	public UserEntity findUserById(String id);
	
	public UserEntity[] findUserByIds(String[] ids);
	
	public int saveUser(UserEntity user);
	
	//修改
	public int updateUser(UserEntity user);
	
	public int deleteUser(UserEntity user);
	
	//查找全部
	public List<UserEntity> findAllUser() ;
	
	public List<UserEntity> findUserByCodeOrName(final String code,final String name);
	
    List<UserEntity> getList(UserQueryParam userQueryParam);
    
    public Long selectUserCount();
    
    public List<UserEntity> findUsersByQueryCriteria(UserQueryParam userQueryParam, Page page);
    
    //解决数据库连接池不能创建bug  for yonyou-gov
    
    //批量修改
  	public int sqlserverBatchUpdateUser(List<UserEntity> userList);
  	public int oracleBatchUpdateUser(List<UserEntity> userList);
  	
  	//批量插入
  	public int sqlserverBatchSaveUser(List<UserEntity> userList);
  	public int oracleBatchSaveUser(List<UserEntity> userList);

}
