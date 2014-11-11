package com.kvk.dp.errorhandlingtool;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import com.kvk.dp.errorhandlingtool.jaxb.Header;

public class TestXmlMessages {
	
	String xmlFileName  		= "src/main/resources/xml/inputMessage.xml";

	
	private String buildInputStringFromXmlFile(String xmlFileName) throws IOException {
		StringBuilder result = new StringBuilder();
		BufferedReader input = new BufferedReader(new FileReader(xmlFileName));
		String line = null;
		while ((line = input.readLine()) != null) {
			result.append(line);
			result.append(System.getProperty("line.separator"));
		}
		input.close();
		return result.toString();
	}

	
	@Test
	public void testXmlMessageCreation() throws Exception{
			String xmlMessage = buildInputStringFromXmlFile(xmlFileName);
			String expectedRetentionId = "8";
			
			XMLInputFactory xif = XMLInputFactory.newFactory();
			InputStream xmlInputStream = new ByteArrayInputStream(xmlMessage.getBytes("UTF-8"));
			XMLStreamReader xsr = xif.createXMLStreamReader(xmlInputStream);
	        xsr.nextTag(); // Advance to Envelope tag
	        xsr.nextTag(); // Advance to Header tag
	        
	        JAXBContext jc = JAXBContext.newInstance(Header.class);
	        Unmarshaller unmarshaller = jc.createUnmarshaller();
	        JAXBElement<Header> je = unmarshaller.unmarshal(xsr, Header.class);
	        


	}
}
