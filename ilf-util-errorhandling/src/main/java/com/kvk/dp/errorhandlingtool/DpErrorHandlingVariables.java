package com.kvk.dp.errorhandlingtool;

import com.kvk.dp.errorhandlingtool.util.DpErrorHandlingUtil;

public interface DpErrorHandlingVariables {

	// miliz ==> 60.000 is 1 minute
	public static long SLEEP_TIME 	= 60000;

    public static String db_user 	= DpErrorHandlingUtil.getProperty("db_user");
    public static String db_pwd 	= DpErrorHandlingUtil.getProperty("db_pwd");
    public static String db_url 	= DpErrorHandlingUtil.getProperty("db_url");
    
    public static String mq_queuename 				= DpErrorHandlingUtil.getProperty("mq_queuename");
    public static String mq_errorqueuename 			= DpErrorHandlingUtil.getProperty("mq_errorqueuename");
    public static String mq_errorRetryQueuename 	= DpErrorHandlingUtil.getProperty("mq_errorRetryQueuename");
    public static String mq_hostname 				= DpErrorHandlingUtil.getProperty("mq_hostname");
    public static String mq_channel 				= DpErrorHandlingUtil.getProperty("mq_channel");
    public static String mq_user 					= DpErrorHandlingUtil.getProperty("mq_user");
    public static int mq_port 						= new Integer(DpErrorHandlingUtil.getProperty("mq_port")).intValue();
    public static String mq_manager 				= DpErrorHandlingUtil.getProperty("mq_manager");
    
    public static int max_retry_times				= 3;
    public static int retry_time					= 3;
    public static int errorhandling_cleanup_hours	= 12;
    public enum QueueType { NORMAL, RETRY, ERROR };
    public enum Environment { SBX, DEV, TST, ACP, EDU, PRD };

    
}
