package tests.jira;

import base.BaseTest;
import jirautil.JiraPolicy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestJiraDemo extends BaseTest {

	@JiraPolicy(raiseDefectFlag = true)
	@Test
	public void testSomething() {

		step("This is the sample Test");

		step("This is the sample Test1");

		step("This is the sample Test2");

		Assert.fail("Intentionally failing the scenario...");

	}
}
