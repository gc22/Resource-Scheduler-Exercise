package testing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import external.interfaces.Gateway;
import external.interfaces.GatewayMessage;

/**
 *
 * @author garry.craig
 *
 */
public class TestGateway implements Gateway {
	public class MessageArrivedEvent{
		private LocalDateTime timestamp;
		private GatewayMessage message;

		public MessageArrivedEvent(LocalDateTime timestamp, GatewayMessage message){
			this.timestamp = timestamp;
			this.message = message;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		public GatewayMessage getMessage() {
			return message;
		}
	}

	private List<MessageArrivedEvent> listMessageArrivedEvents = new ArrayList<>();
	private int timeToProcessMessages = 1000;

	public TestGateway(int timeToProcessMessages) {
		this.timeToProcessMessages = timeToProcessMessages;
	}

	@Override
	public void send(GatewayMessage msg) {
		listMessageArrivedEvents.add(new MessageArrivedEvent(LocalDateTime.now(), msg));
		//System.out.println("Test Gateway :: send :: Received message " + msg + " count " + listMessageArrivedEvents.size());
		try {
			Thread.sleep(timeToProcessMessages);
		} catch (InterruptedException e) {
			//System.err.println("Test Gateway got error " + msg);
		}
		msg.completed();
	}

	public List<MessageArrivedEvent> getMessageArrivedEventList() {
		return this.listMessageArrivedEvents;
	}
}
