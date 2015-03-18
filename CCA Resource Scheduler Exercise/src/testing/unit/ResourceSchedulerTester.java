package testing.unit;

import java.util.List;
import java.util.concurrent.TimeUnit;

import external.interfaces.GatewayMessage;
import resource.scheduler.ResourceScheduler;
import resource.scheduler.acceptance_strategies.fifo.FIFO_ForwardingStrategy;
import resource.scheduler.interfaces.ForwardingStrategy;
import testing.TestGateway;
import testing.TestGateway.MessageArrivedEvent;
import testing.TestMessage;

public class ResourceSchedulerTester {

	public static void main(String[] args) {
		TestGateway gateway = new TestGateway(1);
		//test(gateway, new ResourceScheduler(gateway));
		test(gateway, new ResourceScheduler(new FIFO_ForwardingStrategy(gateway)));


		System.out.println("Main Finished!");
	}

	public static void test(TestGateway gateway, ResourceScheduler resourceScheduler){
		sendMessages(resourceScheduler, 100);

		resourceScheduler.cancel(new TestMessage(9).getGroupId());
		resourceScheduler.cancel(new TestMessage(10).getGroupId());

		resourceScheduler.shutdown();

		doTest(resourceScheduler, 5000);

		resourceScheduler.awaitTermination(1000, TimeUnit.DAYS);

		gateway.getMessageArrivedEventList().forEach((item)->System.out.println(item.getTimestamp() + " :: " + item.getMessage()));
	}

	public static void sendMessages(ResourceScheduler resourceScheduler, int numberOfMessages){
		for (int i = 0; i < numberOfMessages; i++) {
			doTest(resourceScheduler, i);
			if(i < 10){
				doTest(resourceScheduler, 0);
			}

			if(i > 50 && i < 60){
				doTest(resourceScheduler, 1);
			}
		}
	}

	public static void doTest(ResourceScheduler resourceScheduler, int i){
		try{
			resourceScheduler.accept(new TestMessage(i));
		}catch(java.util.concurrent.RejectedExecutionException rejected){
			System.err.println("Rejecting new messages at " + i);
		}
	}
}
