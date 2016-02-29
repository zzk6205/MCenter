package net.web.message.dao;

import java.util.List;

import net.web.message.entity.MessageInfo;

public interface IMessageInfoDao {

	public MessageInfo insertMessage(MessageInfo info);

	public List<MessageInfo> getMessageInfo(List<String> ids);

}
