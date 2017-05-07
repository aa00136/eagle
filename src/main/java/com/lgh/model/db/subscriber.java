package com.lgh.model.db;

import com.huisa.common.reflection.annotations.huisadb_alias;
import com.huisa.common.reflection.annotations.huisadb_ignore;

import java.util.Date;

/*  */
@huisadb_alias("subscriber")
public class Subscriber {
	@huisadb_ignore
	private Integer id;//remark:;length:10; not null,default:null
	private String name;//remark:;length:64; not null,default:null
	private Integer status;//remark:;length:3
	@huisadb_alias("topic_name")
	private String topicName;//remark:;length:10; not null,default:null
	@huisadb_alias("max_send_msg_id")
	private Integer maxSendMsgId;//remark:;length:10
	@huisadb_alias("min_consume_msg_id")
	private Integer minConsumeMsgId;//remark:;length:10
	@huisadb_alias("create_time")
	private Date createTime;//remark:;length:19; not null,default:null
	@huisadb_alias("update_time")
	private Date updateTime;//remark:;length:19

	public Subscriber() {
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public void setMaxSendMsgId(Integer maxSendMsgId) {
		this.maxSendMsgId = maxSendMsgId;
	}

	public Integer getMaxSendMsgId() {
		return maxSendMsgId;
	}

	public void setMinConsumeMsgId(Integer minConsumeMsgId) {
		this.minConsumeMsgId = minConsumeMsgId;
	}

	public Integer getMinConsumeMsgId() {
		return minConsumeMsgId;
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