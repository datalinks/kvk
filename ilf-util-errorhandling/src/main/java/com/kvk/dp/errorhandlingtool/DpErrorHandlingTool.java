package com.kvk.dp.errorhandlingtool;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.ibm.db2.jcc.am.Connection;
import com.ibm.mq.MQException;
import com.kvk.dp.errorhandlingtool.jaxb.Header;
import com.kvk.dp.errorhandlingtool.model.ErrorMessage;
import com.kvk.dp.errorhandlingtool.model.ErrorHandlingObject;
import com.kvk.dp.errorhandlingtool.model.ErrorHandlingRetryType;
import com.kvk.dp.errorhandlingtool.util.DpMQMessageTool;
import com.kvk.dp.errorhandlingtool.util.DpMessageSender;

/*
This tool does the following
* - gets the messages from the DP.ERRORHANDLING.LQ1 queue
* - it will persist the message in the DP_ERRORMESSAGE table
* - based on the value in the errormessage (header: kvk-dp-meta:retry-policyid) it will create 1 or more records in 
* 		the DP.ERRORHANDLING.LQ01 table, for eg if kvk-dp-meta:retry-policyid = 1, in the DP_ERRORHANDLING_RETRYTYPES table it
* 		says that RETRYPOLICY #1, has 3 retries with an interval of 60 seconds, if the time of enterance is 12:00 it will
* 		enter the following data in the DP.ERRORHANDLING.LQ01 table:
* 		ERROR_HANDLING_ID 	--> a unique nr (sequence)
* 		MESSAGE_ID			--> The msgID from the message (reference to the DP_ERRORMESSAGE table)
* 		RETRY_ID			--> The retry policy ID (from the kvk-dp-meta:retry-policyid) reference to the DP_ERRORHANDLING_RETRYTYPES table
* 		RETRY_ENDPOINT		--> The url where the message should be posted (from the kvk-dp-meta:retry-endpoint)
* 		RETRY_DATE			-->	calculated date based on the example 3 records will be made with the following times
* 									TODAYS DATE-12:01
* 									TODAYS DATE-12:02
* 									TODAYS DATE-12:03
* 		STATUS				-->	Per attempt an OK or NOT OK will be entered here, this is needed for the cleanup
*/

public class DpErrorHandlingTool extends Thread implements DpErrorHandlingVariables {

	static Logger log = Logger.getLogger(DpErrorHandlingTool.class.getName());
	private int iteration_counter 	= 0;
    public String propFileName;

	private Connection conn 			= null;
	private static boolean initialized = false;

	private static DpErrorHandlingTool dpc = null;
	
	private static DpMQMessageTool dpMessageTool 						= new DpMQMessageTool();
	private List<String> retryStatusMap 								= new ArrayList<String>();
	private Map<Integer,ErrorHandlingRetryType> errorHandlingRetryTypes = new HashMap<Integer, ErrorHandlingRetryType>();
	private static Properties prop 	  	= new Properties();

	
	
	/*
	 * Initialization of database and records, please note that when adding/ changing records
	 * to the dp_errorhandling_retrytypes table, the app needs to be reinitialized/ restarted
	 */
	private void init() throws ClassNotFoundException, SQLException {

		log.info("Initializing DATABASE");
		
		if (conn != null && !conn.isClosed())
			conn.close();

		Class.forName("com.ibm.db2.jcc.DB2Driver");
		conn = (Connection) DriverManager.getConnection(db_url, db_user, db_pwd);
		
		//	Initializing errorhandling_retrytypes
		String query = "select retrytype_id, retry_count, retry_interval from dp_errorhandling_retrytypes";
		PreparedStatement selectStmt = conn.prepareStatement(query);
		ResultSet rss = selectStmt.executeQuery();
		while(rss.next()){
			ErrorHandlingRetryType ehrt = new ErrorHandlingRetryType();
			ehrt.setRetrytype_id(rss.getInt("RETRYTYPE_ID"));
			ehrt.setRetry_countr(rss.getInt("RETRY_COUNT"));
			ehrt.setRetry_interval(rss.getInt("RETRY_INTERVAL"));
			errorHandlingRetryTypes.put(new Integer(rss.getInt("RETRYTYPE_ID")), ehrt);
		}
		log.debug("dataset dp_errorhandling_retrytypes initialized...");
		initialized = true;
	}

