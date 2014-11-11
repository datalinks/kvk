package com.kvk.dp.errorhandlingtool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kvk.dp.errorhandlingtool.DpErrorHandlingVariables.Environment;
import com.kvk.dp.errorhandlingtool.util.DpErrorHandlingUtil;

public class TestProperties {

	
	@Test
	public void testPropertyFile(){
		String expectedDbUser = "dbxcvu";
		DpErrorHandlingTool dpc = new DpErrorHandlingTool(Environment.SBX);
		String actualDbUser	  = DpErrorHandlingUtil.getProperty("db_user");
		assertEquals("Expected user: "+expectedDbUser+" not found, actual user was: "+actualDbUser,expectedDbUser, actualDbUser);

		String expectedDbUrl = "jdbc:db2://RS94ASOF.k94.kvk.nl:60504/dbxcvu";
		String actualDbUrl	  = DpErrorHandlingUtil.getProperty("db_url");
		assertEquals("Expected db url: "+expectedDbUrl+" not found, actual db_url was: "+actualDbUrl,expectedDbUrl, actualDbUrl);

		
	}
}
