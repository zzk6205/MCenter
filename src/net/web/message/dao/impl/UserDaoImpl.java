package net.web.message.dao.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.web.base.mongodb.MongodbBaseDao;
import net.web.message.dao.IUserDao;
import net.web.message.entity.User;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl extends MongodbBaseDao<User> implements IUserDao {

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	@Override
	@Resource(name = "mongoTemplate")
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public User saveUser(User user) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(user.getUserId()));
		query.addCriteria(Criteria.where("platform").is(user.getPlatform()));
		this.findAndRemove(query);
		return this.save(user);
	}

	@Override
	public User getUser(User user) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(user.getUserId()));
		query.addCriteria(Criteria.where("platform").is(user.getPlatform()));
		return this.findOne(query);
	}

	@Override
	public List<User> getUserList(Map<String, Object> params) {
		Query query = new Query();

		if (params.containsKey("userId")) {
			Object users = params.get("userId");
			if (users instanceof List) {
				query.addCriteria(Criteria.where("userId").in((List<?>) users));
			} else if (users instanceof String) {
				query.addCriteria(Criteria.where("userId").is(users));
			}
			params.remove("userId");
		}

		if (params.containsKey("platform")) {
			Object platform = params.get("platform");
			if (platform instanceof List) {
				query.addCriteria(Criteria.where("platform").in((List<?>) platform));
			} else if (platform instanceof String) {
				query.addCriteria(Criteria.where("platform").is(platform));
			}
			params.remove("platform");
		}

		if (params.size() > 0) {
			Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof List) {
					query.addCriteria(Criteria.where("properties." + key).in((List<?>) value));
				} else if (value instanceof String) {
					query.addCriteria(Criteria.where("properties." + key).is(value));
				}
			}
		}

		return this.find(query);
	}
}
