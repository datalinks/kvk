package com.kvk.dp.errorhandlingtool.model;

import java.util.Date;

public class ErrorHandlingObject {

	private int errorhandling_id;
	private String messageId;
	private String retryEndpoint;
	private Date entryDate;
	private Date retryDate;
	private String message;
	private int retryCount;
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
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getErrorhandling_id() {
		return errorhandling_id;
	}
	public void setErrorhandling_id(int errorhandling_id) {
		this.errorhandling_id = errorhandling_id;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getRetryEndpoint() {
		return retryEndpoint;
	}
	public void setRetryEndpoint(String retryEndpoint) {
		this.retryEndpoint = retryEndpoint;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	public Date getRetryDate() {
		return retryDate;
	}
	public void setRetryDate(Date retryDate) {
		this.retryDate = retryDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private String status;
}
