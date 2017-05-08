package com.lgh.model.command;

import com.lgh.model.db.Message;

import java.util.ArrayList;
import java.util.List;

public class CommandResp {
    private byte responseCode=-1;
	private String message;
    private List<Message> data = new ArrayList<Message>();

    public CommandResp(byte responseCode, String message) {
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

    public List<Message> getData() {
        return data;
    }

    public void setData(List<Message> data) {
        this.data = data;
    }
}
