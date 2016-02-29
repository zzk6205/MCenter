package net.web.message.dao;

import java.util.List;
import java.util.Map;

import net.web.message.entity.User;

public interface IUserDao {

	public User getUser(User user);

	public User saveUser(User user);

	public List<User> getUserList(Map<String, Object> params);

}
