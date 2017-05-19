package com.lgh.model.db;

import com.huisa.common.reflection.annotations.huisadb_alias;
import com.huisa.common.reflection.annotations.huisadb_ignore;

import java.util.Date;

/*  */
@huisadb_alias("consume_info")
public class ConsumeInfo {
    @huisadb_ignore
    private Integer id;//remark:;length:10; not null,default:null
    @huisadb_alias("msg_id")
    private Integer msgId;//remark:;length:10; not null,default:null
    @huisadb_alias("topic_id")
    private Integer topicId;//remark:;length:10; not null,default:null
    @huisadb_alias("subscriber_id")
    private Integer subscriberId;//remark:;length:10; not null,default:null
    @huisadb_alias("consume_count")
    private Integer consumeCount;//remark:;length:10
    @huisadb_alias("create_time")
    private Date createTime;//remark:;length:19
    @huisadb_alias("update_time")
    private Date updateTime;//remark:;length:19

    public ConsumeInfo() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setSubscriberId(Integer subscriberId) {
        this.subscriberId = subscriberId;
    }

    public Integer getSubscriberId() {
        return subscriberId;
    }

    public void setConsumeCount(Integer consumeCount) {
        this.consumeCount = consumeCount;
    }

    public Integer getConsumeCount() {
        return consumeCount;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}