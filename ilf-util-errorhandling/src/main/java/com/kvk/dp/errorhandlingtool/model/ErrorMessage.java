package com.kvk.dp.errorhandlingtool.model;

public class ErrorMessage {

	private int retryPolicyId;
	private String retryEndpoint;
	private String xmlMessageId;
	private String xmlMessage;
	private String retryTo;
	private String retryAction;
	
	public String getRetryTo() {
		return retryTo;
	}
	public void setRetryTo(String retryTo) {
		this.retryTo = retryTo;
	}
	public String getRetryAction() {
		return retryAction;
	}
	public void setRetryAction(String retryAction) {
		this.retryAction = retryAction;
	}
	public int getRetryPolicyId() {
		return retryPolicyId;
	}
	public void setRetryPolicyId(int retryPolicyId) {
		this.retryPolicyId = retryPolicyId;
	}
	public String getRetryEndpoint() {
		return retryEndpoint;
	}
	public void setRetryEndpoint(String retryEndpoint) {
		this.retryEndpoint = retryEndpoint;
	}
	
	public String getXmlMessageId() {
		return xmlMessageId;
	}
	public void setXmlMessageId(String xmlMessageId) {
		this.xmlMessageId = xmlMessageId;
	}
	public String getXmlMessage() {
		return xmlMessage;
	}
	public void setXmlMessage(String xmlMessage) {
		this.xmlMessage = xmlMessage;
	}
}
