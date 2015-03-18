package resource.scheduler.interfaces;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

import external.interfaces.Gateway;
import external.interfaces.GatewayMessage;

/**
 *
 * @author garry.craig
 *
 */
public class FIFO_MessageWorker implements Runnable {
	private BlockingQueue<GatewayMessage> messageQueue = null;
	private GatewayMessage seedMessage = null;
	private Gateway gateway = null;
	private Lock gatewayLock = null;

	public FIFO_MessageWorker(Lock gatewayLock, Gateway gateway, GatewayMessage seedMessage, int maxWorkerQueueSize) {
		this.messageQueue = new ArrayBlockingQueue<GatewayMessage>(maxWorkerQueueSize);
		this.gateway = gateway;
		this.gatewayLock  = gatewayLock;
		this.seedMessage = seedMessage;
		this.messageQueue.add(seedMessage);
	}

	@Override
	public void run() {
		gatewayLock.lock();
		//System.out.println("MsgWorker :: " + Thread.currentThread().getName() + " Number of messages in queue " + messageQueue.size());
		if(messageQueue.size() != 0){
			try {
				//System.out.println("MsgWorker :: " + Thread.currentThread().getName() + " sending message " + seedMessage);
				gateway.send(messageQueue.take());
				//System.out.println("MsgWorker :: " + Thread.currentThread().getName() + " completed send message " + seedMessage);
			} catch (Exception e) {
				System.out.println(e);
			}
			//System.out.println("MsgWorker :: " + Thread.currentThread().getName() + " Number of messages in queue " + messageQueue.size());
		}
		gatewayLock.unlock();
	}

	public GatewayMessage getSeedMessage() {
		return seedMessage;
	}

	public void addJob(GatewayMessage message) {
		//System.out.println(Thread.currentThread().getName() + " adding new Message :: " + message);
		this.messageQueue.add(message);
	}

	public void cancel() {
		messageQueue.clear();
	}
}