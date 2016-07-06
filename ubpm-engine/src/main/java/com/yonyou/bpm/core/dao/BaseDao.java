package com.yonyou.bpm.core.dao;

import java.io.IOException;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class BaseDao {
	private static Logger logger = LoggerFactory.getLogger(BaseDao.class);
	private SqlSessionFactory sqlSessionFactory;
	public BaseDao(){}
	
	public BaseDao(SqlSessionFactory sqlSessionFactory ){
		this.sqlSessionFactory=sqlSessionFactory;
	}
	public BaseDao addMapper(Class<?> type){
		Configuration configuration=sqlSessionFactory.getConfiguration();
		configuration.addMapper(type);
		return this;
	}
	public BaseDao addClassResource(String resource){
		Configuration configuration=sqlSessionFactory.getConfiguration();
		ClassPathResource classPathResource=new ClassPathResource(resource);
		classPathResource.exists();
        XMLMapperBuilder mapperParser=null;
		try {
			mapperParser = new XMLMapperBuilder(classPathResource.getInputStream(), configuration, classPathResource.toString(), configuration.getSqlFragments());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
        mapperParser.parse();
        return this;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

}
