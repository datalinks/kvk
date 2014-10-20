package com.kvk.dp.logpersisttool;

import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.db2.jcc.am.Connection;
import com.ibm.db2.jcc.am.ResultSet;

public class TestXmlQueryFunction{

	static Logger log = Logger.getLogger(DpLogPersisterTool.class.getName());

	//	TODO: BAse64 encoded properties
     private static String user 			= "dbxcvu";
     private static String pwd 				= "zw4rt3p3n";
     private static String url 				= "jdbc:db2://rs94asoi.k94.kvk.nl:60810/ild01";
     private Connection conn				= null;
     private String messageId;

     @Before
     public void init() throws ClassNotFoundException, SQLException{
    	 
    	 log.info("Initializing....");
         Class.forName("com.ibm.db2.jcc.DB2Driver");
         conn = (Connection) DriverManager.getConnection(url, user, pwd);
     }


     //@Test
     public void testXmlQueryDatabase() throws ClassNotFoundException, SQLException, XpathException, UnsupportedEncodingException, JAXBException, XMLStreamException{
    	 String query = ""
    	 		+ "xquery for $message in db2-fn:xmlcolumn('DATAPOWER.DP_MESSAGES.MESSAGE')/*:Envelope where $message/*:Header/*:MessageID/text() = 'uuid://d63d9f83-100b-461f-9ecf-7fc89cee8f9f' "
    	 		+ "return $message";

    	 // prepare the statement
    	 PreparedStatement insertStmt = conn.prepareStatement(query);
    	 java.sql.ResultSet rss = insertStmt.executeQuery();
    	 while(rss.next()){
    		 System.err.println(removeNameSpaceStuff(rss.getString(1)));

    	 }
     }
     
     
    private String removeNameSpaceStuff(String resultSet){
    	String namespacePattern = "xmlns:.*\"";
    	String tagPattern = "<.* >";
    	String tagPatternClose = "</.*>";
    	//String result = resultSet.replaceAll(namespacePattern, "").replaceAll(tagPattern, "").replaceAll(tagPatternClose, "");
    	String result = resultSet.replaceAll(namespacePattern, "");
    	return result;
    }
     
    @After
    public void closeConnection(){
    	try {
            	if(conn != null)
            		conn.close();
        }catch (Exception e) { log.error("Exception during closing connection to db "+e.getMessage()); }
        finally { 
          	try { 
          		conn.close();
          		log.debug("Closing connection to db");
          	} catch (Exception e) { } 
        }
    }  
}
