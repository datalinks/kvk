package com.kvk.dp.errorhandlingtool.model;

public class ErrorHandlingRetryType {

	private int retrytype_id;
	private int retry_countr;
	private int retry_interval;
	
	public int getRetrytype_id() {
		return retrytype_id;
	}
	public void setRetrytype_id(int retrytype_id) {
		this.retrytype_id = retrytype_id;
	}
	public int getRetry_countr() {
		return retry_countr;
	}
	public void setRetry_countr(int retry_countr) {
		this.retry_countr = retry_countr;
	}
	public int getRetry_interval() {
		return retry_interval;
	}
	public void setRetry_interval(int retry_interval) {
		this.retry_interval = retry_interval;
	}
}
