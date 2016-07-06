package com.yonyou.bpm.deploy;

import java.util.List;

import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.apache.ibatis.annotations.Select;

public interface MD5ResourceMapper {

	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DEPLOYMENT_ID = "deploymentId";
	public static final String BYTES = "bytes";
	public static final String REVISION = "revision";

	public static final String MD5_PREFIX = "[MD5]";
	public static final String MD5_SUFFIX = ".md5";

	@Select({
			"select ID_ as " + ID + ", NAME_ as " + NAME 
					+ ", BYTES_ as "+ BYTES 
					+ ", DEPLOYMENT_ID_ as " + DEPLOYMENT_ID
					+ ", REV_ as " + REVISION
					+ " from ACT_GE_BYTEARRAY ",
			" where NAME_ like " + ("'" + MD5_PREFIX + "%'") + "" })
	public List<ByteArrayEntity> selectProcDefinitionMD5List();
}
