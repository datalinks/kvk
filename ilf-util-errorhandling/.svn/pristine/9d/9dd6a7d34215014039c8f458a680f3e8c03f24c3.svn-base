package com.kvk.dp.errorhandlingtool;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import com.kvk.dp.errorhandlingtool.util.DpErrorHandlingUtil;
import com.kvk.dp.errorhandlingtool.util.DpMessageSender;

public class TestSendingMessage {

	String message = "<soapenv:Envelope xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:kvk-dp-meta=\"http://www.kvk.nl/dp/meta\" xmlns:kvk=\"http://www.kvk.nl/dp\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" >"+
					"<soapenv:Header><wsa:To>http://ws.kvk.nl/HRD-Persoon</wsa:To><wsa:Action>http://ws.kvk.nl/UitvoerenRR1</wsa:Action><wsa:MessageID xmlns:ns=\"http://schemas.kvk.nl/contracts/hrd/opvraagservices/2010/01\" xmlns:dp=\"http://dp.kvk.backendtest.nl\" xmlns:kvk=\"http://schemas.kvk.nl/schemas/gen/integratielaag/2013/01\">chris-124</wsa:MessageID>"+
					"<wsse:Security xmlns:ns=\"http://schemas.kvk.nl/contracts/hrd/opvraagservices/2010/01\" xmlns:dp=\"http://dp.kvk.backendtest.nl\" xmlns:kvk=\"http://schemas.kvk.nl/schemas/gen/integratielaag/2013/01\">"+
					"<wsse:UsernameToken wsu:Id=\"SecurityToken-6138db82-5a4c-4bf7-915f-af7a10d9ae96\"><wsse:Username>smoketest</wsse:Username><wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">vo/+8piVwVL6k0uSj8xFM1iIZ3o=</wsse:Password><wsse:Nonce EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">a3ZrX2RwX3Rlc3R1dGlsX25vbmNlXzIwMTQtMDMtMTlUMDI6MTI6MjJaX25yXzAuMTg1NzkxOTY4OTY5MzMwNg==</wsse:Nonce><wsu:Created>2014-03-19T02:12:22Z</wsu:Created></wsse:UsernameToken>"+
					"</wsse:Security><kvk-dp-meta:endpoints><kvk-dp-meta:endpoint><kvk-dp-meta:aflever-endpoint>ilf-hrd-opvragenPersoonService-a1</kvk-dp-meta:aflever-endpoint></kvk-dp-meta:endpoint></kvk-dp-meta:endpoints>"+
					"<wsa:From>http://www.kvk.nl/smoketest</wsa:From><kvk-dp-meta:domain-errorhandling-endpoint>dpmq://ilf-sbox-hrd-mqm-group/?RequestQueue=DP.ERRORHANDLING.LQ01</kvk-dp-meta:domain-errorhandling-endpoint><kvk-dp-meta:domain-messagepersist-endpoint>dpmq://ilf-sbox-hrd-mqm-group/?RequestQueue=DP.MESSAGEPERSIST.LQ01</kvk-dp-meta:domain-messagepersist-endpoint><kvk-dp-meta:retry-endpoint>10.10.45.123:40601</kvk-dp-meta:retry-endpoint>"+
					"<kvk-dp-meta:retry-policyid>1</kvk-dp-meta:retry-policyid><kvk-dp-meta:flow-logging>true</kvk-dp-meta:flow-logging><kvk-dp-meta:flow-log-category>ilf-sbox-hrd</kvk-dp-meta:flow-log-category></soapenv:Header><soapenv:Body xmlns:ns=\"http://schemas.kvk.nl/contracts/hrd/opvraagservices/2010/01\" xmlns:dp=\"http://dp.kvk.backendtest.nl\" xmlns:kvk=\"http://schemas.kvk.nl/schemas/gen/integratielaag/2013/01\" ><ns:opvragenPersoonRequest>"+
					"<ns:header><ns:applicationID>HRS</ns:applicationID><ns:gebruiker>dbxmkb</ns:gebruiker></ns:header><ns:bsn>191313117</ns:bsn></ns:opvragenPersoonRequest></soapenv:Body></soapenv:Envelope>"; 	
			
	
	//@Test
	public void testSendMessageToBackend() throws ClientProtocolException, IOException, HttpException{
		DpMessageSender dpm = new DpMessageSender();

		int actualResult = dpm.callService("10.10.45.123:40601", message);
		int expectedResult = 200;
		assertEquals("actual result was: "+actualResult+" we expected to have a "+expectedResult,actualResult, expectedResult);
	}
}
