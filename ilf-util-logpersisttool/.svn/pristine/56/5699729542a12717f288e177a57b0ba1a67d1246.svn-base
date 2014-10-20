package com.kvk.dp.logpersisttool.jaxb;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MessageID")
@XmlType(name = "MessageID",namespace="http://www.w3.org/2005/08/addressing")
public class MessageId{
    @XmlValue
    protected String value;

    @XmlAttribute(namespace="http://schemas.xmlsoap.org/soap/envelope/")
    protected String actor;
    

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}