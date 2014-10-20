package com.kvk.dp.logpersisttool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kvk.dp.logpersisttool.DpLogPersistVariables.Environment;

public class TestProperties {

	
	@Test
	public void testPropertyFile(){
		String expectedDbUser = "dbxcvu";
		DpLogPersisterTool dpLp = new DpLogPersisterTool(Environment.SBX);
		String actualDbUser	  = DpLogPersisterUtil.getProperty("db_user");
		assertEquals("Expected user: "+expectedDbUser+" not found, actual user was: "+actualDbUser,expectedDbUser, actualDbUser);

		String expectedDbUrl = "jdbc:db2://RS94ASOF.k94.kvk.nl:60504/dbxcvu";
		String actualDbUrl	  = DpLogPersisterUtil.getProperty("db_url");
		assertEquals("Expected db url: "+expectedDbUrl+" not found, actual db_url was: "+actualDbUrl,expectedDbUrl, actualDbUrl);

		
	}
}
