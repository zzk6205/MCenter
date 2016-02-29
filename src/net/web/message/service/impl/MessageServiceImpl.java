package net.web.message.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.web.message.dao.IMessageInfoDao;
import net.web.message.dao.IUserMessageDao;
import net.web.message.entity.MessageInfo;
import net.web.message.entity.UserMessage;
import net.web.message.service.IMessageService;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Service("messageService")
public class MessageServiceImpl implements IMessageService {

	@Resource(name = "messageInfoDao")
	private IMessageInfoDao messageInfoDao;

	@Resource(name = "userMessageDao")
	private IUserMessageDao userMessageDao;

	@Override
	public void insertMessage(MessageInfo messageInfo) {
		messageInfoDao.insertMessage(messageInfo);
		List<String> users = messageInfo.getUsers();
		String platform = messageInfo.getPlatform();
		String expireTime = messageInfo.getExpireTime();
		List<UserMessage> list = new ArrayList<UserMessage>();
		for (String userId : users) {
			UserMessage userMessage = new UserMessage();
			userMessage.setPlatform(platform);
			userMessage.setMsgId(messageInfo.getId());
			userMessage.setUserId(userId);
			userMessage.setExpireTime(expireTime);
			userMessage.setStatus(UserMessage.MESSAGE_STATUS_UNREAD);
			list.add(userMessage);
		}
		userMessageDao.insertUserMessage(list);
	}

	@Override
	public List<MessageInfo> getUnReadMessage(String userId, String platform) {
		List<UserMessage> list = userMessageDao.getUnReadMessage(userId, platform);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		List<String> ids = new ArrayList<String>();
		for (UserMessage data : list) {
			ids.add(data.getMsgId());
		}
		return messageInfoDao.getMessageInfo(ids);
	}

	@Override
	public void readMessage(String userId, String platform, String msgId) {
		userMessageDao.updateUserMessageStatus(userId, platform, msgId, UserMessage.MESSAGE_STATUS_READ);
	}

}
