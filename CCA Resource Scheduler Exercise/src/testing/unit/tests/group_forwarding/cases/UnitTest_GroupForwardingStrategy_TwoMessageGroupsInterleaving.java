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
public class UnitTest_GroupForwardingStrategy_TwoMessageGroupsInterleaving {

	private ResourceScheduler resourceScheduler;
	private ForwardingStrategy<GatewayMessage> forwardingStrategy;
	private TestGateway gateway;

	private static final int timeGatewayTakesToProcessMessage = 1;
	private static final int expectedNumberOfMessagesExpected = 10;
	private static final int numberOfMessagesSentByEachGroup = 5;
	private static final int numberOfResourcesAvailable = 1;

	@SuppressWarnings("serial")
	private static final List<Integer> groupIds = new ArrayList<Integer>(){{
		add(1);
		add(2);
	}};

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		gateway = new TestGateway(timeGatewayTakesToProcessMessage);
		forwardingStrategy = new GroupedForwardingStrategy(gateway, numberOfResourcesAvailable);
		resourceScheduler = new ResourceScheduler(forwardingStrategy);

		for(int count = 0; count < numberOfMessagesSentByEachGroup; count++){
			groupIds.forEach((id)->{
				resourceScheduler.accept(new TestMessage(id));
			});
		}

		resourceScheduler.shutdown();
		resourceScheduler.awaitTermination(1000, TimeUnit.DAYS);
		//displayEventList();
	}

	/**
	 * 
	 */
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
		if(eventList.size() ==  expectedNumberOfMessagesExpected){
			int msgNum = 0;
			for(MessageArrivedEvent event : gateway.getMessageArrivedEventQueue()){
				GatewayMessage msg = event.getMessage();				
				if(msgNum < numberOfMessagesSentByEachGroup){
					assertEquals("Msg Group Id First Group Test", msg.getGroupId().getInternalId(), groupIds.get(0));
				}else{
					assertEquals("Msg Group Id Second Group Test", msg.getGroupId().getInternalId(), groupIds.get(1));
				}
				msgNum++;
			}
		}
	}
	
	private void checkNumberOfArrivedEventsSize(){
		assertEquals("Size of Msg Received List", expectedNumberOfMessagesExpected, gateway.getMessageArrivedEventQueue().size());
	}
}
