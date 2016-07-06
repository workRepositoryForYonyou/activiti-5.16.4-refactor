package com.yonyou.bpm.core.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.yonyou.bpm.core.category.Category;
import com.yonyou.bpm.core.category.CategoryLink;
import com.yonyou.bpm.core.category.CategoryService;
import com.yonyou.bpm.core.dao.BaseDao;
import com.yonyou.bpm.core.entity.CategoryEntity;
/**
 * 流程目录默认服务实现
 * @author zhaohb
 *
 */
public class CategoryServiceImpl implements CategoryService {
	private static Logger logger = LoggerFactory
			.getLogger(CategoryServiceImpl.class);
	private static final String RESOURCE="com/yonyou/bpm/entity/category.xml";
	private static final String LINKRESOURCE="com/yonyou/bpm/entity/categoryLink.xml";
	private SqlSessionFactory sqlSessionFactory;
	
	private CommandExecutor commandExecutor;
	
	public CategoryServiceImpl(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
		initMappingResource();
	}
	protected void initMappingResource(){
			if(!this.sqlSessionFactory.getConfiguration().getMappedStatementNames().contains("insertBpmCategory")){
				BaseDao baseDao=new BaseDao(this.sqlSessionFactory);
				this.sqlSessionFactory=baseDao.addClassResource(RESOURCE).getSqlSessionFactory();
			}
			if(!this.sqlSessionFactory.getConfiguration().getMappedStatementNames().contains("insertBpmCategoryLink")){
				BaseDao baseDao=new BaseDao(this.sqlSessionFactory);
				this.sqlSessionFactory=baseDao.addClassResource(LINKRESOURCE).getSqlSessionFactory();
			}
			
	}
	
