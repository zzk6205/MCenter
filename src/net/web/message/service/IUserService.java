package net.web.message.service;

import java.util.List;
import java.util.Map;

import net.web.message.entity.User;

public interface IUserService {

	public User getUser(User user);

	public User saveUser(User user);

	public List<User> getUserList(Map<String, Object> params);

}
