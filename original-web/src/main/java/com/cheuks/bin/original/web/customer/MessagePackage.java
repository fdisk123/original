package com.cheuks.bin.original.web.customer;

import java.io.Serializable;

/***
 * 数据包结构
 */
public class MessagePackage implements MessageOption, Serializable {

	private static final long serialVersionUID = 1L;

	private MessagePackageType type;// 功能类型:心跳，发送（消息跳转）。。。。
	private String psid;// psid;
	private SenderType senderType;// 发送人类型
	private String sender;// 发送都ID
	private String receiver;// 接收者ID
	private String questioner;// 发问者名字
	private String responder;// 回复者名字
	private String msgType;// 文字/图片
	private String msg;// 消息内容

	public MessagePackageType getType() {
		return type;
	}

	public MessagePackage setType(MessagePackageType type) {
		this.type = type;
		return this;
	}

	public String getPsid() {
		return psid;
	}

	public MessagePackage setPsid(String psid) {
		this.psid = psid;
		return this;
	}

	public SenderType getSenderType() {
		return senderType;
	}

	public MessagePackage setSenderType(SenderType senderType) {
		this.senderType = senderType;
		return this;
	}

	public String getSender() {
		return sender;
	}

	public MessagePackage setSender(String sender) {
		this.sender = sender;
		return this;
	}

	public String getReceiver() {
		return receiver;
	}

	public MessagePackage setReceiver(String receiver) {
		this.receiver = receiver;
		return this;
	}

	public String getQuestioner() {
		return questioner;
	}

	public MessagePackage setQuestioner(String questioner) {
		this.questioner = questioner;
		return this;
	}

	public String getResponder() {
		return responder;
	}

	public MessagePackage setResponder(String responder) {
		this.responder = responder;
		return this;
	}

	public String getMsgType() {
		return msgType;
	}

	public MessagePackage setMsgType(String msgType) {
		this.msgType = msgType;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public MessagePackage setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public MessagePackage(String psid, SenderType senderType, String sender, String receiver) {
		super();
		this.psid = psid;
		this.senderType = senderType;
		this.sender = sender;
		this.receiver = receiver;
	}

	public MessagePackage() {
		super();
	}

}
