package com.kvk.dp.errorhandlingtool.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Header", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class Header {

    @XmlElement(name="MessageID", namespace = "http://www.w3.org/2005/08/addressing")
    String MessageID;
    @XmlElement(name="retry-policyid", namespace = "http://www.kvk.nl/dp/meta")
    int retryPolicyid;
    @XmlElement(name="retry-endpoint", namespace = "http://www.kvk.nl/dp/meta")
    String retryEndpoint;
    @XmlElement(name="RetryTo", namespace = "http://www.kvk.nl/dp/meta")
    String retryTo;
    @XmlElement(name="RetryAction", namespace = "http://www.kvk.nl/dp/meta")
    String retryAction;
    
    
	public String getRetryTo() {
		return retryTo;
	}

	public String getRetryAction() {
		return retryAction;
	}


	public String getRetryEndpoint() {
		return retryEndpoint;
	}

	public int getRetryPolicyid() {
		return retryPolicyid;
	}


	public String getMessageID() {
		return MessageID;
	}

	public void setMessageID(String messageID) {
		MessageID = messageID;
	}

}
