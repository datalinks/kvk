package com.kvk.dp.logpersisttool.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Header", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class Header {

    @XmlElement(name="MessageID", namespace = "http://www.w3.org/2005/08/addressing")
    String MessageID;
    @XmlElement(name="retentionID", namespace = "http://www.kvk.nl/dp/meta")
    String retentionID;
    
    public String getRetentionID(){
    	return retentionID;
    }
    
	public String getMessageID() {
		return MessageID;
	}

	public void setMessageID(String messageID) {
		MessageID = messageID;
	}

}
