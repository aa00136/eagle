package com.lgh.model.command;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

public class Command {
	protected int requestId;
	protected short commandCode;
	protected byte responseCode = 1;
	protected int bodyLength;
	protected byte extentionLength;
	protected String body;
	protected String extention;

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public short getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(short commandCode) {
		this.commandCode = commandCode;
	}

	public byte getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(byte responseCode) {
		this.responseCode = responseCode;
	}

	public int getBodyLength() throws UnsupportedEncodingException {
		if (StringUtils.isNotBlank(body)) {
			return body.getBytes("utf-8").length;
		}
		return 0;
	}

	public byte getExtentionLength() throws UnsupportedEncodingException {
		if (StringUtils.isNotBlank(extention)) {
			return (byte) extention.getBytes("utf-8").length;
		}
		return 0;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getExtention() {
		return extention;
	}

	public void setExtention(String extention) {
		this.extention = extention;
	}

	@Override
	public String toString() {
		return "Command [requestId=" + requestId + ", commandCode=" + commandCode + ", responseCode=" + responseCode
				+ ", bodyLength=" + bodyLength + ", extentionLength=" + extentionLength + ", body=" + body
				+ ", extention=" + extention + "]";
	}

	public Command(){
		
	}
	public Command(int requestId, short commandCode, String body) {
		super();
		this.requestId = requestId;
		this.commandCode = commandCode;
		this.body = body;
	}

}
