package net.web.websocket;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import net.web.message.entity.MessageInfo;
import net.web.message.entity.User;
import net.web.message.service.IMessageService;
import net.web.message.service.IUserService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;

public class WebSocketHandlerImpl implements WebSocketHandler {

	private static final Logger logger;
	private static final ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>> users;

	@Resource(name = "userService")
	private IUserService userService;

	@Resource(name = "messageService")
	private IMessageService messageService;

	static {
		users = new ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>>();
		logger = LoggerFactory.getLogger(WebSocketHandlerImpl.class);
	}

	private String getUserId(WebSocketSession session) {
		Map<String, Object> attr = session.getAttributes();
		if (attr.containsKey(WebSocketConstants.WEBSOCKET_KEY_USER_ID)) {
			return (String) session.getAttributes().get(WebSocketConstants.WEBSOCKET_KEY_USER_ID);
		}
		return null;
	}

	private String getPlatform(WebSocketSession session) {
		Map<String, Object> attr = session.getAttributes();
		if (attr.containsKey(WebSocketConstants.WEBSOCKET_KEY_PLATFORM)) {
			return (String) session.getAttributes().get(WebSocketConstants.WEBSOCKET_KEY_PLATFORM);
		}
		return null;
	}

	private JSONObject getUserInfo(WebSocketSession session) {
		Map<String, Object> attr = session.getAttributes();
		if (attr.containsKey(WebSocketConstants.WEBSOCKET_KEY_USER_INFO)) {
			return (JSONObject) session.getAttributes().get(WebSocketConstants.WEBSOCKET_KEY_USER_INFO);
		}
		return null;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.debug("connect to the websocket success......");

		String userId = getUserId(session);
		String platform = getPlatform(session);

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(platform)) {
			return;
		}

		if (users.containsKey(platform)) {
			ConcurrentHashMap<String, WebSocketSession> userIdHashMap = users.get(platform);
			if (userIdHashMap.containsKey(userId)) {
				WebSocketSession origSession = userIdHashMap.get(userId);
				if (origSession.isOpen()) {
					origSession.close();
				}
				userIdHashMap.remove(userId);
			}
			userIdHashMap.put(userId, session);
		} else {
			ConcurrentHashMap<String, WebSocketSession> userIdHashMap = new ConcurrentHashMap<String, WebSocketSession>();
			userIdHashMap.put(userId, session);
			users.put(platform, userIdHashMap);
		}

		// 保存用户信息
		User user = new User();
		user.setUserId(getUserId(session));
		user.setPlatform(getPlatform(session));
		user.setProperties(getUserInfo(session));
		userService.saveUser(user);

		// 发送未读消息
		List<MessageInfo> messageList = messageService.getUnReadMessage(userId, platform);
		if (!CollectionUtils.isEmpty(messageList) && session.isOpen()) {
			for (MessageInfo messageInfo : messageList) {
				JSONObject json = (JSONObject) JSONObject.toJSON(messageInfo);
				json.remove("users");
				json.remove("platform");
				session.sendMessage(new TextMessage(json.toJSONString()));
			}
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		Object payload = message.getPayload();
		if (payload instanceof String) {
			String msgText = (String) payload;
			JSONObject msgJson = null;
			try {
				msgJson = JSONObject.parseObject(msgText);
			} catch (Exception e) {
				System.out.println("非自定义消息，忽略.");
			}
			if (msgJson != null && "feedback".equals(msgJson.getString("type"))) {
				String msgId = msgJson.getString("msgId");
				String userId = getUserId(session);
				String platform = getPlatform(session);
				messageService.readMessage(userId, platform, msgId);
			}
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		String userId = getUserId(session);
		String platform = getPlatform(session);

		if (session.isOpen()) {
			session.close();
		}

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(platform)) {
			return;
		}

		if (users.containsKey(platform)) {
			ConcurrentHashMap<String, WebSocketSession> userIdHashMap = users.get(platform);
			if (userIdHashMap.containsKey(userId)) {
				userIdHashMap.remove(userId);
			}
			if (userIdHashMap.size() <= 0) {
				users.remove(platform);
			}
		}
		if (session.isOpen()) {
			session.close();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		String userId = getUserId(session);
		String platform = getPlatform(session);

		if (session.isOpen()) {
			session.close();
		}

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(platform)) {
			return;
		}

		if (users.containsKey(platform)) {
			ConcurrentHashMap<String, WebSocketSession> userIdHashMap = users.get(platform);
			if (userIdHashMap.containsKey(userId)) {
				userIdHashMap.remove(userId);
			}
			if (userIdHashMap.size() <= 0) {
				users.remove(platform);
			}
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	public void sendMessage(List<String> userIds, String platform, TextMessage message) {
		if (CollectionUtils.isEmpty(userIds)) {
			return;
		}
		ConcurrentHashMap<String, WebSocketSession> sessionHashMap = null;
		if (!StringUtils.isEmpty(platform)) {
			if (users.containsKey(platform)) {
				sessionHashMap = users.get(platform);
				for (String userId : userIds) {
					if (sessionHashMap.containsKey(userId)) {
						WebSocketSession session = sessionHashMap.get(userId);
						if (session.isOpen()) {
							try {
								session.sendMessage(message);
							} catch (IOException e) {
								logger.error("sendMessage error. userId:{}, platform:{}, message:{}", userId, platform, message, e);
							}
						}
					}
				}
			}
		} else {
			Iterator<Map.Entry<String, ConcurrentHashMap<String, WebSocketSession>>> iterator = users.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, ConcurrentHashMap<String, WebSocketSession>> entry = iterator.next();
				sessionHashMap = entry.getValue();
				for (String userId : userIds) {
					if (sessionHashMap.containsKey(userId)) {
						WebSocketSession session = sessionHashMap.get(userId);
						if (session.isOpen()) {
							try {
								session.sendMessage(message);
							} catch (IOException e) {
								logger.error("sendMessage error. userId:{}, platform:{}, message:{}", userId, platform, message, e);
							}
						}
					}
				}
			}
		}
	}

}