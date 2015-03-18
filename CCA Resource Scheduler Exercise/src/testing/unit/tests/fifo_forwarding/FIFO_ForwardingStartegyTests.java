package testing.unit.tests.fifo_forwarding;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import testing.unit.tests.fifo_forwarding.cases.UnitTest_FIFO_ForwardingStrategy_SingleMessage;
import testing.unit.tests.fifo_forwarding.cases.UnitTest_FIFO_ForwardingStrategy_TwoMessageGroupsInterleaving;

@RunWith(Suite.class)
@SuiteClasses({ UnitTest_FIFO_ForwardingStrategy_SingleMessage.class,
		UnitTest_FIFO_ForwardingStrategy_TwoMessageGroupsInterleaving.class })
public class FIFO_ForwardingStartegyTests {

}
