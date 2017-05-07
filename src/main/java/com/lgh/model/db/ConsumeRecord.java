package com.lgh.model.db;

import com.huisa.common.reflection.annotations.huisadb_alias;
import com.huisa.common.reflection.annotations.huisadb_ignore;

import java.util.Date;

/*  */
@huisadb_alias("consume_record")
public class ConsumeRecord {
	@huisadb_ignore
	private Integer id;//remark:;length:10; not null,default:null
	@huisadb_alias("topic_id")
	private Integer topicId;//remark:;length:10; not null,default:null
	@huisadb_alias("message_id")
	private Integer messageId;//remark:;length:10; not null,default:null
	@huisadb_alias("status")
	private Integer status;//remark:;length:10; not null,default:null
	@huisadb_alias("create_time")
	private Date createTime;//remark:;length:19; not null,default:null
	@huisadb_alias("update_time")
	private Date updateTime;//remark:;length:19; not null,default:null

	public ConsumeRecord() {
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}

	public Integer getTopicId() {
		return topicId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public Integer getMessageId() {
		return messageId;
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