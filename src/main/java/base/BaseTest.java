package base;

import com.aventstack.extentreports.Status;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import utils.APIUtils;
import utils.ExtentTestManager;
import utils.PropertiesUtil;
import utils.TestListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@Listeners({utils.TestListener.class})
public class BaseTest {

    protected File file = new File("");
    String sSeperator = System.getProperty("file.separator");
    public static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/config/config.properties";

    protected FileInputStream configFis;
    Properties configProp = new Properties();
    public static String sStagingURL;


    public Response loginResponse;
    protected APIUtils apiUtils = new APIUtils();


    Logger log = Logger.getLogger(BaseTest.class);

    @BeforeSuite
    public void setUp() throws Exception {

        // Log4j ImplementationS
        String propertiesFilePath = "./src/main//resources/log4j/log4j.properties";

        PropertiesConfiguration log4jProperties = new PropertiesConfiguration(propertiesFilePath);
        log4jProperties.setProperty("log4j.appender.HTML.File",
                "Reports/" + TestListener.sLatestReportFolder + "/Logs/Htmllogs.html");
        log4jProperties.setProperty("log4j.appender.Appender2.File",
                "Reports/" + TestListener.sLatestReportFolder + "/Logs/Logs.log");
        log4jProperties.save();

        PropertyConfigurator.configure(propertiesFilePath);

        configFis = new FileInputStream(CONFIG_FILE_PATH);
        configProp.load(configFis);
        sStagingURL = configProp.getProperty("StagingURL");


        if (!sStagingURL.endsWith("/")) {
            sStagingURL += "/";
        }
        log.info("sBaseURI : " + sStagingURL);

        log.info("Getting authentication code.");
        String sBody = "{\"email\":\"iossetup@gmail.com\",\"password\":\"test1234\"}";

        log.info("Load the authentication request");
//        RequestSpecification httpRequest = RestAssured.given().baseUri(sStagingURL).basePath("api/login").contentType(ContentType.JSON).body(sBody);

        loginResponse=apiUtils.sendPostRequest("api/login", null, sBody);
//        loginResponse = httpRequest.when().request(Method.POST);


        storeAuthenticationDetailsInProperties();

    }

    public void storeAuthenticationDetailsInProperties() {
        Assert.assertEquals(loginResponse.getStatusCode(), 201, "Response code is not matching.");
        JsonPath responseJSONBody = loginResponse.jsonPath();
        System.out.println("Authentication working............");

        PropertiesUtil.storeValuesInPropertiesFile("Login", "LoginData", "auth_token", responseJSONBody.get("auth_token").toString());
        PropertiesUtil.storeValuesInPropertiesFile("Login", "LoginData", "refresh_token", responseJSONBody.get("refresh_token").toString());
        PropertiesUtil.storeValuesInPropertiesFile("Login", "LoginData", "user.id", responseJSONBody.get("user.id").toString());
        PropertiesUtil.storeValuesInPropertiesFile("Login", "LoginData", "user.name", responseJSONBody.get("user.name").toString());
        PropertiesUtil.storeValuesInPropertiesFile("Login", "LoginData", "user.first_name", responseJSONBody.get("user.first_name").toString());
        PropertiesUtil.storeValuesInPropertiesFile("Login", "LoginData", "user.last_name", responseJSONBody.get("user.last_name").toString());
        PropertiesUtil.storeValuesInPropertiesFile("Login", "LoginData", "user.email", responseJSONBody.get("user.email").toString());
        PropertiesUtil.storeValuesInPropertiesFile("Login", "LoginData", "user.created_at", responseJSONBody.get("user.created_at").toString());

    }

    public void step(String sStepMessage) {
        log.info(sStepMessage);
        ExtentTestManager.getTest().log(Status.INFO, sStepMessage);
    }

}
