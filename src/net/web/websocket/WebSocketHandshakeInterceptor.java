package net.web.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.alibaba.fastjson.JSONObject;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			String user = servletRequest.getServletRequest().getParameter("user");
			if (!StringUtils.isEmpty(user)) {
				JSONObject userJson = JSONObject.parseObject(user);
				String userId = userJson.getString("userId");
				String platform = userJson.getString("platform");
				if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(platform)) {
					attributes.put(WebSocketConstants.WEBSOCKET_KEY_USER_ID, userId);
					attributes.put(WebSocketConstants.WEBSOCKET_KEY_PLATFORM, platform);
					attributes.put(WebSocketConstants.WEBSOCKET_KEY_USER_INFO, userJson);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}

}