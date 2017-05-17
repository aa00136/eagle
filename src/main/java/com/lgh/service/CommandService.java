package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.model.command.*;
import com.lgh.model.db.Message;
import com.lgh.util.GsonSerializeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令服务
 *
 * @author ligh
 * @create 2017-05-17 16:44
 **/
public class CommandService {
    public static SubscribeCommandResp excuteSubscribeCmd(Command subscribeCmd) throws ServiceException {
        Map<String, Object> subscribeMap = GsonSerializeUtil.fromJson(subscribeCmd.getBody());
        String topicName = (String) subscribeMap.get("topic_name");
        String clientName = (String) subscribeMap.get("client_name");
        if (StringUtils.isBlank(topicName) || StringUtils.isBlank(clientName)) {
            throw new ServiceException(-1, "client_name or topic_name is blank");
        }
        SubscriberService.addSubscriber(topicName, clientName);

        return new SubscribeCommandResp(subscribeCmd.getRequestId(), (byte) 1, "subscribe success");
    }

    public static PullCommandResp excutePullCmd(Command pullCmd) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(pullCmd.getBody());
        String topicName = (String) body.get("topic_name");
        String clientName = (String) body.get("client_name");
        Double limit = (Double) body.get("limit");
        if (StringUtils.isBlank(topicName) || StringUtils.isBlank(clientName) || limit.intValue() <= 0) {
            throw new ServiceException(-1, "client_name or topic_name or limit is blank");
        }
        List<Message> messageList = QueueService.readMessage(topicName, clientName, limit.intValue());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("data", messageList);

        return new PullCommandResp(pullCmd.getRequestId(), (byte) 1, GsonSerializeUtil.toJson(resultMap));
    }

    public static PublishCommandResp excutePublishCmd(Command publishCmd) throws ServiceException {
        Message message = GsonSerializeUtil.fromJson(publishCmd.getBody(), Message.class);
        if (StringUtils.isBlank(message.getTopicName())) {
            throw new ServiceException(-1, "topic_name is blank");
        }
        QueueService.writeMessage(message);

        return new PublishCommandResp(publishCmd.getRequestId(), (byte) 1, "publish success");
    }

    public static PublishTopicCommandResp excutePublishTopicCmd(Command publishTopicCmd) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(publishTopicCmd.getBody());
        String topicName = (String) body.get("topic_name");
        if (StringUtils.isBlank(topicName)) {
            throw new ServiceException(-1, "topic_name is blank");
        }
        TopicService.createTopic(topicName);

        return new PublishTopicCommandResp(publishTopicCmd.getRequestId(), (byte) 1, "publish topic success");
    }

    public static void excutePullAckCmd(Command pullAckCmd) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(pullAckCmd.getBody());
        String topicName = (String) body.get("topic_name");
        String clientName = (String) body.get("client_name");
        Double msgId = (Double) body.get("msg_id");
        if (StringUtils.isBlank(topicName) || StringUtils.isBlank(clientName) || msgId.intValue() <= 0) {
            throw new ServiceException(-1, "client_name or topic_name or msg_id is blank");
        }
        SubscriberService.updateConsumeState(topicName, clientName, msgId.intValue());
    }

    public static UnsubscribeCommandResp excuteUnsubscribeCmd(Command unsubscribeCmd) throws ServiceException {
        Map<String, Object> subscribeMap = GsonSerializeUtil.fromJson(unsubscribeCmd.getBody());
        String topicName = (String) subscribeMap.get("topic_name");
        String clientName = (String) subscribeMap.get("client_name");
        if (StringUtils.isBlank(clientName) || StringUtils.isBlank(topicName)) {
            throw new ServiceException(-1, "client_name or topic_name is blank");
        }
        SubscriberService.deleteSubscriber(topicName, clientName);

        return new UnsubscribeCommandResp(unsubscribeCmd.getRequestId(), (byte) 1, "unsubscribe success");
    }
}
