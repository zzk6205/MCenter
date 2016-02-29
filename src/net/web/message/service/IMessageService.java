package net.web.message.service;

import java.util.List;

import net.web.message.entity.MessageInfo;

public interface IMessageService {

	public void insertMessage(MessageInfo info);

	public List<MessageInfo> getUnReadMessage(String userId, String platform);

	public void readMessage(String userId, String platform, String msgId);

}
