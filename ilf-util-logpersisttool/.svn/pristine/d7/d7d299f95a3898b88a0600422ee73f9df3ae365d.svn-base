package com.kvk.dp.logpersisttool;

public interface DpLogPersistVariables {

	public static int maxBatchSize	= 250;
	
    public static String db_user 	= DpLogPersisterUtil.getProperty("db_user");
    public static String db_pwd 	= DpLogPersisterUtil.getProperty("db_pwd");
    public static String db_url 	= DpLogPersisterUtil.getProperty("db_url");

    
    public static String mq_queuename 				= DpLogPersisterUtil.getProperty("mq_queuename");
    public static String mq_errorqueuename 			= DpLogPersisterUtil.getProperty("mq_errorqueuename");
    public static String mq_errorRetryQueuename 	= DpLogPersisterUtil.getProperty("mq_errorRetryQueuename");
    public static String mq_hostname 				= DpLogPersisterUtil.getProperty("mq_hostname");
    public static String mq_channel 				= DpLogPersisterUtil.getProperty("mq_channel");
    public static String mq_user 					= DpLogPersisterUtil.getProperty("mq_user");
    public static int mq_port 						= new Integer(DpLogPersisterUtil.getProperty("mq_port")).intValue();
    public static String mq_manager 				= DpLogPersisterUtil.getProperty("mq_manager");
    
    public static int max_retry_times				= 3;

    public enum QueueType { NORMAL, RETRY, ERROR };
    public enum Environment { SBX, DEV, TST, ACP, EDU, PRD };
    
    /*
     * mq_queuename=CHRIS.MI.TEST.LOG01
     * mq_errorqueuename=CHRIS.MI.TEST.LOG02
     * mq_hostname=rs94aso7.k94.kvk.nl
     * mq_channel=DATAPOWER.SVRCONN
     * mq_user=dbxcvu
     * mq_port=10425
     * mq_manager=MQMMI06
     * 
     */

    
}
