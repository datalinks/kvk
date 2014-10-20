package com.kvk.dp.logpersisttool;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;

public class DpLogPersisterUtil implements DpLogPersistVariables{
	
	static Logger log 			= Logger.getLogger(DpLogPersisterUtil.class.getName());
    
	

	
	public static String getProperty(String propName){
		String property = DpLogPersisterTool.getProp().getProperty(propName);
		return StringUtils.newStringUtf8(Base64.decodeBase64(property)).replaceAll("\n", "");
	}
	
}
