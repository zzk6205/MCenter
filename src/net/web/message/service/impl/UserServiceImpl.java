package net.web.message.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.web.message.dao.IUserDao;
import net.web.message.entity.User;
import net.web.message.service.IUserService;

import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements IUserService {

	@Resource(name = "userDao")
	private IUserDao userDao;

	@Override
	public User getUser(User user) {
		return userDao.getUser(user);
	}

	@Override
	public User saveUser(User user) {
		return userDao.saveUser(user);
	}

	@Override
	public List<User> getUserList(Map<String, Object> params) {
		return userDao.getUserList(params);
	}

}
