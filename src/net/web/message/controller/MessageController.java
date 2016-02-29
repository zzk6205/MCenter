package net.web.message.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.web.base.entity.Message;
import net.web.message.entity.MessageInfo;
import net.web.message.entity.User;
import net.web.message.service.IMessageService;
import net.web.message.service.IUserService;
import net.web.websocket.WebSocketHandlerImpl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller("messageController")
@RequestMapping("/message")
public class MessageController {

	@Resource(name = "userService")
	private IUserService userService;

	@Resource(name = "messageService")
	private IMessageService messageService;

	@Bean
	public WebSocketHandlerImpl webSocketHandler() {
		return new WebSocketHandlerImpl();
	}

	@RequestMapping("/send")
	@ResponseBody
	public Message send(String target, String content, String type, String expireTime) {
		if (StringUtils.isEmpty(target) || StringUtils.isEmpty(content)) {
			return Message.error("参数无效！");
		}

		// 解析target参数，获取要发送消息的对象
		Map<String, Object> params = new HashMap<String, Object>();
		JSONObject json = JSONObject.parseObject(target);
		Iterator<Map.Entry<String, Object>> iterator = json.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof JSONArray) {
				params.put(key, Arrays.asList(((JSONArray) value).toArray()));
			} else if (value instanceof String) {
				params.put(key, value);
			} else {
				return Message.error("参数无效！");
			}
		}
		List<User> userList = userService.getUserList(params);

		if (CollectionUtils.isEmpty(userList)) {
			return Message.error("找不到用户信息！");
		}

		Collections.sort(userList, new Comparator<User>() {
			public int compare(User user0, User user1) {
				return user0.getPlatform().compareTo(user1.getPlatform());
			}
		});

		// 消息处理
		String platform = userList.get(0).getPlatform();
		List<String> userIds = new ArrayList<String>();

		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (!platform.equals(user.getPlatform())) {
				// 发送并保存消息至数据库
				sendAndSaveMessage(userIds, platform, content, type, expireTime);

				platform = user.getPlatform();
				userIds = new ArrayList<String>();
			}

			userIds.add(user.getUserId());

			if ((i + 1) >= userList.size()) {
				// 发送并保存消息至数据库
				sendAndSaveMessage(userIds, platform, content, type, expireTime);
			}
		}

		return Message.success("发送成功！");
	}

	private void sendAndSaveMessage(List<String> userIds, String platform, String content, String type, String expireTime) {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setUsers(userIds);
		messageInfo.setPlatform(platform);
		messageInfo.setContent(content);
		messageInfo.setType(type);
		messageInfo.setExpireTime(expireTime);
		messageService.insertMessage(messageInfo);

		JSONObject json = (JSONObject) JSONObject.toJSON(messageInfo);
		json.remove("users");
		json.remove("platform");
		webSocketHandler().sendMessage(userIds, platform, new TextMessage(json.toJSONString()));
	}

}