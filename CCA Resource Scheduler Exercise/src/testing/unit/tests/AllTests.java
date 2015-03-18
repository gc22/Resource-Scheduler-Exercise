package testing.unit.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import testing.unit.tests.fifo_forwarding.FIFO_ForwardingStartegyTests;
import testing.unit.tests.group_forwarding.GroupedForwardingStartegyTests;
import testing.unit.tests.group_forwarding.cases.UnitTest_GroupForwardingStrategy_SingleMessage;
import testing.unit.tests.group_forwarding.cases.UnitTest_GroupForwardingStrategy_TwoMessageGroupsInterleaving;

@RunWith(Suite.class)
@SuiteClasses({ GroupedForwardingStartegyTests.class, FIFO_ForwardingStartegyTests.class })
public class AllTests {

}
