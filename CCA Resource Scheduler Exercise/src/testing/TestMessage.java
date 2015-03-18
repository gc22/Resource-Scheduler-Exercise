package testing;

import external.interfaces.GroupId;
import external.interfaces.GatewayMessage;

public class TestMessage implements GatewayMessage {

	private class TestGroupId implements GroupId<Integer>{
		private int internalId;

		public TestGroupId(int internalId) {
			this.internalId = internalId;
		}

		@Override
		public int compareTo(GroupId<Integer> o) {
			return Integer.compare(internalId, o.getInternalId());
		}

		@Override
		public Integer getInternalId() {
			return this.internalId;
		}

		@Override
		public String toString() {
			return String.format("GroupId<%d>", this.internalId);
		}

		@Override
		public boolean equals(Object obj) {
			System.out.println("Is equal?");
			boolean isEqual = false;
			if(obj instanceof TestGroupId){
				isEqual = ((TestGroupId) obj).internalId == internalId;
			}
			return isEqual | super.equals(obj);
		}
	}

	private TestGroupId groupId = null;
	private boolean isCompleted = false;

	public TestMessage(int internalId) {
		this.groupId = new TestGroupId(internalId);
	}

	@Override
	public void completed() {
		this.isCompleted = true;
	}

	@Override
	public GroupId<Integer> getGroupId() {
		return this.groupId;
	}

	@Override
	public String toString() {
		return "<Test Message <" + getGroupId() + ">, Completed? " + this.isCompleted + " " + this.hashCode() + ">";
	}
}
