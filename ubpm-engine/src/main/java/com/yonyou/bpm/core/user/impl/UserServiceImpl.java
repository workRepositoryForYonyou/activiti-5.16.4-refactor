/**
 * 
 */
package com.yonyou.bpm.core.user.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.yonyou.bpm.core.dao.BaseDao;
import com.yonyou.bpm.core.user.IUserService;
import com.yonyou.bpm.core.user.entity.UserEntity;

/**
 * @author chouhl
 *
 * 2015年5月21日 下午3:07:20
 */
public class UserServiceImpl implements IUserService {
	
	private static final String RESOURCE="com/yonyou/bpm/entity/User.xml";
	
	private static final String NAMESPACE = "com.yonyou.bpm.engine.mapper.UserMapper";
	
	private SqlSessionFactory sqlSessionFactory;
	
	private CommandExecutor commandExecutor;
	
	public UserServiceImpl(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory=sqlSessionFactory;
		this.initMappingResource();
	}
	
	protected synchronized void initMappingResource(){
		if(!this.sqlSessionFactory.getConfiguration().getMappedStatementNames().contains(NAMESPACE + ".extselectUserById")){
			BaseDao baseDao=new BaseDao(this.sqlSessionFactory);
			this.sqlSessionFactory=baseDao.addClassResource(RESOURCE).getSqlSessionFactory();
		}
	}
	
	@Override
	public UserEntity findUserById(final String id) {
		return commandExecutor.execute(new Command<UserEntity>() {
			@Override
			public UserEntity execute(
					CommandContext commandContext) {
				Map<String, Object> param = new HashMap<String, Object>(2, 1);
				param.put("userId", id);
				
				UserEntity entity =  sqlSessionFactory.openSession().selectOne(NAMESPACE + ".extselectUserById", param);
				return entity;
			}
		});
	}
	
	@Override
	public UserEntity[] findUserByIds(final String[] ids) {
		return commandExecutor.execute(new Command<UserEntity[]>() {
			@Override
			public UserEntity[] execute(
					CommandContext commandContext) {
				Map<String, Object> param = new HashMap<String, Object>(2, 1);
				param.put("userId", ids);
				List<UserEntity> entity = sqlSessionFactory.openSession().selectList(NAMESPACE + ".extselectUserList", param);
				if(entity == null){
					entity = new ArrayList<UserEntity>(0);
				}
						
				return entity.toArray(new UserEntity[0]);
			}
		});
		
	}
	

	
	@Override
	public int saveUser(final UserEntity user) {
		
		if(user.getId() == null||"".equals(user.getId().trim())){
			String id=new UUIDGenerator().generateId(user).toString();
			user.setCreateTime(new Date());
			user.setId(id);
		}
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(CommandContext commandContext) {
				return sqlSessionFactory.openSession().insert(NAMESPACE + ".extinsertUser", user);
			}
		});
	}
	
	@Override
	public int updateUser(final UserEntity user) {
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(CommandContext commandContext) {
				return sqlSessionFactory.openSession().update(NAMESPACE + ".extupdateUser", user);
			}
		});
	}

	public CommandExecutor getCommandExecutor() {
		return commandExecutor;
	}

	public void setCommandExecutor(CommandExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}

	@Override
	public int deleteUser(final UserEntity user) {
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(CommandContext commandContext) {
				return sqlSessionFactory.openSession().delete(NAMESPACE + ".deleteUser", user);
			}
		});
	}
	
	@Override
	public List<UserEntity> findAllUser() {
		return commandExecutor.execute(new Command<List<UserEntity>>() {
			@Override
			public List<UserEntity> execute(
					CommandContext commandContext) {
				UserQueryParam userQueryParam=new UserQueryParam();
				RowBounds rowBounds=new RowBounds(userQueryParam.getFirstResult(), userQueryParam.getMaxResults());
				return sqlSessionFactory.openSession().selectList("bpmSelectUsersByQueryCriteria", null, rowBounds);
				
			}
		});
	}
	
	@Override
	public List<UserEntity> findUserByCodeOrName(final String code,final String name) {
		return commandExecutor.execute(new Command<List<UserEntity>>() {
			@Override
			public List<UserEntity> execute(
					CommandContext commandContext) {
				UserQueryParam userQueryParam=new UserQueryParam();
				userQueryParam.codeLike(code);
				userQueryParam.nameLike(name);
				return (List<UserEntity>) getList(userQueryParam);
			}
		});
		
	}
	
	@Override
	public List<UserEntity> getList(final UserQueryParam userQueryParam){
		final RowBounds rowBounds=new RowBounds(userQueryParam.getFirstResult(), userQueryParam.getMaxResults());
		
		return commandExecutor.execute(new Command<List<UserEntity>>() {
			@Override
			public List<UserEntity> execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectList("bpmSelectUsersByQueryCriteria", userQueryParam, rowBounds);
			}
		});
		
	}
	
	@Override
	public Long selectUserCount(){
		return commandExecutor.execute(new Command<Long>() {
			@Override
			public Long execute(CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectOne("bpmSelectUserCountByQueryCriteria");
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> findUsersByQueryCriteria(UserQueryParam userQueryParam, Page page) {
	    final String query = "bpmSelectUsersByQueryCriteria";
	    return Context.getCommandContext().getSession(DbSqlSession.class).selectList(query, userQueryParam, page);
	}

	@Override
	public int sqlserverBatchUpdateUser(final List<UserEntity> userList) {
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(CommandContext commandContext) {
				return sqlSessionFactory.openSession().update(NAMESPACE + ".sqlserverBatchExtupdateUser", userList);
			}
		});
	}

	@Override
	public int sqlserverBatchSaveUser(final List<UserEntity> userList) {
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(CommandContext commandContext) {
				List<UserEntity> nUserList = new ArrayList<UserEntity>();
				for (int i = 0; i < userList.size(); i++) {
					UserEntity user = userList.get(i);
					if(user.getId() == null||"".equals(user.getId().trim())){
						String id=new UUIDGenerator().generateId(user).toString();
						user.setCreateTime(new Date());
						user.setId(id);
					}
					nUserList.add(user);
				}
				return sqlSessionFactory.openSession().insert(NAMESPACE + ".sqlserverBatchExtinsertUser", nUserList);
			}
		});
	}

	@Override
	public int oracleBatchUpdateUser(final List<UserEntity> userList) {
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(CommandContext commandContext) {
				return sqlSessionFactory.openSession().update(NAMESPACE + ".oracleBatchExtupdateUser", userList);
			}
		});
	}

	@Override
	public int oracleBatchSaveUser(final List<UserEntity> userList) {
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(CommandContext commandContext) {
				List<UserEntity> nUserList = new ArrayList<UserEntity>();
				for (int i = 0; i < userList.size(); i++) {
					UserEntity user = userList.get(i);
					if(user.getId() == null||"".equals(user.getId().trim())){
						String id=new UUIDGenerator().generateId(user).toString();
						user.setCreateTime(new Date());
						user.setId(id);
					}
					nUserList.add(user);
				}
				return sqlSessionFactory.openSession().insert(NAMESPACE + ".oracleBatchExtinsertUser", nUserList);
			}
		});
	}
}
