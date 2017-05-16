package com.lgh.handler;

import com.lgh.model.db.Message;

/**
 * Created by ligh on 2017/5/8.
 */
public class MessageHandlerImp implements MessageHandler {
    public Boolean handlerMessage(Message message) {
        System.out.println("msg_id=" + message.getId() + "`content=" + message.getContent());
        return true;
    }
}
