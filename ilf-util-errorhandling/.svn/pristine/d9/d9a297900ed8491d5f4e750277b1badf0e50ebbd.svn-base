package com.kvk.dp.errorhandlingtool.util;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;

import com.kvk.dp.errorhandlingtool.DpErrorHandlingTool;
import com.kvk.dp.errorhandlingtool.DpErrorHandlingVariables;

public class DpErrorHandlingUtil implements DpErrorHandlingVariables{
	
	static Logger log = Logger.getLogger(DpErrorHandlingUtil.class.getName());



	
	public static String getProperty(String propName){
		String property = DpErrorHandlingTool.getProp().getProperty(propName);
		return StringUtils.newStringUtf8(Base64.decodeBase64(property)).replaceAll("\n", "");
	}
	
}
