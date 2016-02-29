package net.web.message.dao;

import java.util.List;

import net.web.message.entity.UserMessage;

public interface IUserMessageDao {

	public List<UserMessage> insertUserMessage(List<UserMessage> list);

	public List<UserMessage> getUnReadMessage(String userId, String platform);

	public UserMessage updateUserMessageStatus(String userId, String platform, String msgId, String status);

}
