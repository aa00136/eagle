package com.lgh.handler;

import com.lgh.model.db.Message;

import java.util.List;

/**
 * Created by ligh on 2017/5/8.
 */
public interface MessageHandler {
    public Boolean handlerMessage(List<Message> messagesList);
}
