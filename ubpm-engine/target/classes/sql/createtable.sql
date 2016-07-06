--租户
create table BPM_ORG_TENANT(
	id_ varchar2(40) primary key,
	code_ varchar2(40) not null unique,
	name_ varchar2(100) not null,
	msg_ varchar2(500),
	create_time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	creator_ varchar2(50) default 'system',
	modified_Time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	modifier_ varchar2(50) default 'system'
);

--部门
create table BPM_ORG_DEPT(
	tenant_id_ varchar2(40) not null,
	id_ varchar2(40) primary key,
	code_ varchar2(40) not null ,
	father_id_ varchar2(40) not null,
	name_ varchar2(100) not null,
	depth_ integer not null,
	msg_ varchar2(500),
	create_time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	creator_ varchar2(50) default 'system',
	modified_Time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	modifier_ varchar2(50) default 'system'
);

--用户
create table BPM_ORG_USER(
	tenant_id_ varchar2(40) not null,
	id_ varchar2(40) primary key,
	code_ varchar2(40) not null ,
	name_ varchar2(100) not null,
	dept_id_ varchar2(40) not null,
	msg_ varchar2(500),
	create_time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	creator_ varchar2(50) default 'system',
	modified_Time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	modifier_ varchar2(50) default 'system'
);

--分组
create table BPM_ORG_GROUP(
	tenant_id_ varchar2(40) not null,
	id_ varchar2(40) primary key,
	code_ varchar2(40) not null ,
	name_ varchar2(100) not null,
	msg_ varchar2(500),
	create_time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	creator_ varchar2(50) default 'system',
	modified_Time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	modifier_ varchar2(50) default 'system'
);

--角色
create table BPM_ORG_ROLE(
	tenant_id_ varchar2(40) not null,
	id_ varchar2(40) primary key,
	code_ varchar2(40) not null ,
	name_ varchar2(100) not null,
	msg_ varchar2(500),
	create_time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	creator_ varchar2(50) default 'system',
	modified_Time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
	modifier_ varchar2(50) default 'system'
);

--用户角色
create table BPM_ORG_USER_ROLE(
	tenant_id_ varchar2(40) not null,
	id_ varchar2(40) primary key,
	user_id_ varchar2(40) not null,
	role_id_ varchar2(40) not null
);
--用户分组
create table BPM_ORG_USER_GROUP(
	tenant_id_ varchar2(40) not null,
	id_ varchar2(40) primary key,
	user_id_ varchar2(40) not null,
	group_id_ varchar2(40) not null
);

--维度定义
--create table BPM_ORG_DIMENSION(
--	tenant_id_ varchar2(40) not null,
--	id_ varchar2(40) primary key,
--	code_ varchar2(40) not null ,
--	name_ varchar2(100) not null,
--	msg_ varchar2(500),
--	create_time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
--	creator_ varchar2(50) default 'system',
--	modified_Time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
--	modifier_ varchar2(50) default 'system'
--);


--维度数据
--create table BPM_ORG_DIMENSION_DATA(
--	tenant_id_ varchar2(40) not null,
--	id_ varchar2(40) primary key,
--	code_ varchar2(40) not null ,
--	name_ varchar2(100) not null,
--	dim_code_ varchar2(40) not null,
--	seq_ integer not null,	
--	msg_ varchar2(500),
--	create_time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
--	creator_ varchar2(50) default 'system',
--	modified_Time_ char(19) DEFAULT 'to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')',
--	modifier_ varchar2(50) default 'system'
--);
