package com.kvk.dp.logpersisttool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.*;

public class DpMQMessageTool{

	static Logger log = Logger.getLogger(DpMQMessageTool.class.getName());
	
	private static MQQueueManager qMgr;
	private static MQQueue mqQueue 				= null;
	private static MQQueue mqErrorQueue 		= null;
	private static String MQ_QUEUENAME 			= "CHRIS.MI.TEST.LOG01"; 
	private static String MQ_ERROR_QUEUENAME 	= "CHRIS.MI.TEST.LOG02";
	private static String MQ_HOSTNAME 			= "rs94aso7.k94.kvk.nl";
	private static String MQ_CHANNEL			= "DATAPOWER.SVRCONN";
	private static String MQ_USERID				= "dbxcvu";
	private static int MQ_PORT					= 10425;
	private String qManager 					= "MQMMI06";
	private int maxBatchSize					= 10;
	public static boolean initialized 			= false;
	private static MQPutMessageOptions pmo;
	
    
	

	private void init() throws MQException, IOException{

		if(qMgr!=null && qMgr.isOpen())
			closeConnection();
			
		MQEnvironment.hostname 	= MQ_HOSTNAME;
		MQEnvironment.channel  	= MQ_CHANNEL;
		MQEnvironment.userID 	= MQ_USERID; 
		MQEnvironment.port 		= MQ_PORT;
		qMgr = new MQQueueManager(qManager);
		pmo = new MQPutMessageOptions();
		
		log.info("initializing DpMQMessageTool");
		int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_OUTPUT | CMQC.MQOO_INQUIRE; 
		
		mqQueue = qMgr.accessQueue(MQ_QUEUENAME, openOptions);
		mqErrorQueue = qMgr.accessQueue(MQ_ERROR_QUEUENAME, openOptions);
		initialized = true;
  }

		   
		
	
	public void putMessageInQueue(String message, boolean isError) throws MQException, IOException{
		   if(!initialized || !mqQueue.isOpen() || !mqErrorQueue.isOpen()){
			   init();
		   }
		   MQMessage mqmessage = new MQMessage();
		   mqmessage.writeUTF(message);
		   if(isError)
			   mqErrorQueue.put(mqmessage,pmo);
		   else
			   mqQueue.put(mqmessage,pmo);
	}

	
  public List<String> readQueue() throws MQException, IOException{
	   if(!initialized){
		   init();
	   }
	   int counter = 0;
	   List<String> result = new ArrayList<String>();
	   while(mqQueue.getCurrentDepth()>0 && counter<maxBatchSize){
		   result.add(getMessage(mqQueue));
		   counter++;
	   }
	   return result;
  }

  public void closeConnection(){
		try{
			mqQueue.close();
			mqErrorQueue.close();
			qMgr.disconnect();
		}catch(Exception ex){
			log.error("Exception during close and/or disconnect queue, message is: "+ex.getMessage());
		}
  }
  
  
	
   private String getMessage(MQQueue mqQueue) throws IOException, MQException{
		MQMessage retrievedMessage = new MQMessage();
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		mqQueue.get(retrievedMessage, gmo);
		byte[] b = new byte[retrievedMessage.getMessageLength()];
		retrievedMessage.readFully(b);
		return new String(b);
   }
}
