package com.abcplusd.sample.util;

public class Constants {
	public static final String SQL_CREATE_TABLE = "create table test.demo(id integer, name character varying(20))";
	public static final String PING_QUERY="select 'OK' as db";
	public static final String INSERT_QUERY = "insert into test.demo(id, name) values(?, ?)";
	public static final String FETCH_QUERY = "select id,name from test.demo order by id";
}
