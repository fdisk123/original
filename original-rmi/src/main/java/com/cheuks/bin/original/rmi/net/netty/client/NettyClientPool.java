package com.cheuks.bin.original.rmi.net.netty.client;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheuks.bin.original.common.rmi.LoadBalanceFactory;
import com.cheuks.bin.original.common.rmi.RmiClient;
import com.cheuks.bin.original.common.rmi.model.ConsumerValueModel;
import com.cheuks.bin.original.common.rmi.model.RegisterLoadBalanceModel;
import com.cheuks.bin.original.common.rmi.model.RegisterLoadBalanceModel.ServiceType;
import com.cheuks.bin.original.common.rmi.net.NetworkClient;
import com.cheuks.bin.original.common.util.AbstractObjectPool;
import com.cheuks.bin.original.rmi.config.RmiConfigArg;
import com.cheuks.bin.original.rmi.net.netty.SimpleChannelFutureListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class NettyClientPool extends AbstractObjectPool<NettyClientHandle, InetSocketAddress> implements RmiClient<String, Void, NetworkClient<Bootstrap, NettyClientHandle, InetSocketAddress, String, Void, RmiConfigArg, Boolean, Channel>, RmiConfigArg> {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleNettyClient.class);

	private LoadBalanceFactory<String, Void> loadBalanceFactory;

	private NetworkClient<Bootstrap, NettyClientHandle, InetSocketAddress, String, Void, RmiConfigArg, Boolean, Channel> nettyClient;

	private RmiConfigArg rmiConfigArg;

	/***
	 * 默认连接处理数,默认CPU*2
	 */
	private int availableProcessors;

	// 连接任务
	private Thread connectionWorker = null;

	private final BlockingQueue<ConsumerValueModel> connectionQueue = new LinkedBlockingQueue<ConsumerValueModel>();

	public NettyClientPool() {
	}

	public void cancleRegistration(String clientName) throws Throwable {
		// String clientCode = sessionInfo.get(channel.id());
		if (null != clientName) {
			RegisterLoadBalanceModel registerLoadBalanceModel = new RegisterLoadBalanceModel();
			registerLoadBalanceModel.setType(ServiceType.client);
			registerLoadBalanceModel.setServerName(clientName);
			loadBalanceFactory.cancleRegistration(registerLoadBalanceModel);
		}
	}

	@SuppressWarnings("static-access")
	public void start() throws NumberFormatException, IllegalStateException, UnsupportedOperationException, InterruptedException, Exception {
		if (null == connectionWorker || connectionWorker.interrupted()) {
			if (null == loadBalanceFactory)
				loadBalanceFactory = nettyClient.getLoadBalanceFactory();
			// 线程平均分配
			int a = rmiConfigArg.getProtocolModel().getNetWorkThreads();
			a = a > 0 ? a : Runtime.getRuntime().availableProcessors() / rmiConfigArg.getReferenceGroup().getReferenceGroup().size();
			availableProcessors = a;
			// 添加服务
			// for (Entry<String, ReferenceGroupModel> en : rmiConfigArg.getReferenceGroup().getReferenceGroup().entrySet()) {
			// for (int i = 0; i < threadCount; i++) {
			// connectionQueue.add(new ConsumerValueModel(name = rmiConfigArg.getProtocolModel().getLocalName() + "_" + en.getKey() + "_" + i, en.getKey()).setConsumerUrl(rmiConfigArg.getProtocolModel().getLocalAddress()));
			// // 记录服务名，对应的服务信息（断线重连时使用）
			// connectionInfo.put(name, en.getKey());
			// }
			// }

			// 连接生成数
			for (int i = 0; i < availableProcessors; i++) {
				connectionQueue.add(new ConsumerValueModel(rmiConfigArg.getProtocolModel().getLocalName() + "_" + getPoolName() + "_" + i, getPoolName()));
			}
			connectionWorker = new Thread(new Runnable() {
				public void run() {
					try {
						while (true) {
							connect(connectionQueue.take());
						}
					} catch (Throwable e) {
						LOG.error(null, e);
					}
				}
			});
			connectionWorker.start();
		}
	}

	public void addConnectionByServerName(String serviceName, String clientName) {
		connectionQueue.add(new ConsumerValueModel(clientName, serviceName));
	}
	public void addConnectionByServerName(ConsumerValueModel consumerValueModel) {
		connectionQueue.add(consumerValueModel);
	}

	private void connect(ConsumerValueModel consumerValueModel) throws Throwable {
		try {
			RegisterLoadBalanceModel registerLoadBalanceModel = new RegisterLoadBalanceModel();
			// 拥有的服务
			registerLoadBalanceModel.setServiceName(consumerValueModel.getServiceName());
			// 服务器主机名
			registerLoadBalanceModel.setServerName(consumerValueModel.getConsumerName());
			registerLoadBalanceModel.setType(ServiceType.client);
			// 健康
			// registerLoadBalanceModel.setHealthCheck(rmiConfigArg.getProtocolModel().getLocalAddress() + ":" + rmiConfigArg.getProtocolModel().getPort());

			// String address = loadBalanceFactory.getResourceAndUseRegistration(registerLoadBalanceModel);
			List<String> address = loadBalanceFactory.getResource(registerLoadBalanceModel);
			if (LOG.isDebugEnabled())
				LOG.debug("资源服务器地址：{}", address);
			if (null == address || address.isEmpty())
				throw new Throwable("not service resource online.");
			String[] tempServerInfo = address.get(0).split("@");
			String[] addresses = tempServerInfo[1].split(":");
			consumerValueModel.setServerName(tempServerInfo[0]).setServerUrl(tempServerInfo[1]);

			InetSocketAddress inetSocketAddress = new InetSocketAddress(addresses[0], Integer.valueOf(addresses[1]));
			nettyClient.getClient().connect(inetSocketAddress).addListener(new SimpleChannelFutureListener(nettyClient, consumerValueModel)).sync().channel();
		} catch (InterruptedException e) {
			LOG.error(null, e);
		}
	}

	@Override
	public void invalidateReBuildObject(int count) throws Exception {
		// 一共过期N个对象
		if (LOG.isDebugEnabled())
			LOG.debug("连接池{}:一共过期{}个对象。", getPoolName(), count);
	}

	public void invalidateObject(NettyClientHandle t) throws Exception {
		destroyObject(t);
	}

	public void destroyObject(NettyClientHandle t) throws Exception {
		ChannelHandlerContext ctx = t.getObject().getChannelHandlerContext();
		ctx.close();
		ctx.channel().close();
	}

	@Override
	public synchronized void removeObject(NettyClientHandle t) throws Exception {
		super.removeObject(t);
	}

	public LoadBalanceFactory<String, Void> getLoadBalanceFactory() {
		return loadBalanceFactory;
	}

	public NettyClientPool setLoadBalanceFactory(LoadBalanceFactory<String, Void> loadBalanceFactory) {
		this.loadBalanceFactory = loadBalanceFactory;
		return this;
	}

	public RmiConfigArg getRmiConfigArg() {
		return rmiConfigArg;
	}

	public NettyClientPool setRmiConfigArg(RmiConfigArg rmiConfigArg) {
		this.rmiConfigArg = rmiConfigArg;
		return this;
	}

	public NettyClientPool setNettyClient(NetworkClient<Bootstrap, NettyClientHandle, InetSocketAddress, String, Void, RmiConfigArg, Boolean, Channel> client) {
		this.nettyClient = client;
		return this;
	}

	public NettyClientPool(int poolSize, String serviceName) {
		super(poolSize, serviceName);
	}

	public NettyClientPool(String serviceName) {
		super(serviceName);
	}

	public RmiClient<String, Void, NetworkClient<Bootstrap, NettyClientHandle, InetSocketAddress, String, Void, RmiConfigArg, Boolean, Channel>, RmiConfigArg> setServiceName(String serviceName) {
		setPoolName(serviceName);
		return this;
	}

	public boolean isFailure(NettyClientHandle t) throws Exception {
		return !t.getChannelHandlerContext().channel().isActive();
	}

	public NettyClientPool(NetworkClient<Bootstrap, NettyClientHandle, InetSocketAddress, String, Void, RmiConfigArg, Boolean, Channel> nettyClient, LoadBalanceFactory<String, Void> loadBalanceFactory, RmiConfigArg rmiConfigArg, int availableProcessors, String serviceName) {
		this.nettyClient = nettyClient;
		this.loadBalanceFactory = loadBalanceFactory;
		this.rmiConfigArg = rmiConfigArg;
		this.availableProcessors = availableProcessors;
		setPoolName(serviceName);
	}

}