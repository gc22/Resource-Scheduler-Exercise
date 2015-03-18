/**
 *
 */
package testing.unit.tests.fifo_forwarding.cases;

import static org.junit.Assert.*;

import java.net.PasswordAuthentication;
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
 * This test sends a single message to the gateway and makes sure it is received on the other side.
 *
 */
public class UnitTest_FIFO_ForwardingStrategy_SingleMessage {
	private ResourceScheduler resourceScheduler;
	private ForwardingStrategy<GatewayMessage> forwardingStrategy;
	private TestGateway gateway;

	private static final int timeGatewayTakesToProcessMessage = 1;
	private static final int expectedNumberOfMessagesExpected = 1;
	private static final int expectedGroupId = 1;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		gateway = new TestGateway(timeGatewayTakesToProcessMessage);
		forwardingStrategy = new GroupedForwardingStrategy(gateway);
		resourceScheduler = new ResourceScheduler(forwardingStrategy);
		resourceScheduler.accept(new TestMessage(expectedGroupId));
		resourceScheduler.shutdown();
		resourceScheduler.awaitTermination(1000, TimeUnit.DAYS);
		//displayEventList();
	}

	private void displayEventList() {
		Queue<MessageArrivedEvent> eventList = gateway.getMessageArrivedEventQueue();
		eventList.forEach((msg)->{
			System.out.println(msg.getTimestamp() + " :: " + msg.getMessage());
		});
	}

	@Test
	public void checkMessageOrder() {
		checkNumberOfArrivedEventsSize();
		Queue<MessageArrivedEvent> eventList = gateway.getMessageArrivedEventQueue();
		if(eventList.size() > 0){
			assertEquals("First Msg Group Id Test", eventList.element().getMessage().getGroupId().getInternalId(), expectedGroupId);
		}
	}
	
	private void checkNumberOfArrivedEventsSize(){
		assertEquals("Size of Msg Received List", expectedNumberOfMessagesExpected, gateway.getMessageArrivedEventQueue().size());
	}

}
