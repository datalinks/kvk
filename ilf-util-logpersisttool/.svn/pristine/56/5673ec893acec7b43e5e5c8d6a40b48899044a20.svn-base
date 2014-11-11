package com.kvk.dp.logpersisttool;

import com.ibm.db2.jcc.am.Connection;
import com.ibm.mq.MQException;
import com.kvk.dp.logpersisttool.jaxb.Header;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.exceptions.XpathException;

//	TODO	improve error handling
/*	When there is a connection error to the database...the message will go in the message_retry queue
 * 	This queue will be emptied after 30 iterations or so....a counter per message will be kept in memory
 *  if the message with ID in the memory map has been retried for 3 times or some...it will be put in the
 *  error_queue...this is the queue that will be monitored
 */

public class DpLogPersisterTool extends Thread implements DpLogPersistVariables {

    
    
    public String propFileName;
	static Logger log = Logger.getLogger(DpLogPersisterTool.class.getName());

	// miliz ==> 60.000 is 1 minute
	private static final long SLEEP_TIME 		= 60000;

	private int iteration_counter 		= 0;
        private int iteration_counter_cleanup   = 0;
	private static Properties prop 	  	= new Properties();

	private Connection conn = null;
	private static boolean initialized = false;


	private static DpLogPersisterTool dpc = null;
	private static final DpMQMessageTool dpMessageTool = new DpMQMessageTool();
	private List<String> retryStatusMap = new ArrayList<String>();
	
	public DpLogPersisterTool(Environment env) {

		switch(env){
			case SBX : 	propFileName = "/opt/kvk/logpersisttool/properties/sbx-connection.properties";
						break;
			case DEV : 	propFileName = "/opt/kvk/logpersisttool/properties/dev-connection.properties";
						break;
			case TST : 	propFileName = "/opt/kvk/logpersisttool/properties/tst-connection.properties";
						break;
			case ACP : 	propFileName = "/opt/kvk/logpersisttool/properties/acp-connection.properties";
						break;
			case EDU : 	propFileName = "/opt/kvk/logpersisttool/properties/edu-connection.properties";
						break;
			case PRD : 	propFileName = "/opt/kvk/logpersisttool/properties/prd-connection.properties";
						break;
		}

		try {
			InputStream 	input = new FileInputStream(propFileName);
			prop.load(input);
		} catch (IOException e) {
			log.error("IOException loading propfile: "+propFileName);
		}

	}
	
	public static Properties getProp() {
		return prop;
	}

	public static void setProp(Properties prop) {
		DpLogPersisterTool.prop = prop;
	}
	
	private void init() throws ClassNotFoundException, SQLException {

		log.info("Initializing DATABASE");
		
		
		if (conn != null && !conn.isClosed())
			conn.close();

		Class.forName("com.ibm.db2.jcc.DB2Driver");
		conn = (Connection) DriverManager.getConnection(db_url, db_user, db_pwd);
		initialized = true;
	}

