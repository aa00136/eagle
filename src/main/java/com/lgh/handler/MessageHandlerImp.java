package com.lgh.handler;

import com.lgh.model.db.Message;

import java.util.List;

/**
 * Created by ligh on 2017/5/8.
 */
public class MessageHandlerImp implements MessageHandler {
    public Boolean handlerMessage(List<Message> messagesList) {
        for (Message message : messagesList) {
            System.out.println("msg_id=" + message.getId() + "`content=" + message.getContent());
        }
        return true;
    }
}
