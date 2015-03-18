package resource.scheduler.acceptance_strategies.grouped;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import external.interfaces.Gateway;
import external.interfaces.GatewayMessage;
import external.interfaces.GroupId;
import resource.scheduler.interfaces.ForwardingStrategy;

public class GroupedForwardingStrategy implements ForwardingStrategy<GatewayMessage>{
	private static final int MAX_WORKER_QUEUE_SIZE = 1000;
	private static final int START_DEFAULT_WORKER_THREAD_COUNT_NUMBER = 1000;
	private static final int MAX_DEFAULT_WORKER_THREAD_COUNT_NUMBER = 1000;

	private BlockingQueue<Runnable> workQueue = null;
	private ThreadFactory threadFactory = null;
	private ThreadPoolExecutor pool = null;
	private Map<Object, GroupMessageWorker> workerMap;
	private Gateway gateway = null;
	private Lock gatewayLock;

	public GroupedForwardingStrategy(Gateway gateway) {
		this.gateway = gateway;
		this.workQueue = new ArrayBlockingQueue<Runnable>(MAX_WORKER_QUEUE_SIZE);
		this.threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r);
			}
		};
		this.pool = new ThreadPoolExecutor(START_DEFAULT_WORKER_THREAD_COUNT_NUMBER, MAX_DEFAULT_WORKER_THREAD_COUNT_NUMBER, 1000, TimeUnit.MINUTES, workQueue, threadFactory);
		this.gatewayLock = new ReentrantLock();
		this.workerMap = new HashMap<>();
	}

	@Override
	public void accept(GatewayMessage message) {
		Object key = message.getGroupId().getInternalId();
		if(workerMap.containsKey(key)){
			//System.out.println("ResourceScheduler :: accept :: Contains key " + key);
			GroupMessageWorker worker = workerMap.get(key);
			worker.addJob(message);
		}else{
			//System.out.println("ResourceScheduler :: accept :: New key " + key);
			GroupMessageWorker worker = new GroupMessageWorker(gatewayLock, gateway, message, MAX_WORKER_QUEUE_SIZE);
			workerMap.put(key, worker);
			pool.submit(worker);
		}
	}

	public void shutdown(){
		//System.out.println("ResourceScheduler :: shutdown :: approx number of workers in pool " + pool.getTaskCount());
		pool.shutdown();
	}

	@Override
	public void cancel(GroupId<?> groupId) {
		Object key = groupId.getInternalId();
		if(workerMap.containsKey(key)){
			//System.out.println("ResourceScheduler :: cancel :: Contains key " + key);
			workerMap.get(key).cancel();
		}else{
			//System.out.println("ResourceScheduler :: cancel :: Cannot cancel key doesn't exist! " + key);
		}
	}

	@Override
	public void awaitTermination(long timeout, TimeUnit unit) {
		try {
			pool.awaitTermination(timeout, unit);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}