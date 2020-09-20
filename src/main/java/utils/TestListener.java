package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import jirautil.JiraPolicy;
import jirautil.JiraServiceProvider;
import net.rcarz.jiraclient.Issue;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.*;
import testrailutil.APIClient;
import testrailutil.TestRail;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TestListener extends TestListenerAdapter implements ISuiteListener {

    public static String sFolderName;
    public static String sReportFolderPath;
    public static String sLatestReportFolder;
    ExtentTest extentTest;
    private RemoteWebDriver driver;
    public static String sLatestReportFolderPath;
    final public static String sSeperator = System.getProperty("file.separator");

    APIClient client;
    int iTestRailRunId;
    public static Boolean postResultsToTestRail = false;
    public Boolean bRaiseJiraTicket = false;
    Issue issue;

    String sJiraUrl;
    String sJiraUsername;
    String sJiraPassword;
    String sJiraProject;

    public TestListener() {

    }

    /**
     * Purpose of this method is to perform certain action on the test execution
     * start Primarily the report is deleted, Test rail API initialization
     *
     * @param suite
     */
    public void onStart(ISuite suite) {
        createLatestReportFolder();

        FileUtils.deleteReportsMoreThanXDays(sReportFolderPath, Integer.parseInt(PropertiesUtil.getConfigProperties("NoOfDaysToStoreReport")));
        client = new APIClient(PropertiesUtil.getConfigProperties("TestRailURL"));
        client.setUser(PropertiesUtil.getConfigProperties("TestRailUsername"));
        client.setPassword(PropertiesUtil.getConfigProperties("TestRailPassword"));

        iTestRailRunId = Integer.parseInt(PropertiesUtil.getConfigProperties("TestRailRunID"));
        postResultsToTestRail = Boolean.parseBoolean(PropertiesUtil.getConfigProperties("StoreTestRailValue"));

        sJiraUrl = PropertiesUtil.getConfigProperties("JiraURL");
        sJiraUsername = PropertiesUtil.getConfigProperties("JiraUsername");
        sJiraPassword = PropertiesUtil.getConfigProperties("JiraPassword");
        sJiraProject = PropertiesUtil.getConfigProperties("JiraProject");

        bRaiseJiraTicket = Boolean.parseBoolean(PropertiesUtil.getConfigProperties("RaiseJiraTickets"));
    }

    /**
     * Purpose of this method is to create the latest report folder in the framework
     */
    public void createLatestReportFolder() {

        sReportFolderPath = System.getProperty("user.dir") + sSeperator + "Reports";
        sLatestReportFolder = DateUtils.getTimeStamp("ddMMMyyyy HHmm") + "Report";

        sLatestReportFolderPath = sReportFolderPath + sSeperator + sLatestReportFolder;

        File reportFolder = new File(sReportFolderPath);// Reports folder
        File latestResultFolder = new File(sLatestReportFolderPath);// latestresults
        if (!reportFolder.exists()) {
            reportFolder.mkdir();
        }

        if (!latestResultFolder.exists()) {
            latestResultFolder.mkdir();
            System.out.println("Reports folder created successfully");
        }
    }

    /**
     * Purpose of this method is to invoke the onTestStart before any test is
     * started This initializes the Extent report to have test without any result
     *
     * @param result
     */
    @Override
    public synchronized void onTestStart(ITestResult result) {
        String sMethodName = result.getMethod().getMethodName();
        System.out.println("on Test Start " + sMethodName);

        extentTest = ExtentTestManager.startTest(result.getMethod().getMethodName());
    }

    /**
     * This method is invoked when any Test is passed and this method has the
     * implementation of Updating Extent report, Posting test result on TestRail
     * Test case management tool
     *
     * @param result
     */
    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        System.out.println("onTestSuccess");

        System.out.println("*********Test Passed*********");
        String sMethodName = result.getMethod().getMethodName();

        ExtentTestManager.getTest().log(Status.PASS, "Test passed");

        if (postResultsToTestRail) {
            // get the TestRail Test Case id from the Test annotation description value
            int[] testCaseIds = returnTestCaseId(result);
            if (testCaseIds != null && iTestRailRunId != 0) {
                String sTestRailComment = "Automated TestNG Test - PASS; \n\nTest method name = " + sMethodName;
                System.out.println(sTestRailComment);

                try {
                    // add the result to TestRail
                    for (int testCaseId : testCaseIds) {
                        System.out.println(testCaseId);
                        // 1	Passed,2 Blocked, 3	Untested (not allowed when adding a result),4 Retest, 5	Failed
                        addResult(1, sTestRailComment, iTestRailRunId, testCaseId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        System.out.println("*****************************");

    }

    /**
     * * This method is invoked when any Test is Failed and this method has the
     * implementation of Updating Extent report along with the screenshot, Posting
     * test result on TestRail Test case management tool
     *
     * @param result
     */
    @Override
    public synchronized void onTestFailure(ITestResult result) {

        System.out.println("*********Test Failed*********");

        String sMethodName = result.getMethod().getMethodName();
        System.out.println("onTestFailure " + sMethodName);
        Object currentClass = result.getInstance();
        String sErrorMessage = result.getThrowable().toString();
        System.out.println(result.getThrowable());

        ExtentTestManager.getTest().log(Status.FAIL, sMethodName + " is failed : " + sErrorMessage);


        // Test rail implementation
        if (postResultsToTestRail) {
            // get the TestRail Test Case id from the Test annonation description value
            int[] testCaseIds = returnTestCaseId(result);
            if (testCaseIds != null && iTestRailRunId != 0) {
                String testRailComment = "Automated TestNG Test - FAIL\n\nTest method name = " + sMethodName
                        + "\n\nFailure Exception = " + sErrorMessage;

                try {
                    // add the result to TestRail
                    for (int testCaseId : testCaseIds) {
                        System.out.println(testCaseId);
                        // 1	Passed,2 Blocked, 3	Untested (not allowed when adding a result),4 Retest, 5	Failed
                        addResult(5, testRailComment, iTestRailRunId, testCaseId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (bRaiseJiraTicket) {
            // Jira implementation
            JiraPolicy jiraMetaData = result.getMethod().getConstructorOrMethod().getMethod()
                    .getAnnotation(JiraPolicy.class);
            if (jiraMetaData != null) {
                // get the annotation parameter value as a boolean
                boolean bugCreationFlag = jiraMetaData.raiseDefectFlag();
                // Check if the annotation attribute value is raiseDefectFlag=true
                if (bugCreationFlag) {
                    // raise the jira ticket
                    JiraServiceProvider jiraSP = new JiraServiceProvider(sJiraUrl, sJiraUsername, sJiraPassword,
                            sJiraProject);

                    // Add the failed method name as the issue summary
                    String issueSummary = result.getMethod().getConstructorOrMethod().getMethod().getName()
                            + " was failed due to an exception";

                    // get the error message from the exception to description
                    String issueDescription = "Exception details : " + result.getThrowable().getMessage() + "\n";
                    // Append the full stack trace to the description.
                    issueDescription.concat("");

                    issue = jiraSP.createJiraIssue("Bug", issueSummary, issueDescription);

                    System.out.println("Jira Issue created : " + issue);
                    ExtentTestManager.getTest().log(Status.FAIL, "Jira Issue created : " + sJiraUrl + "/browse/" + issue);

                }
            }
        }


        System.out.println("*****************************");

    }

    /**
     * * This method is invoked when any Test is Skipped and this method has the
     * implementation of Updating Extent report
     *
     * @param result
     */
    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        System.out.println("onTestSkipped");
        ExtentTestManager.getTest().log(Status.SKIP, "Test Skipped");
    }

    public void onFinish(ISuite suite) {
        // TODO Auto-generated method stub
        ExtentTestManager.endTest();
        ExtentManager.getInstance().flush();

//        WebDriverManager.chromedriver().setup();
//
//        WebDriver driver = new ChromeDriver();
//        driver.manage().window().maximize();
//        driver.get(sLatestReportFolderPath + sSeperator + "WhistleExtentReports" + ".html");

    }

    /**
     * Purpose of this method is to return the caseID mentioned in @test method
     *
     * @param result
     * @return
     */
    public int[] returnTestCaseId(ITestResult result) {
        ITestNGMethod testNGMethod = result.getMethod();
        Method method = testNGMethod.getConstructorOrMethod().getMethod();
        TestRail testRailAnnotation = method.getAnnotation(TestRail.class);
        int[] testCaseIds;
        try {
            testCaseIds = testRailAnnotation.testCaseId();
        } catch (Exception ex) {
            return null;
        }
        return testCaseIds;
    }

    public JSONObject addResult(int p_statusId, String p_comment, int p_runId, int p_caseId) throws Exception {

        System.out.println("Posting result to TestRail...");
        System.out.println("Comment : " + p_comment);
        Map data = new HashMap();
        data.put("status_id", new Integer(p_statusId));
        data.put("comment", p_comment);
        JSONObject r = (JSONObject) client.sendPost("add_result_for_case/" + p_runId + "/" + p_caseId, data);
        System.out.println("Posted result to TestRail...");

        return r;
    }

}
