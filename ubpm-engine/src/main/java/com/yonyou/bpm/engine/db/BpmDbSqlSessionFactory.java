package com.yonyou.bpm.engine.db;

import org.activiti.engine.impl.db.DbSqlSessionFactory;

public class BpmDbSqlSessionFactory extends DbSqlSessionFactory{
	
	static{
	     
		 addDatabaseSpecificStatement("mysql", "bpmSelectProcessDefinitionsByQueryCriteria", "bpmSelectProcessDefinitionsByQueryCriteria_mysql");
		 addDatabaseSpecificStatement("mysql", "bpmSelectProcessDefinitionCountByQueryCriteria", "bpmSelectProcessDefinitionCountByQueryCriteria_mysql");
		 addDatabaseSpecificStatement("mysql", "bpmSelectDeploymentsByQueryCriteria", "bpmSelectDeploymentsByQueryCriteria_mysql");
		 addDatabaseSpecificStatement("mysql", "bpmSelectDeploymentCountByQueryCriteria", "bpmSelectDeploymentCountByQueryCriteria_mysql");
		 addDatabaseSpecificStatement("mysql", "bpmSelectModelCountByQueryCriteria", "bpmSelectModelCountByQueryCriteria_mysql");
		
		 addDatabaseSpecificStatement("oracle", "selectExclusiveJobsToExecute", "selectExclusiveJobsToExecute_integerBoolean");
		 
		 addDatabaseSpecificStatement("db2", "bpmSelectTaskWithVariablesByQueryCriteria", "bpmSelectTaskWithVariablesByQueryCriteria_mssql_or_db2");
		 addDatabaseSpecificStatement("db2", "bpmSelectProcessInstanceWithVariablesByQueryCriteria", "bpmSelectProcessInstanceWithVariablesByQueryCriteria_mssql_or_db2");
		 addDatabaseSpecificStatement("db2", "bpmSelectHistoricProcessInstancesWithVariablesByQueryCriteria", "bpmSelectHistoricProcessInstancesWithVariablesByQueryCriteria_mssql_or_db2");
		 addDatabaseSpecificStatement("db2", "bpmSelectHistoricTaskInstancesWithVariablesByQueryCriteria", "bpmSelectHistoricTaskInstancesWithVariablesByQueryCriteria_mssql_or_db2");
		 
		 addDatabaseSpecificStatement("mssql", "bpmSelectTaskWithVariablesByQueryCriteria", "bpmSelectTaskWithVariablesByQueryCriteria_mssql_or_db2");
		 addDatabaseSpecificStatement("mssql", "bpmSelectProcessInstanceWithVariablesByQueryCriteria", "bpmSelectProcessInstanceWithVariablesByQueryCriteria_mssql_or_db2");
		 addDatabaseSpecificStatement("mssql", "bpmSelectHistoricProcessInstancesWithVariablesByQueryCriteria", "bpmSelectHistoricProcessInstancesWithVariablesByQueryCriteria_mssql_or_db2");
		 addDatabaseSpecificStatement("mssql", "bpmSelectHistoricTaskInstancesWithVariablesByQueryCriteria", "bpmSelectHistoricTaskInstancesWithVariablesByQueryCriteria_mssql_or_db2");
	}

}