	@Override
	public Category getCategoryById(final String categoryId) {
		return commandExecutor.execute(new Command<Category>() {
			@Override
			public Category execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectOne("selectBpmCategory", categoryId);
			}
		});
	}

	@Override
	public List<? extends Category> findAllCategory() {
		return commandExecutor.execute(new Command<List<? extends Category>>() {
			@Override
			public List<? extends Category> execute(
					CommandContext commandContext) {
				CategoryQueryParam categoryQueryParam=new CategoryQueryParam();
				RowBounds rowBounds=new RowBounds(categoryQueryParam.getFirstResult(), categoryQueryParam.getMaxResults());
				return sqlSessionFactory.openSession().selectList("selectBpmCategories", null, rowBounds);
			}
		});
		
	}
	
	@Override
	public List<? extends Category> findAllCategoryWithApplication(){
		return commandExecutor.execute(new Command<List<? extends Category>>() {
			@Override
			public List<? extends Category> execute(
					CommandContext commandContext) {
				CategoryQueryParam categoryQueryParam=new CategoryQueryParam().withApplication();
				RowBounds rowBounds=new RowBounds(categoryQueryParam.getFirstResult(), categoryQueryParam.getMaxResults());
				return sqlSessionFactory.openSession().selectList("selectBpmCategories", categoryQueryParam, rowBounds);
			}
		});
	}

	@Override
	public List<? extends Category> findCategoriesByIds(final String[] ids) {
		if(ids==null||ids.length==0)return null;
		return commandExecutor.execute(new Command<List<? extends Category>>() {
			@Override
			public List<? extends Category> execute(
					CommandContext commandContext) {
				CategoryQueryParam categoryQueryParam=new CategoryQueryParam();
				RowBounds rowBounds=new RowBounds(categoryQueryParam.getFirstResult(), categoryQueryParam.getMaxResults());
				return sqlSessionFactory.openSession().selectList("selectBpmCategoriesByIds", ids, rowBounds);
			}
		});
	}

	@Override
	public List<? extends Category> findCategoriesByKeyWord(final String keyWord) {
		return commandExecutor.execute(new Command<List<? extends Category>>() {
			@Override
			public List<? extends Category> execute(
					CommandContext commandContext) {
				CategoryQueryParam categoryQueryParam=new CategoryQueryParam();
				categoryQueryParam.codeLike(keyWord);
				categoryQueryParam.nameLike(keyWord);
				return (List<? extends Category>) getList(categoryQueryParam);
			}
		});
		
	}

	@Override
	public List<? extends Category> findCategoriesByCodeOrName(final String code,final String name) {
		return commandExecutor.execute(new Command<List<? extends Category>>() {
			@Override
			public List<? extends Category> execute(
					CommandContext commandContext) {
				CategoryQueryParam categoryQueryParam=new CategoryQueryParam();
				categoryQueryParam.codeLike(code);
				categoryQueryParam.nameLike(name);
				return (List<? extends Category>) getList(categoryQueryParam);
			}
		});
		
	}


	@Override
	public int delete(final Category category) throws Exception{
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().delete("deleteBpmCategory", category);
			}
		});
	
	}

	@Override
	public int delete(Category[] categories)throws Exception {
		int result=0;
		for (final Category category : categories) {
			int temp= commandExecutor.execute(new Command<Integer>() {
				@Override
				public Integer execute(
						CommandContext commandContext) {
					return sqlSessionFactory.openSession().delete("deleteBpmCategory", category);
				}
			});
			result+=temp;
		}
		
		return result;
		
	}

	@Override
	public int save(final Category category) throws Exception {
		int result=0;
			if(category.getId()==null||"".equals(category.getId().trim())){
				if(category instanceof CategoryEntity){
					final CategoryEntity categoryEntity=(CategoryEntity)category;
					String id=new UUIDGenerator().generateId(categoryEntity).toString();
					categoryEntity.setCreateTime(new Date());
					categoryEntity.setId(id);
					return commandExecutor.execute(new Command<Integer>() {
						@Override
						public Integer execute(
								CommandContext commandContext) {
							return sqlSessionFactory.openSession().insert("insertBpmCategory", categoryEntity);
						}
					});
				}
				
			}else{
				if(category instanceof CategoryEntity){
					CategoryEntity categoryEntity=(CategoryEntity)category;
					categoryEntity.setModifyTime(new Date());
				}
				return commandExecutor.execute(new Command<Integer>() {
					@Override
					public Integer execute(
							CommandContext commandContext) {
						return sqlSessionFactory.openSession().update("updateBpmCategory", category);
					}
				});
			}
		return result;
	}

	
	@Override
	public int save(List<CategoryLink> categoryLinks) throws Exception {
		if(categoryLinks==null||categoryLinks.size()==0)return 0;
		int result=0;
			for (final CategoryLink categoryLink : categoryLinks) {
				int temp=0;
				if (null==categoryLink.getId()||"".equals(categoryLink.getId().trim()) ) {
					categoryLink.setId(UUID.randomUUID().toString());
					temp= commandExecutor.execute(new Command<Integer>() {
						@Override
						public Integer execute(
								CommandContext commandContext) {
							return sqlSessionFactory.openSession().insert("insertBpmCategoryLink", categoryLink);
						}
					});
				}else{
					 temp= commandExecutor.execute(new Command<Integer>() {
						@Override
						public Integer execute(
								CommandContext commandContext) {
							return sqlSessionFactory.openSession().update("updateBpmCategoryLink", categoryLink);
						}
					});
				}
				result+=temp;
			}
		return result;
	}
	@Override
	public int delete(List<CategoryLink> categoryLinks) throws Exception {
		if(categoryLinks==null||categoryLinks.size()==0)return 0;
		int result = 0;
			for (final CategoryLink categoryLink : categoryLinks) {
				
				int temp= commandExecutor.execute(new Command<Integer>() {
					@Override
					public Integer execute(
							CommandContext commandContext) {
						return sqlSessionFactory.openSession().delete("deleteBpmCategoryLink", categoryLink);
					}
				});
				 result+=temp;
			}
		return result;
	}
	@Override
	public List<? extends Category> findAuthorizedCategoriesByUserId(
			final CategoryLinkQueryParam categoryLinkQueryParam) {
		
		if(categoryLinkQueryParam==null||categoryLinkQueryParam.userId==null||"".equals(categoryLinkQueryParam.userId.trim())){
			throw new IllegalArgumentException("'userId' can not be null!");
		}
		return commandExecutor.execute(new Command<List<? extends Category>>() {
			@Override
			public List<? extends Category> execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectList("selectBpmCategoriesByUser", categoryLinkQueryParam);
			}
		});
		
	}

	@Override
	public boolean isAuthorized(final CategoryLinkQueryParam categoryLinkQueryParam) {
		if(categoryLinkQueryParam==null||categoryLinkQueryParam.userId==null||"".equals(categoryLinkQueryParam.userId.trim())){
			throw new IllegalArgumentException("'userId' can not be null!");
		}
		if(categoryLinkQueryParam==null||categoryLinkQueryParam.categoryId==null||"".equals(categoryLinkQueryParam.categoryId.trim())){
			throw new IllegalArgumentException("'getCategoryId' can not be null!");
		}
		List<Object> list= commandExecutor.execute(new Command<List<Object>>() {
			@Override
			public List<Object> execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectList("selectBpmCategoriesByUser", categoryLinkQueryParam);
			}
		});
		if(list==null||list.size()==0)return false;
		return true;
	}
	@Override
	public List<? extends CategoryLink> getList(final CategoryLinkQueryParam categoryLinkQueryParam){
		final RowBounds rowBounds=new RowBounds(categoryLinkQueryParam.getFirstResult(), categoryLinkQueryParam.getMaxResults());
		
		return commandExecutor.execute(new Command<List<? extends CategoryLink>>() {
			@Override
			public List<? extends CategoryLink> execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectList("selectBpmCategoryLinks", categoryLinkQueryParam, rowBounds);
			}
		});
		
	}
	@Override
	 public List<? extends Category> getList(final CategoryQueryParam categoryQueryParam){
		final RowBounds rowBounds=new RowBounds(categoryQueryParam.getFirstResult(), categoryQueryParam.getMaxResults());
		
		return commandExecutor.execute(new Command<List<? extends Category>>() {
			@Override
			public List<? extends Category> execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectList("selectBpmCategories", categoryQueryParam, rowBounds);
			}
		});
		
	}
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
//	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
//		this.sqlSessionFactory = sqlSessionFactory;
//	}
	public CommandExecutor getCommandExecutor() {
		return commandExecutor;
	}
	public void setCommandExecutor(CommandExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}
}
