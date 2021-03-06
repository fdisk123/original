package com.cheuks.bin.original.reflect.rmi.net.netty;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.cheuks.bin.original.reflect.rmi.net.MessageCallBack;

import io.netty.channel.SimpleChannelInboundHandler;

/***
 * 客户端消息处理器适配器
 * 
 * @author ben
 *
 * @param <Input>
 * @param <Value>
 */
public abstract class NettyClientMessageHandleAdapter<Input, Value> extends SimpleChannelInboundHandler<Value> implements MessageCallBack<Input, Value> {

	private final BlockingDeque<Value> RESULT = new LinkedBlockingDeque<Value>(1);

	public void setResult(final Value value) {
		RESULT.add(value);
	}

	public Value callBack() throws InterruptedException {
		return RESULT.poll(60, TimeUnit.SECONDS);
	}
}
