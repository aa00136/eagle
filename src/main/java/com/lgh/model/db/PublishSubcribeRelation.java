package com.lgh.model.db;

import com.huisa.common.reflection.annotations.huisadb_alias;
import com.huisa.common.reflection.annotations.huisadb_ignore;

import java.util.Date;

/*  */
@huisadb_alias("publish_subcribe_relation")
public class PublishSubcribeRelation {
	@huisadb_ignore
	private Integer id;//remark:;length:10; not null,default:null
	@huisadb_alias("produce_id")
	private Integer produceId;//remark:;length:10; not null,default:null
	@huisadb_alias("consumer_id")
	private Integer consumerId;//remark:;length:10; not null,default:null
	@huisadb_alias("create_time")
	private Date createTime;//remark:;length:19; not null,default:null
	@huisadb_alias("update_time")
	private Date updateTime;//remark:;length:19; not null,default:null

	public PublishSubcribeRelation() {
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setProduceId(Integer produceId) {
		this.produceId = produceId;
	}

	public Integer getProduceId() {
		return produceId;
	}

	public void setConsumerId(Integer consumerId) {
		this.consumerId = consumerId;
	}

	public Integer getConsumerId() {
		return consumerId;
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