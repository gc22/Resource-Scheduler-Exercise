package testing.unit;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import external.interfaces.GatewayMessage;
import resource.scheduler.acceptance_strategies.grouped.GroupMessageWorker;
import testing.TestGateway;
import testing.TestMessage;

public class ThreadPoolExecutorTester {

	public static void main(String[] args) {
		TestGateway gateway = new TestGateway(1);

		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r);
			}
		};
		ThreadPoolExecutor p = new ThreadPoolExecutor(100, 100, 1000, TimeUnit.HOURS, workQueue, threadFactory);
		Consumer<GatewayMessage> c = (e)->{
			p.submit(new GroupMessageWorker(gateway, e, 1000));
		};

		for (int i = 0; i < 1000; i++) {
			try{
				c.accept(new TestMessage(i));
			}catch(java.util.concurrent.RejectedExecutionException rejected){
				System.out.println("Rejecting new messages at " + i);
			}
		}
	}

}
