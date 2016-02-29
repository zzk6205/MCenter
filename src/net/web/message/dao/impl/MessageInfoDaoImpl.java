package net.web.message.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import net.web.base.mongodb.MongodbBaseDao;
import net.web.message.dao.IMessageInfoDao;
import net.web.message.entity.MessageInfo;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository("messageInfoDao")
public class MessageInfoDaoImpl extends MongodbBaseDao<MessageInfo> implements IMessageInfoDao {

	@Override
	protected Class<MessageInfo> getEntityClass() {
		return MessageInfo.class;
	}

	@Override
	@Resource(name = "mongoTemplate")
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public MessageInfo insertMessage(MessageInfo info) {
		return this.insert(info);
	}

	@Override
	public List<MessageInfo> getMessageInfo(List<String> ids) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").in(ids));
		return this.find(query);
	}

}
