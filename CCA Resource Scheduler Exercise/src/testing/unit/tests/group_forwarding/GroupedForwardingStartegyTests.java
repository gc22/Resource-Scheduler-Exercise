package testing.unit.tests.group_forwarding;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import testing.unit.tests.group_forwarding.cases.UnitTest_GroupForwardingStrategy_SingleMessage;
import testing.unit.tests.group_forwarding.cases.UnitTest_GroupForwardingStrategy_TwoMessageGroupsInterleaving;

@RunWith(Suite.class)
@SuiteClasses({ UnitTest_GroupForwardingStrategy_SingleMessage.class,
		UnitTest_GroupForwardingStrategy_TwoMessageGroupsInterleaving.class })
public class GroupedForwardingStartegyTests {

}
