package external.interfaces;

public interface GroupId<T> extends Comparable<GroupId<T>> {
	public T getInternalId();
}