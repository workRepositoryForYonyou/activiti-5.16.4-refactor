package com.yonyou.bpm.deploy;

import java.util.List;

import org.activiti.engine.impl.cmd.CustomSqlExecution;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;

public class ProcDefMD5QueryExecution implements CustomSqlExecution<MD5ResourceMapper, List<ByteArrayEntity>>{

	@Override
	public Class<MD5ResourceMapper> getMapperClass() {
		return MD5ResourceMapper.class;
	}

	@Override
	public List<ByteArrayEntity> execute(MD5ResourceMapper mapper) {
		List<ByteArrayEntity> result = mapper.selectProcDefinitionMD5List();
		return result;
	}

}
