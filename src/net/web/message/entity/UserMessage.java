package net.web.message.entity;

import java.io.Serializable;

public class UserMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE_STATUS_UNREAD = "0";
	public static final String MESSAGE_STATUS_READ = "1";

	private String id;
	private String msgId;
	private String userId;
	private String platform;
	private String expireTime;
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
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

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
