package resource.scheduler.acceptance_strategies.grouped;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import external.interfaces.Gateway;
import external.interfaces.GatewayMessage;
import external.interfaces.GroupId;

/**
 *
 * @author garry.craig
 *
 */
public class GroupMessageWorker implements Runnable {
	private BlockingQueue<GatewayMessage> messageQueue = null;
	private GatewayMessage seedMessage = null;
	private Gateway gateway = null;
	private Thread workerThread = null;
	private int maxWorkerQueueSize;
	private GatewayMessage shutdownMessage = new GatewayMessage() {
		@Override
		public GroupId<?> getGroupId() {
			return new GroupId<Integer>() {
				private Integer shutdownNumber = -1;
				
				@Override
				public int compareTo(GroupId<Integer> other) {
					return Integer.compare(shutdownNumber, other.getInternalId());
				}

				@Override
				public Integer getInternalId() {
					return shutdownNumber;
				}
			};
		}
		
		@Override
		public void completed() {}
	};

	public GroupMessageWorker(Gateway gateway, GatewayMessage seedMessage, int maxWorkerQueueSize) {
		this.gateway = gateway;
		this.seedMessage = seedMessage;
		this.messageQueue = new ArrayBlockingQueue<GatewayMessage>(maxWorkerQueueSize);
		this.messageQueue.add(seedMessage);
	}

	@Override
	public void run() {
		System.out.println("GroupMessageWorker Started! " + this.seedMessage.getGroupId().getInternalId() + " :: " + this.hashCode());
		
		this.workerThread  = Thread.currentThread();		
		processUntilShutdown();
		processRemaining();

		System.out.println("GroupMessageWorker Finished! " + this.seedMessage.getGroupId().getInternalId() + " :: " + this.hashCode());
	}
	
	private void processUntilShutdown() {
		try {
			GatewayMessage msg = null;
			do{			
				System.out.println("Waiting on job");
				msg = messageQueue.take();
				System.out.println("Taken job");
				if(msg != shutdownMessage){
					gateway.send(msg);
				}
			}while(msg != shutdownMessage);
		} catch (InterruptedException e) {
			System.out.println("Error in GroupMessageWorker processUntilShutdown:: " + e);
		}
	}

	private void processRemaining(){
		while(this.messageQueue.size() != 0){
			try {
				GatewayMessage msg = messageQueue.take();
				if(msg != shutdownMessage){
					gateway.send(msg);
				}
			} catch (InterruptedException e) {
				System.out.println("Error in GroupMessageWorker processRemaining:: " + e);
			}
		}
	}
	
	public GatewayMessage getSeedMessage() {
		return seedMessage;
	}

	public void addJob(GatewayMessage message) {
		System.out.println("Adding job");
		if(this.messageQueue != null){
			if(this.messageQueue.remainingCapacity() > 1){
				//System.out.println(Thread.currentThread().getName() + " adding new Message :: " + message);
				this.messageQueue.add(message);
			}else{
				System.err.println("GroupMessageWorker's queue is full" + this.workerThread);
			}
		}
	}

	public void cancel() {
		messageQueue.clear();
		shutdown();
	}
	
	public void shutdown() {
		this.messageQueue.add(shutdownMessage);
	}
}