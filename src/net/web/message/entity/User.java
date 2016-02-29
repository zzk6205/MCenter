package net.web.message.entity;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String userId;
	private String platform;
	private JSONObject properties;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public JSONObject getProperties() {
		return properties;
	}

	public void setProperties(JSONObject properties) {
		this.properties = properties;
	}

}
