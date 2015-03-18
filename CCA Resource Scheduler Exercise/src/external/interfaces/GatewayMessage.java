package external.interfaces;

public interface GatewayMessage {
	public void completed();

	public GroupId<?> getGroupId();
}
