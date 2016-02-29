package net.web.message.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.web.base.mongodb.MongodbBaseDao;
import net.web.message.dao.IUserMessageDao;
import net.web.message.entity.UserMessage;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("userMessageDao")
public class UserMessageDaoImpl extends MongodbBaseDao<UserMessage> implements IUserMessageDao {

	@Override
	protected Class<UserMessage> getEntityClass() {
		return UserMessage.class;
	}

	@Override
	@Resource(name = "mongoTemplate")
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<UserMessage> insertUserMessage(List<UserMessage> list) {
		return this.insertAll(list);
	}

	@Override
	public List<UserMessage> getUnReadMessage(String userId, String platform) {
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		Query query = new Query();

		query.addCriteria(Criteria.where("userId").is(userId));
		query.addCriteria(Criteria.where("platform").is(platform));
		query.addCriteria(Criteria.where("status").is(UserMessage.MESSAGE_STATUS_UNREAD));

		Criteria criteria = new Criteria();
		criteria = criteria.orOperator(Criteria.where("expireTime").gt(now), Criteria.where("expireTime").exists(false));
		query.addCriteria(criteria);

		return this.find(query);
	}

	@Override
	public UserMessage updateUserMessageStatus(String userId, String platform, String msgId, String status) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId));
		query.addCriteria(Criteria.where("platform").is(platform));
		query.addCriteria(Criteria.where("msgId").is(msgId));
		Update update = new Update().set("status", status);
		return this.findAndModify(query, update);
	}

}
