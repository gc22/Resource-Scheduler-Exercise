package resource.scheduler;

import java.util.concurrent.TimeUnit;

import resource.scheduler.acceptance_strategies.grouped.GroupedForwardingStrategy;
import resource.scheduler.interfaces.ForwardingStrategy;
import external.interfaces.Gateway;
import external.interfaces.GroupId;
import external.interfaces.GatewayMessage;

/**
 *
 * @author garry.craig
 *
 */
public class ResourceScheduler implements ForwardingStrategy<GatewayMessage>{

	private ForwardingStrategy<GatewayMessage> forwardingStrategy = null;

	/**
	 *
	 * @param acceptanceStrategy
	 */
	public ResourceScheduler(ForwardingStrategy<GatewayMessage> forwardingStrategy){
		this.forwardingStrategy = forwardingStrategy;
	}

	/**
	 *
	 * @param gateway
	 */
	public ResourceScheduler(Gateway gateway){
		this.forwardingStrategy = new GroupedForwardingStrategy(gateway);
	}

	@Override
	public void accept(GatewayMessage message) {
		this.forwardingStrategy.accept(message);
	}

	@Override
	public void shutdown() {
		this.forwardingStrategy.shutdown();
	}

	@Override
	public void cancel(GroupId<?> groupId) {
		this.forwardingStrategy.cancel(groupId);
	}

	@Override
	public void awaitTermination(long timeout, TimeUnit unit) {
		this.forwardingStrategy.awaitTermination(timeout, unit);
	}
}
