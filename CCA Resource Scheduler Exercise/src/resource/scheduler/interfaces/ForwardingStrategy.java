package resource.scheduler.interfaces;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import external.interfaces.GroupId;

/**
 *
 * @author garry.craig
 *
 * @param <T>
 */
public interface ForwardingStrategy<T> extends Consumer<T> {
	public void shutdown();

	public void cancel(GroupId<?> groupId);

	public void awaitTermination(long timeout, TimeUnit unit);
}
