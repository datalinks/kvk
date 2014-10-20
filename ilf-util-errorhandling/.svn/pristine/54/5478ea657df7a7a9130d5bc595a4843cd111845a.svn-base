package com.kvk.dp.errorhandlingtool.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Envelope" , namespace = "http://schemas.xmlsoap.org/soap/envelope/" )
public class Envelope {


    @XmlElement(name="Header", type=Header.class )
    Header Header;
    @XmlElement(name="Body", type=Body.class)
    Body Body;

    
	public Body getBody() {
		return Body;
	}

	public void setBody(Body body) {
		Body = body;
	}

	public Header getHeader() {
		return Header;
	}

	public void setHeader(Header header) {
		Header = header;
	}
}
