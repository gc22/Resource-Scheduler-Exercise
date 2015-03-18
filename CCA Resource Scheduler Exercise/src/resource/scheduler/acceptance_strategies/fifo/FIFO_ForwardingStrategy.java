package resource.scheduler.acceptance_strategies.fifo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import external.interfaces.Gateway;
import external.interfaces.GatewayMessage;
import external.interfaces.GroupId;
import resource.scheduler.acceptance_strategies.grouped.GroupMessageWorker;
import resource.scheduler.interfaces.ForwardingStrategy;

public class FIFO_ForwardingStrategy implements
		ForwardingStrategy<GatewayMessage> {
	private static final int MAX_WORKER_QUEUE_SIZE = 1000;

	private BlockingQueue<GatewayMessage> messageQueue = null;
	private AtomicBoolean shutdownSwitch = new AtomicBoolean(false);
	private Gateway gateway = null;
	private Thread thread = new Thread(() -> {
		try {
			while(!shutdownSwitch.get()){
				gateway.send(this.messageQueue.take());
			}
			while(this.messageQueue.size() > 0){
				gateway.send(this.messageQueue.take());
			}
		}catch(InterruptedException exTwo){
		}
	});

	public FIFO_ForwardingStrategy(Gateway gateway) {
		this.gateway = gateway;
		this.messageQueue = new ArrayBlockingQueue<GatewayMessage>(MAX_WORKER_QUEUE_SIZE);
		this.thread.start();
	}

	@Override
	public void accept(GatewayMessage message) {
		messageQueue.add(message);
	}

	public void shutdown() {
		shutdownSwitch.set(true);
	}

	@Override
	public void cancel(GroupId<?> groupId) {
	}

	@Override
	public void awaitTermination(long timeout, TimeUnit unit) {
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}