	public static Properties getProp() {
		return prop;
	}

	public static void setProp(Properties prop) {
		DpErrorHandlingTool.prop = prop;
	}

	
	public DpErrorHandlingTool(Environment env) {

		switch(env){
			case SBX : 	propFileName = "/opt/kvk/errorhandlingtool/properties/sbx-connection.properties";
						break;
			case DEV : 	propFileName = "/opt/kvk/errorhandlingtool/properties/dev-connection.properties";
						break;
			case TST : 	propFileName = "/opt/kvk/errorhandlingtool/properties/tst-connection.properties";
						break;
			case ACP : 	propFileName = "/opt/kvk/errorhandlingtool/properties/acp-connection.properties";
						break;
			case EDU : 	propFileName = "/opt/kvk/errorhandlingtool/properties/edu-connection.properties";
						break;
			case PRD : 	propFileName = "/opt/kvk/errorhandlingtool/properties/prd-connection.properties";
						break;
		}

		try {
			InputStream 	input = new FileInputStream(propFileName);
			prop.load(input);
		} catch (IOException e) {
			log.error("IOException loading propfile: "+propFileName);
		}

	}

	

	/*
	 *	Checks if DB connection is still ok, if not...init() is called, which is responsible
	 *	for DB initialization 
	 */
	private void checkInit() throws SQLException{
		if (!initialized || conn == null || (conn != null && conn.isClosed())) {
			try {
				init();
			} catch (Exception ex) {
				throw new SQLException("Exception during database initialization: "+ ex.toString());
			}
		}
	}

	
	/*
	 * Makes from String JAXB Message, which is mapped to the HEAD object
	 * Elements from the JAXB are queried in order to create the dpMessage object
	 */
	private ErrorMessage createDpErrorMessage(String message) throws JAXBException,UnsupportedEncodingException, XMLStreamException {
		ErrorMessage dpMessage = new ErrorMessage();
		XMLInputFactory xif = XMLInputFactory.newFactory();
		InputStream xmlInputStream = new ByteArrayInputStream(message.getBytes("UTF-8"));
		XMLStreamReader xsr = xif.createXMLStreamReader(xmlInputStream);
		xsr.nextTag(); // Advance to Envelope tag
		xsr.nextTag(); // Advance to Header tag

		JAXBContext jc = JAXBContext.newInstance(Header.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		JAXBElement<Header> je = unmarshaller.unmarshal(xsr, Header.class);

		log.debug("unmarshalled with header ");
		int retryPolicyId = je.getValue().getRetryPolicyid();
		log.debug("retryPolicy ID "+retryPolicyId);
		String messageId = je.getValue().getMessageID() == null ? "NO_ID" : je.getValue().getMessageID().trim();
		log.debug("message ID "+messageId);
		String retryEndpoint = je.getValue().getRetryEndpoint() == null ? "NO_ENDPOINT" : je.getValue().getRetryEndpoint().trim(); 
		log.debug("retryEndpoint "+retryEndpoint);
		String retryTo = je.getValue().getRetryTo() == null ? "?" : je.getValue().getRetryTo().trim(); 
		log.debug("retryTo "+retryTo);
		String retryAction = je.getValue().getRetryAction() == null ? "?" : je.getValue().getRetryAction().trim(); 
		log.debug("retryAction "+retryAction);
		
		dpMessage.setRetryPolicyId(retryPolicyId);
		dpMessage.setRetryEndpoint(retryEndpoint);
		dpMessage.setXmlMessageId(messageId);
		dpMessage.setXmlMessage(message);
		dpMessage.setRetryTo(retryTo);
		dpMessage.setRetryAction(retryAction);

		return dpMessage;
	}

	/*
	 * This method is called when a message is sent succesfully or if the 
	 * last retry_date is older than x hours ago (x --> errorhandling_cleanup_hours)
	 */
	private void removeErrorMessageFromDatabase(ErrorMessage dpMessage) throws ClassNotFoundException, SQLException {

		checkInit();
		
		String query = "delete from dp_errorhandling where message_id=?";
		log.debug("query: "+query+" msgId: "+dpMessage.getXmlMessageId());
		PreparedStatement deleteStmt = conn.prepareStatement(query);
		deleteStmt.setString(1, dpMessage.getXmlMessageId());
		deleteStmt.executeUpdate();
		
		query = "delete from dp_errormessage where message_id=?";
		log.debug("query: "+query+" msgId: "+dpMessage.getXmlMessageId());
		deleteStmt = conn.prepareStatement(query);
		deleteStmt.setString(1, dpMessage.getXmlMessageId());
		deleteStmt.executeUpdate();
	}


	/*
	 * When a message is retried before the aflever service, the to-action combination
	 * should be put back into the original call (the call for the functional-aanlever)
	 * This original to-action is from the Header/kvk-dp-meta:RetryTo and Header/kvk-dp-meta:RetryAction
	 */
	private String changeRetryToRetryActionCombinationIntoOriginal(ErrorMessage dpMessage){
		
		String originalTo = dpMessage.getRetryTo();
		String originalAction = dpMessage.getRetryAction();
		String xmlMessage 	= dpMessage.getXmlMessage();
		int startToIndex 	= xmlMessage.indexOf("wsa:To")-1;
		int endToIndex		= xmlMessage.indexOf("/wsa:To")+8;
		
		String toSubstring  = xmlMessage.substring(startToIndex,endToIndex);
		xmlMessage			= xmlMessage.replace(toSubstring, "<wsa:To xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"+originalTo+"</wsa:To>");

		int startActionIndex 	= xmlMessage.indexOf("wsa:Action")-1;
		int endActionIndex		= xmlMessage.indexOf("/wsa:Action")+12;

		String actionSubstring  = xmlMessage.substring(startActionIndex,endActionIndex);
		xmlMessage				= xmlMessage.replace(actionSubstring, "<wsa:Action xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"+originalAction+"</wsa:Action>");
		
		return xmlMessage;
		
	}
	
	
	// TODO: Make user of Connection pool mechanism (for e.g. C3PO)
	/*
	 * Persisting messages in the dp_errormessage and dp_errorhandling tables,
	 * only if the messageId does not already exists in the dp_errorhandling_history table
	 */
	private void persistMessageInDatabase(ErrorMessage dpMessage) throws ClassNotFoundException, SQLException {

		checkInit();

		String messageId = dpMessage.getXmlMessageId();
		//	Check if message already exists in DB, if so --> return
		String query = "select message_id from dp_errorhandling_history where message_id=?";
		PreparedStatement selectStmt = conn.prepareStatement(query);
		selectStmt.setString(1, messageId);
		ResultSet rss = selectStmt.executeQuery();
		if(rss.next()){
			log.debug("message with id: "+messageId+" already found in history, IGNORING message...");
			return;
		}
		query = "select message_id from dp_errormessage where message_id=?";
		selectStmt = conn.prepareStatement(query);
		selectStmt.setString(1, messageId);
		rss = selectStmt.executeQuery();
		if(rss.next()){
			log.debug("message with id: "+messageId+" already found in dp_errormessage, IGNORING message...");
			return;
		}

		
		
		//	Inserting message into DP_ERRORMESSAGE, change to/ action combination in original
		query = "insert into dp_errormessage (message_id,message,message_date) values (?,?,current timestamp)";

		if (log.isDebugEnabled()) {
			log.debug("persisting message in db with following query : ");
			log.debug(query);
			log.debug(" arg 1: " + messageId);
		}
		
		// prepare the statement
		PreparedStatement insertStmt = conn.prepareStatement(query);
		
		String xmlMessage = changeRetryToRetryActionCombinationIntoOriginal(dpMessage); 
		insertStmt.setString(1, messageId);
		insertStmt.setString(2, xmlMessage);

		// execute the statement
		if (insertStmt.executeUpdate() != 1) {
			log.error("No record inserted with query: "+query);
		}
		
		ErrorHandlingRetryType ehrt = errorHandlingRetryTypes.get(new Integer(dpMessage.getRetryPolicyId()));
		
		log.debug("current retry ID: "+dpMessage.getRetryPolicyId());
		log.debug("Resolved ErrorHandlingRetryType: ID: "+ehrt.getRetrytype_id()+" counter: "+ehrt.getRetry_countr()+" interval: "+ehrt.getRetry_interval());

		Calendar cal = GregorianCalendar.getInstance();
		
		for(int i=0; i<ehrt.getRetry_countr(); i++){
			int secondsToAdd = (i+1)*(ehrt.getRetry_interval());
			
			log.debug("inserting record, count = "+(i+1)+" seconds to add: "+secondsToAdd+" retrycounter = "+ehrt.getRetry_countr());
			query = "insert into dp_errorhandling (message_id,retrytype_id,retry_endpoint,entry_date,retry_to,retry_action,retry_date) values (?,?,?,current timestamp,?,?,current timestamp +"+secondsToAdd+" seconds)";
			
			cal.add(Calendar.SECOND, secondsToAdd);
			
			PreparedStatement insertStmt2 = conn.prepareStatement(query);
			insertStmt2.setString(1, messageId);
			insertStmt2.setInt(2, dpMessage.getRetryPolicyId());
			insertStmt2.setString(3, dpMessage.getRetryEndpoint());
			insertStmt2.setString(4, dpMessage.getRetryTo());
			insertStmt2.setString(5, dpMessage.getRetryAction());
			
			if (insertStmt2.executeUpdate() != 1) {
				log.error("No record inserted with query: "+query);
			}

		}
	}

	public static void main(String[] args) {
		if(args.length != 1){
			System.err.println("Please give environment as parameter, possible values are: ");
			for(Environment env : Environment.values()){
				System.err.println("- "+env);
			}
			System.exit(1);
		}
		dpc = new DpErrorHandlingTool(Environment.valueOf(args[0].toString()));
		dpc.run();
	}

	public void run() {
		try{
			while (true) {
				//	Retry time is ever 3 times...(see variable file)
				if(iteration_counter>retry_time){
					dpc.iterateOverMessages(QueueType.RETRY);
					iteration_counter = 0;
					sleep(SLEEP_TIME);
				}else{
					try {
						//	Getting messages from queue and putting them in database
						dpc.iterateOverMessages(QueueType.NORMAL);
						dpc.processErrorHandlingTable();
						dpc.cleanupErrorHandlingTable();
						sleep(SLEEP_TIME);
					}catch(SQLException e){
						log.error(DpErrorHandlingTool.class.getName()	+ " SQL exception occured, wrong db init???....");
						try {
							init();
						} catch (ClassNotFoundException e1) {
							log.error(DpErrorHandlingTool.class.getName()	+ " SQL exception occured, crash, init()...."+e1.getMessage());
						} catch (SQLException e1) {
							log.error(DpErrorHandlingTool.class.getName()	+ " SQL exception occured, crash, init()...."+e1.getMessage());
						}
					}catch(Exception e){
						log.error(DpErrorHandlingTool.class.getName()	+ " General exception occured, application crash....");
						e.printStackTrace();
					}
				}
				iteration_counter++;
			}
		}catch (InterruptedException e) {
			log.info(DpErrorHandlingTool.class.getName()	+ " application interupted....");
		} finally {
			dpc.closeConnection();
			dpMessageTool.closeConnection();
		}
	}
	
	private List<ErrorMessage> convertXmlMessagesToDpErrorMessageObjects(List<String> xmlMessages) throws UnsupportedEncodingException, JAXBException, XMLStreamException{
		List<ErrorMessage> result = new ArrayList<ErrorMessage>();
		for(String xmlMessage : xmlMessages){
			ErrorMessage dpMessage = createDpErrorMessage(xmlMessage);
			result.add(dpMessage);
		}
		
		return result;
	}
	
	/*	Cleanup of dp_errorhandling and dp_errormessage
		Cleanup if RETRY_DATE<CURRENT_DATE  ...met 1 dag verschil
		If ALL is NOK ...then ...to ERROR QUEUE
	*/
	protected void cleanupErrorHandlingTable() throws SQLException, MQException, IOException, JAXBException, XMLStreamException{
		checkInit();
		
		log.debug("cleanup...");
		
		//	Determine wether to put messages in the error queue
		String query = 	"select	deh.MESSAGE_ID,dpe.message,deh.STATUS,dpr.RETRY_COUNT from dp_errorhandling deh,"+ 
						"DP_ERRORHANDLING_RETRYTYPES dpr,	DP_ERRORMESSAGE dpe where	deh.MESSAGE_ID = dpe.MESSAGE_ID and "+
						"deh.RETRYTYPE_ID = dpr.RETRYTYPE_ID";
		
		PreparedStatement selectStmt 					= conn.prepareStatement(query);
		ResultSet rss 									= selectStmt.executeQuery();
		List<ErrorHandlingObject> errorHandlingObjects  = new ArrayList<ErrorHandlingObject>();
		List<String> listForErrorQueue 					= new ArrayList<String>();

		
		//	Initialize workload for putting messages to ERROR Queue
		while(rss.next()){
			ErrorHandlingObject eho = new ErrorHandlingObject();
			eho.setMessageId(rss.getString("message_id"));
			eho.setMessage(rss.getString("message"));
			eho.setStatus(rss.getString("status"));
			eho.setRetryCount(rss.getInt("retry_count"));
			errorHandlingObjects.add(eho);
		}
		
		
		//	Messages to ERROR QUEUE AND TO DP_ERRORHANDLING_NOK_HISTORY
		//	for e.g. If retry count = 3 and 3 NOK are found for certain messageID...this message goes into error queue
		for(ErrorHandlingObject eho : errorHandlingObjects ){
			query = "select count(message_id) as aantal from DP_ERRORHANDLING where MESSAGE_ID = ? and STATUS = 'NOK'";
			selectStmt = conn.prepareStatement(query);
			selectStmt.setString(1, eho.getMessageId());
			rss 	   = selectStmt.executeQuery();
			while(rss.next()){
				log.debug("check "+eho.getRetryCount()+" x NOK count: "+rss.getInt("aantal")+" retrycount: "+eho.getRetryCount());
				
				if (rss.getInt("aantal")==eho.getRetryCount())
					listForErrorQueue.add(eho.getMessage());
			}
		}

		if(listForErrorQueue.size()>0){
			writeToErrorQueueAndCleanup(listForErrorQueue);
			
			List<ErrorMessage> ehos = convertXmlMessagesToDpErrorMessageObjects(listForErrorQueue);
			
			List<String> handledIds = new ArrayList<String>();
			for(ErrorMessage dpMessage : ehos){
				query = "insert into dp_errorhandling_history (message_id,handle_date,status,message) values (?,current timestamp,?,?)";
				PreparedStatement insertStmt = conn.prepareStatement(query);
				String messageId = dpMessage.getXmlMessageId();
				if(!handledIds.contains(messageId)){
					insertStmt.setString(1, messageId);
					insertStmt.setString(2, "NOK");
					insertStmt.setString(3, dpMessage.getXmlMessage());
					insertStmt.executeUpdate();
					handledIds.add(messageId);
				}
			}

		}
			
		
		
		//	Delete all messages older than retry date (errorhandling_cleanup_hours) ==12 hours
		//	Delete all messages which have status OK
		query = 	"select dpe.errorhandling_id,dpe.message_id,dpe.retry_endpoint,dpe.entry_date,dpe.retry_date,dpe.status,dpem.message "+ 
					"from dp_errorhandling dpe, dp_errormessage dpem where dpe.MESSAGE_ID = dpem.MESSAGE_ID "+
					"and dpe.RETRY_DATE<current TIMESTAMP - ? hours or dpe.STATUS='OK'";
		selectStmt = conn.prepareStatement(query);
		selectStmt.setInt(1, errorhandling_cleanup_hours);
		
		rss 					= selectStmt.executeQuery();
		errorHandlingObjects 	= new ArrayList<ErrorHandlingObject>();
		List<String> handledIds = new ArrayList<String>();
		
		//	Initialize workload for deleting messages from DB
		while(rss.next()){
			ErrorHandlingObject eho = new ErrorHandlingObject();
			eho.setErrorhandling_id(rss.getInt("errorhandling_id"));
			eho.setMessageId(rss.getString("message_id"));
			errorHandlingObjects.add(eho);
		}


		for(ErrorHandlingObject eho : errorHandlingObjects){

			String messageId = eho.getMessageId();
			query = 	"delete from dp_errorhandling where message_id=?";
			selectStmt = conn.prepareStatement(query);
			selectStmt.setString(1, messageId);
			selectStmt.executeUpdate();
			log.debug("query: "+query+" message_id:"+messageId);

			//if(!handledIds.contains(eho.getMessageId())){
			//	handledIds.add(eho.getMessageId());
			query = 	"delete from dp_errormessage where message_id=?";
			log.debug("handleIds query: "+query+" msg_id:"+messageId);
			selectStmt = conn.prepareStatement(query);
			selectStmt.setString(1, messageId);
			selectStmt.executeUpdate();
			//}
		}
		

	}

	/*
	 * This determines the workload and sends the messages accordingly
	 */
	protected void processErrorHandlingTable() throws SQLException, ClientProtocolException, IOException, HttpException{
		log.debug("processErrorHandlingTable...");
		checkInit();
		
		String query = "select dpe.errorhandling_id,dpe.message_id,dpe.retry_endpoint,dpe.entry_date,dpe.retry_date,dpe.status,dpem.message "
						+ "from dp_errorhandling dpe, dp_errormessage dpem where dpe.MESSAGE_ID = dpem.MESSAGE_ID and dpe.status is null";
		PreparedStatement selectStmt 	= conn.prepareStatement(query);
		ResultSet rss 					= selectStmt.executeQuery();
		List<ErrorHandlingObject> errorHandlingObjects = new ArrayList<ErrorHandlingObject>();
		
		//	Sending message
		log.debug("excecuting query: "+query);
		List<String> handledIds = new ArrayList<String>();
		
		//	Initialize workload
		while(rss.next()){
			String status 		= rss.getString("status")==null ? "unknown" : rss.getString("status").trim(); 
			String messageId 	= rss.getString("message_id").trim();
			Date retryDate 		= rss.getDate("retry_date");
			Date now			= new Date();

			log.debug("messageId: "+messageId+" status "+status);
			
			//	Add nothing that is scheduled in the past
			if(!handledIds.contains(messageId)	&&	retryDate.before(now)	){
				
				
				int errorHandlingId = rss.getInt("errorhandling_id");
				log.debug("errorhandlingId "+errorHandlingId);
				
				ErrorHandlingObject eho = new ErrorHandlingObject();
				handledIds.add(messageId);
				
				eho.setErrorhandling_id(errorHandlingId);
				eho.setMessageId(messageId);
				eho.setRetryEndpoint(rss.getString("retry_endpoint"));
				eho.setEntryDate(rss.getDate("entry_date"));
				eho.setMessage(rss.getString("message"));
				eho.setRetryDate(retryDate);
				eho.setStatus(status);
				errorHandlingObjects.add(eho);
				
				log.debug("will handle message with id: "+messageId+" time: "+retryDate.toString());
			}
		}
		log.debug("workload initialized, amount of messages that will be send: "+errorHandlingObjects.size());
		if(errorHandlingObjects.size()>0)
			sendMessages(errorHandlingObjects);
	}
	
	/*
	 * Wrapper for sending messages to the aanlever or aflever service
	 */
	private void sendMessages(List<ErrorHandlingObject> errorHandlingObjects){
		DpMessageSender dpMessageSender = new DpMessageSender();
		//	Now per ErrorHandlingObject in the errorHandlingObjects send the message and update the status
		int succescounter = 0;
		int errorcounter = 0;
		int result = 0;
		String query = "";
		PreparedStatement updateStmt;
		PreparedStatement insertHistoryStmt;
		List<String> allMessages = new ArrayList<String>();
		try{
			for(ErrorHandlingObject eho : errorHandlingObjects){
				allMessages.add(eho.getMessage());
				query = "update dp_errorhandling set status=? where errorhandling_id=?";
				String historyQuery = "insert into dp_errorhandling_history (message_id,handle_date,status) values (?,current timestamp,?)";
				updateStmt = conn.prepareStatement(query);
				insertHistoryStmt = conn.prepareStatement(historyQuery);
				
				
				//result = dpMessageSender.callService(eho.getRetryEndpoint(), eho.getMessage());
				result = dpMessageSender.callService("10.10.45.123:6969", eho.getMessage());
				log.debug("called webservice: "+eho.getRetryEndpoint()+" result was: "+result);
				if(result==200){
					updateStmt.setString(1, "OK");
					insertHistoryStmt.setString(1, eho.getMessageId());
					insertHistoryStmt.setString(2, "OK");
					insertHistoryStmt.execute();
					succescounter++;
				}else{
					updateStmt.setString(1, "NOK");
					errorcounter++;
				}
				updateStmt.setInt(2, eho.getErrorhandling_id());
				updateStmt.executeUpdate();
			}
			if(succescounter!=0 || errorcounter !=0 ){
				log.debug("Succesfull messages sent: "+succescounter);
				log.debug("UnSuccesfull messages sent: "+errorcounter);
			}
		}catch(Exception ex){
			log.error("Exception sending message, code: "+result);
		}
	}
	
	/*
	 * Writes messages to error queue and removes the from the DB handling tables
	 */
	private void writeToErrorQueueAndCleanup(List<String> allMessages){
		try{
			List<String> handledIds = new ArrayList<String>();
			//	Removing messages from DB
			for(String message : allMessages){
				ErrorMessage dpMessage = createDpErrorMessage(message);
				String messageId = dpMessage.getXmlMessageId();
				if(!handledIds.contains(messageId)){
					removeErrorMessageFromDatabase(dpMessage);
					List<String> tmpList = new ArrayList<String>();
					tmpList.add(message);
					writeAllMessagesInQueue(tmpList,QueueType.ERROR);
					handledIds.add(messageId);
				}
			}
		} catch (MQException e) {
			log.error("MQ Exception : + writing message to ERROR Queue writeToErrorQueueAndCleanup method" );
		} catch (IOException e) {
			log.error("IO Exception : + writing message to ERROR Queue writeToErrorQueueAndCleanup method" );
		} catch (JAXBException e) {
			log.error("JAXB Exception : "+ e.getMessage() +" writeToErrorQueueAndCleanup method");
		} catch (XMLStreamException e) {
			log.error("XMLStreamException Exception : "+ e.getMessage() +" writeToErrorQueueAndCleanup method");
		} catch (ClassNotFoundException e) {
			log.error("ClassNotFoundException Exception : "+ e.getMessage()+" writeToErrorQueueAndCleanup method");
		} catch (SQLException e) {
			log.error("SQLException Exception : "+ e.getMessage()+" writeToErrorQueueAndCleanup method");
		}
	}
	
	/*
	 * Iterating over all available messages
	 */
	protected void iterateOverMessages(QueueType qType) {
		List<String> allMessagesToBeHandled = null;
			
		//	Getting messages from (M)Queue, only NORMAL and RETRY (error queue is handled manually)
		try {
			if(qType.equals(QueueType.NORMAL))
				allMessagesToBeHandled = dpMessageTool.readQueue();
			if(qType.equals(QueueType.RETRY))
				allMessagesToBeHandled = dpMessageTool.readErrorRetryQueue();
		} catch (MQException mex) {
			log.error("MQException getting messages from queue: "+ mex.getMessage()+" messagetype is: "+qType.toString());
		} catch (IOException e) {
			log.error("IO Exception : + getting message from Queue, msg: "+e.getMessage() );
		}
		log.debug("Amount of retrieved messages from queue: "+mq_queuename+" is: "+allMessagesToBeHandled.size());
		createJaxbMessageAndPersistInDatabase(allMessagesToBeHandled);
	}

	//	TODO:	improve exception handling
	/*	
	 * Iterating over the messages, creating dpMessage and persisting them in DB 
	 * */
	private void createJaxbMessageAndPersistInDatabase(List<String> allMessagesToBeHandled){
		ErrorMessage dpMessage = null;
		try {
			for(String aMessage : allMessagesToBeHandled){
				try {
					log.debug("create error message... ");
					dpMessage = createDpErrorMessage(aMessage);
					log.debug("persist error message... ");
					dpc.persistMessageInDatabase(dpMessage);
				} catch (JAXBException exception) {
					//	Will write to ERROR QUEUE eventually, cannot do anything with this message :(
					log.error("JAXBException during createErrorMessage or persistMessageInDatabase, writing message to ERROR Q");
					writeAllMessagesInQueue(allMessagesToBeHandled,QueueType.ERROR,dpMessage.getXmlMessageId());
				}catch (UnsupportedEncodingException exception){
					log.error("UnsupportedEncodingException during createErrorMessage or persistMessageInDatabase, writing message to ERROR Q");
					writeAllMessagesInQueue(allMessagesToBeHandled,QueueType.ERROR,dpMessage.getXmlMessageId());
				} catch (XMLStreamException exception) {
					log.error("XMLStreamException during createErrorMessage or persistMessageInDatabase, writing message to ERROR Q");
					writeAllMessagesInQueue(allMessagesToBeHandled,QueueType.ERROR,dpMessage.getXmlMessageId());
				}
			}
		//	Serious database problems????
		} catch (SQLException ex) {
			try{
				String messageId = dpMessage.getXmlMessageId();
				if(!retryStatusMap.contains(messageId)){
					retryStatusMap.add(messageId);
				}
				int occurrences = Collections.frequency(retryStatusMap, dpMessage.getXmlMessageId());
				log.debug(dpMessage.getXmlMessageId()+" occurs: "+occurrences+" times in retryStatusMap");
				
				if(occurrences>=max_retry_times){
					Iterator<String> iterator = retryStatusMap.iterator();
					log.error("putting message with ID: " + dpMessage.getXmlMessageId()+ " in ERROR Q, exception was: "+ ex.getMessage());
					
					writeAllMessagesInQueue(allMessagesToBeHandled,QueueType.ERROR,dpMessage.getXmlMessageId());
					//	MAP Cleanup
					while(iterator.hasNext()){
						if(iterator.next() == dpMessage.getXmlMessageId())
							retryStatusMap.remove(dpMessage.getXmlMessageId());
					}
				}else{
					log.error("putting message with ID: " + dpMessage.getXmlMessageId()+ " in error Retry Q, exception was: "+ ex.getMessage());
					writeAllMessagesInQueue(allMessagesToBeHandled,QueueType.RETRY,dpMessage.getXmlMessageId());
				}
			} catch (MQException e) {
				log.error("MQ Exception : + writing message to Queue for message with id "+dpMessage.getXmlMessageId()+" msg: "+e.getMessage() );
			} catch (IOException e) {
				log.error("IO Exception : + writing message to Queue for message with id "+dpMessage.getXmlMessageId()+" msg: "+e.getMessage() );
			} catch (JAXBException e) {
				log.error("JAXB Exception : "+ e.getMessage() );
			} catch (XMLStreamException e) {
				log.error("XMLStreamException Exception : "+ e.getMessage() );
			}
		}catch(Exception ex){
			log.error("No SQL exception wrong message? putting in ERROR Q, exception was: "+ ex.getMessage());
			try{
				writeAllMessagesInQueue(allMessagesToBeHandled,QueueType.ERROR);
			} catch (MQException e) {
				log.error("MQ Exception : + writing message to ERROR Queue for message with id "+dpMessage.getXmlMessageId()+" msg: "+e.getMessage() );
			} catch (IOException e) {
				log.error("IO Exception : + writing message to ERROR Queue for message with id "+dpMessage.getXmlMessageId()+" msg: "+e.getMessage() );
			} catch (JAXBException e) {
				log.error("JAXB Exception : "+ e.getMessage() );
			} catch (XMLStreamException e) {
				log.error("XMLStreamException Exception : "+ e.getMessage() );
			}
		}
		
	}

	/*
	 * Wrapper for writeAllMessagesInQueue(List<String> messagesToBeHandled , QueueType qtype, String msgIdWithProblem)
	 */
	private void writeAllMessagesInQueue(List<String> messagesToBeHandled , QueueType qtype) throws MQException, IOException, JAXBException, XMLStreamException{
		writeAllMessagesInQueue(messagesToBeHandled,qtype,"wrapper");
	}

	/*
	 * writes messages in queue in case this is called within an exception, the following needs to happen
	 * the correct messages will be put back in the NORMAL queue, the message with the exception will be put
	 * in the queue you provided as parameter (probably ERROR or RETRY)
	 */
	private void writeAllMessagesInQueue(List<String> messagesToBeHandled , QueueType qtype, String msgIdWithProblem) throws MQException, IOException, JAXBException, XMLStreamException{
		
		for (int i = 0; i < messagesToBeHandled.size(); i++) {
			String message = messagesToBeHandled.get(i);
			ErrorMessage dpMessage = createDpErrorMessage(message);
			
			if(!msgIdWithProblem.equalsIgnoreCase("wrapper")){
				if(dpMessage.getXmlMessageId().equalsIgnoreCase(msgIdWithProblem))
					dpMessageTool.putMessageInQueue(message, qtype);
				else
					dpMessageTool.putMessageInQueue(message, QueueType.NORMAL);
			}else{
				dpMessageTool.putMessageInQueue(message, qtype);
			}
		}
	}
	
	/*
	 * Close DB connection
	 */
	private void closeConnection() {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			log.error("Exception during closing connection to db "+ e.getMessage());
		} finally {
			try {
				conn.close();
				log.debug("Closing connection to db");
			} catch (Exception e) {
			}
		}
	}
}
