package com.lgh.model.db;

import com.huisa.common.reflection.annotations.huisadb_alias;
import com.huisa.common.reflection.annotations.huisadb_ignore;

import java.util.Date;

/*  */
@huisadb_alias("producer")
public class Producer {
	@huisadb_ignore
	private Integer id;//remark:;length:10; not null,default:null
	private String name;//remark:;length:64; not null,default:null
	private Integer status;//remark:;length:3; not null,default:null
	@huisadb_alias("topic_id")
	private Integer topicId;//remark:;length:10; not null,default:null
	@huisadb_alias("create_time")
	private Date createTime;//remark:;length:19; not null,default:null
	@huisadb_alias("update_time")
	private Date updateTime;//remark:;length:19; not null,default:null

	public Producer() {
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

	public Integer getTopicId() {
		return topicId;
	}

	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}
}