package com.lgh.dao;

import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.model.db.Subscriber;

/**
 * Created by ligh on 2017/5/6.
 */
public class SubscriberDao extends BaseDao {
    public void addConsumer(Subscriber subscriber) throws ServiceException {
        add(subscriber);
    }
    public Subscriber getSubscriberByClientName(String clientName) throws ServiceException {
        String sql = "select * from subscriber where name = ?";
        return get(sql,Subscriber.class, clientName);
    }
}
