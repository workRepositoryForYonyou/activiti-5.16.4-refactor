package com.yonyou.bpm.engine.conf;

import java.sql.Driver;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;

public class BpmEngineDataSourceFactory implements FactoryBean<DataSource> {

	private org.slf4j.Logger logger = LoggerFactory.getLogger(BpmEngineDataSourceFactory.class);

	private String jndi;
	
	private String driverClass;
	private String url;
	private String userName;
	private String password;


	public String getJndi() {
		return jndi;
	}

	public void setJndi(String jndi) {
		this.jndi = jndi;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private DataSource dataSource;

	private void init() {
		logger.debug("Start data source initialization... ");
		if (jndi != null && jndi.trim().length() > 0) {
			dataSource = initJndiDataSource();
		} else {
			dataSource = initJDBCDataSource();
		}
		logger.debug("Completed data source initialization! ");
	}

	private DataSource initJndiDataSource() {
		if (jndi == null || jndi.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Not specified the jndi name of the jdbc data source!");
		}
		jndi = jndi.trim();
		JndiObjectFactoryBean jndiDSFactory = new JndiObjectFactoryBean();
		jndiDSFactory.setJndiName(jndi);
		try {
			jndiDSFactory.afterPropertiesSet();
		} catch (NamingException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return (DataSource) jndiDSFactory.getObject();
	}

	@SuppressWarnings("unchecked")
	private DataSource initJDBCDataSource() {
		// Normal data source;
		if (driverClass == null || driverClass.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Not specified the jdbc driver class!");
		}
		driverClass = driverClass.trim();

		if (url == null || url.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Not specified the url of database!");
		}
		url = url.trim();

		if (userName == null || userName.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Not specified the userName of database!");
		}
		userName = userName.trim();

		if (password == null || password.length() == 0) {
			throw new IllegalArgumentException(
					"Not specified the userName of database!");
		}

		// load driver class;
		Class<? extends Driver> driver = null;
		try {
			driver = (Class<? extends Driver>) Class.forName(driverClass);

		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		// Connection settings
		SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClass(driver);
		ds.setUrl(url);
		ds.setUsername(userName);
		ds.setPassword(password);
		return ds;
	}


	@Override
	public DataSource getObject() throws Exception {
		if (dataSource == null) {
			init();
		}
		return dataSource;
	}

	@Override
	public Class<?> getObjectType() {
		return DataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
