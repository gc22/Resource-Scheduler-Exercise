/**
 *
 */
package testing.unit.tests.group_forwarding.cases;

import static org.junit.Assert.*;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import external.interfaces.Gateway;
import external.interfaces.GatewayMessage;
import resource.scheduler.ResourceScheduler;
import resource.scheduler.acceptance_strategies.grouped.GroupedForwardingStrategy;
import resource.scheduler.interfaces.ForwardingStrategy;
import testing.TestGateway;
import testing.TestGateway.MessageArrivedEvent;
import testing.TestMessage;

/**
 * @author garry.craig
 *
 */
public class UnitTest_GroupForwardingStrategy_QueueLimitReached {

	private ResourceScheduler resourceScheduler;
	private GroupedForwardingStrategy forwardingStrategy;
	private TestGateway gateway;

	private static final int timeGatewayTakesToProcessMessage = 1;
	

	@SuppressWarnings("serial")
	private static final List<Integer> groupIds = new ArrayList<Integer>(){{
		add(1);
		add(2);
		add(3);
		add(4);
	}};
	
	private static final int numberOfResourcesAvailable = 1;
	private static final int numberOfMessagesSentByEachGroup = 500;
	private static final int expectedNumberOfMessagesExpected = 397;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		gateway = new TestGateway(timeGatewayTakesToProcessMessage);
		forwardingStrategy = new GroupedForwardingStrategy(gateway, numberOfResourcesAvailable, 500);
		forwardingStrategy.setMaxMessageQueueLength(100);
		
		resourceScheduler = new ResourceScheduler(forwardingStrategy);

		for(int count = 0; count < numberOfMessagesSentByEachGroup; count++){
			for(int id : groupIds){
				resourceScheduler.accept(new TestMessage(id));
			};
		}

		resourceScheduler.shutdown();
		resourceScheduler.awaitTermination(1000, TimeUnit.DAYS);
		displayEventList();
	}

	private void displayEventList() {
		Queue<MessageArrivedEvent> eventQueue = gateway.getMessageArrivedEventQueue();
		for(MessageArrivedEvent event : eventQueue){
			System.out.println(event.getTimestamp() + " :: " + event.getMessage());
		}
	}

	@Test
	public void checkNumberOfArrivedEventsSize() {
		assertEquals("Size of Msg Received List", expectedNumberOfMessagesExpected, gateway.getMessageArrivedEventQueue().size());
	}
}