	/*
	 * Please note if there are 2 elements of msgId that only the last created element will be picked up...
	 * this should be correct as we are adding the messageID ourselves
	 */
	private DpMessage createDpMessage(String message) throws JAXBException,
			UnsupportedEncodingException, XMLStreamException {
		DpMessage dpMessage = new DpMessage();
		XMLInputFactory xif = XMLInputFactory.newFactory();
		InputStream xmlInputStream = new ByteArrayInputStream(message.getBytes("UTF-8"));
		XMLStreamReader xsr = xif.createXMLStreamReader(xmlInputStream);
		xsr.nextTag(); // Advance to Envelope tag
		xsr.nextTag(); // Advance to Header tag

		JAXBContext jc = JAXBContext.newInstance(Header.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		JAXBElement<Header> je = unmarshaller.unmarshal(xsr, Header.class);
		

		String retentionId = je.getValue().getRetentionID() == null ? "1" : je.getValue().getRetentionID().trim();
		String messageId = je.getValue().getMessageID() == null ? "NO_ID" : je.getValue().getMessageID().getValue().trim();
		dpMessage.setRetentionId(retentionId);
		dpMessage.setXmlMessageId(messageId);
		dpMessage.setXmlMessage(message);
		
		return dpMessage;
	}

	// TODO: Make user of Connection pool mechanism (for e.g. C3PO)
	public void persistMessageInDatabase(DpMessage dpMessage) throws ClassNotFoundException, SQLException, XpathException,
																			UnsupportedEncodingException, JAXBException, XMLStreamException {
		String query = "insert into dp_messages (message_id,message,message_retention_type,message_date) values (?,?,?,current timestamp)";
		if (log.isDebugEnabled()) {
			log.debug("persisting message in db with following query : ");
			log.debug(query);
			log.debug(" arg 1: " + dpMessage.getXmlMessageId());
			//log.debug(" arg 2: " + dpMessage.getXmlMessage());
			log.debug(" arg 3: " + dpMessage.getRetentionId());
		}

		if (!initialized || conn == null || (conn != null && conn.isClosed())) {
			try {
				init();
			} catch (Exception ex) {
				throw new SQLException("Exception during database initialization: "+ ex.toString());
			}
		}

		// prepare the statement
		PreparedStatement insertStmt = conn.prepareStatement(query);
		insertStmt.setString(1, dpMessage.getXmlMessageId());
		insertStmt.setString(2, dpMessage.getXmlMessage());
		insertStmt.setString(3, dpMessage.getRetentionId());

		// execute the statement
		if (insertStmt.executeUpdate() != 1) {
			log.info("No record inserted.");
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
		dpc = new DpLogPersisterTool(Environment.valueOf(args[0]));
		dpc.start();
	}

	public void run() {
            try {
                    while (true) {
                        try{
                                //	This should run every 5 minutes
                                if(iteration_counter>3){
                                        dpc.iterateOverMessages(QueueType.RETRY);
                                        iteration_counter = 0;
                                        sleep(SLEEP_TIME);
                                }else{
                                        dpc.iterateOverMessages(QueueType.NORMAL);
                                        sleep(SLEEP_TIME);
                                }

                                //     Cleanup: Once a day = 60 * 24 = 1440 (SLEEP_TIME = 60000 milliz = 1 min)
                                if(iteration_counter>1440){
                                    dpc.cleanupOldMessages();
                                    iteration_counter = 0;
                                }

                        }catch(Exception e){
                                log.error(DpLogPersisterTool.class.getName()	+ " General exception occured, application crash....");
                                e.printStackTrace();
                        }
                        iteration_counter++;
                        iteration_counter_cleanup++;
                    }
		} finally {
			dpc.closeConnection();
			dpMessageTool.closeConnection();
		}
	}

        private void cleanupOldMessages() throws SQLException{
            String query = "delete from DP_MESSAGES where MESSAGE_ID in ( SELECT m.MESSAGE_ID FROM   DP_MESSAGES m, DP_MESSAGES_RETENTIONTYPES mr WHERE  m.MESSAGE_RETENTION_TYPE = mr.RETENTION_TYPE_ID AND m.MESSAGE_DATE < CURRENT DATE - mr.DAYS DAYS )";
            if (log.isDebugEnabled()) {
		log.debug("cleaning up data : ");
            }

            if (!initialized || conn == null || (conn != null && conn.isClosed())) {
                    try {
                        init();
                    } catch (Exception ex) {
                        throw new SQLException("Exception during database initialization: "+ ex.toString());
                    }
            }

		// prepare the statement
		PreparedStatement deleteStmt = conn.prepareStatement(query);
                deleteStmt.executeQuery();
        }
            
            
        
	private void iterateOverMessages(QueueType qType) throws JAXBException, XMLStreamException, MQException {
		String message = null;
		DpMessage dpMessage = null;
		List<String> allMessagesToBeHandled = null;
			
		//	Getting all the messages from the queue
		try {
			//	Handle the ERROR Messages --> readErrorQueue() ...or not!
			if(qType.equals(QueueType.RETRY))
				allMessagesToBeHandled = dpMessageTool.readErrorRetryQueue();
			if(qType.equals(QueueType.NORMAL))
				allMessagesToBeHandled = dpMessageTool.readQueue();
		} catch (MQException mex) {
			log.error("MQException getting messages from queue: "+ mex.getMessage());
		} catch (IOException e) {
			log.error("IO Exception : getting message from Queue, msg: "+e.getMessage() );
		}

			
		try {

			//	Iterating over the messages
			for (int i = 0; i < allMessagesToBeHandled.size(); i++) {
				message = allMessagesToBeHandled.get(i);
				try {
					dpMessage = createDpMessage(message);
					dpc.persistMessageInDatabase(dpMessage);
				} catch (JAXBException e) {
					throw new Exception("Exception during JAXB creation: "+ e.getMessage());
				} catch(SQLException ex2){
					log.debug("ERRORCODE: "+ex2.getErrorCode()+" ...occured ");
					//	ERRORCODE -803 is duplicate record...skip persisting this
					if(!(ex2.getErrorCode()==-803)){
						throw new SQLException();
					}else{
						log.debug("DUPLICATE RECORD...skipping persistance of msgID: "+dpMessage.getXmlMessageId());
					}
						
				}
				
			}
		} catch (SQLException ex) {
			try{
				retryStatusMap.add(dpMessage.getXmlMessageId());
				int occurrences = Collections.frequency(retryStatusMap, dpMessage.getXmlMessageId());
				log.debug(dpMessage.getXmlMessageId()+" occurs: "+occurrences+" times in retryStatusMap");
				if(occurrences>=max_retry_times){
					Iterator<String> iterator = retryStatusMap.iterator();
					log.error("putting message with ID: " + dpMessage.getXmlMessageId()+ " in ERROR Q, exception was: "+ ex.getMessage());
					
					writeAllMessagesInQueue(allMessagesToBeHandled,QueueType.ERROR,dpMessage.getXmlMessageId());
					while(iterator.hasNext()){
						if(iterator.next() == null ? dpMessage.getXmlMessageId() == null : iterator.next().equals(dpMessage.getXmlMessageId()))
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
			}
		}catch(Exception ex){
			log.error("No SQL exception for : " + dpMessage.getXmlMessageId()+ " wrong message? putting in ERROR Q, exception was: "+ ex.getMessage());
			try{
				writeAllMessagesInQueue(allMessagesToBeHandled,QueueType.ERROR,dpMessage.getXmlMessageId());
			} catch (MQException e) {
				log.error("MQ Exception : + writing message to ERROR Queue for message with id "+dpMessage.getXmlMessageId()+" msg: "+e.getMessage() );
			} catch (IOException e) {
				log.error("IO Exception : + writing message to ERROR Queue for message with id "+dpMessage.getXmlMessageId()+" msg: "+e.getMessage() );
			}
			closeConnection();
		}
		
	}

	private void writeAllMessagesInQueue(List<String> messagesToBeHandled , QueueType qtype, String msgIdWithProblem) throws MQException, IOException, JAXBException, XMLStreamException{
		
            for (String message : messagesToBeHandled) {
                DpMessage dpMessage = createDpMessage(message);
                if(dpMessage.getXmlMessageId().equalsIgnoreCase(msgIdWithProblem))
                    dpMessageTool.putMessageInQueue(message, qtype);
                else
                    dpMessageTool.putMessageInQueue(message, QueueType.NORMAL);
            }
	}
	
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
                            log.error("Exception during closing connection to db "+e.getMessage()+ " finally closing!!!");
			}
		}
	}
}
