package com.kvk.dp.errorhandlingtool.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;


public class DpMessageSender {

	CloseableHttpClient httpclient 	= null; 
	HttpPost httppost				= null;
	StringEntity entityIn			= null;

	static Logger log = Logger.getLogger(DpMessageSender.class.getName());

	
	public int callService(String url, String requestBody) throws ClientProtocolException, IOException, HttpException {

		httpclient = HttpClients.createDefault(); 
		
		if(!url.startsWith("http://")){
			url = "http://"+url;
		}
		
		String message = changeSecurityHeader(requestBody);
		int result = 0;
		httppost = new HttpPost(url);
		entityIn = new StringEntity(message);
		httppost.setEntity(entityIn);
		CloseableHttpResponse response = null;
		
		try{
			response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201 ) {
				log.debug("Exception calling " + url+ " statusCode " + response.getStatusLine().getStatusCode());
			}
			result = response.getStatusLine().getStatusCode();
		}catch(Exception ex){
			//	Something went wrong eh?
			result = 500;
		}finally{
			response.close();
		}
		return result;
		
		
		
	}

	/*	Only for password credential
		This is needed for the authentication, when a nonce is created it cannot be reused, 
		a new nonce need to be created	on every request 
	*/	
	private String changeSecurityHeader(String message) throws UnsupportedEncodingException{
		int startIndex 	  = message.indexOf("wsse:Security")-1;
		int stopIndex  	  = message.indexOf("/wsse:Security")+15;
		String newString	  = "";
		if(startIndex > -1 && stopIndex >-1){
			String before 	= message.substring(0,startIndex);
			String end		= message.substring(stopIndex,message.length());
			newString = before+end;
		}
		
		String stringToReplace = "</soapenv:Header>";
		String securityHeader = SoapUtil.generateSoapSecurityHeader()+stringToReplace;
		return newString.replace(stringToReplace, securityHeader);
	}
}

