package com.lgh.model;

public class CommandResponse {
	private byte responseCode=-1;
	private String message;
	
	public CommandResponse(byte responseCode, String message) {
		super();
		this.responseCode = responseCode;
		this.message = message;
	}
	public byte getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(byte responseCode) {
		this.responseCode = responseCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
