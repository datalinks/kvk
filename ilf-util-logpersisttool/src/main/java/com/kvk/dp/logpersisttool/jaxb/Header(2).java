package com.kvk.dp.logpersisttool.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kvk.dp.logpersisttool.jaxb.MessageId;

@XmlRootElement(name = "Header", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class Header {

	@XmlElement
    MessageId MessageID;
	
    @XmlElement(name="retentionID", namespace = "http://www.kvk.nl/dp/meta")
    String retentionID;
    
    public String getRetentionID(){
    	return retentionID;
    }
    
	public MessageId getMessageID() {
		return MessageID;
	}

	public void setMessageID(MessageId messageID) {
		MessageID = messageID;
	}

}
