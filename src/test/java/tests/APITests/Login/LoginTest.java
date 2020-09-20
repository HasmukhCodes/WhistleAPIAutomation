package tests.APITests.Login;

import common.CommonTestManager;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.APIUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginTest extends CommonTestManager {

    Response response;

    LoginTest() {
        apiUtils = new APIUtils();
    }

    @BeforeMethod
    public void loadingTestData() {
        List<Header> headerlist = new ArrayList<>();
        headerlist.add(new Header("first_name", "Hasmukh"));
        headerlist.add(new Header("last_name", "Patel"));
        Headers headers = new Headers(headerlist);
    }

    @Test
    public void testLoginPositive() {
        step("Authentication generated in Before Suite");
        step("Verify the response code to be 201");
        Assert.assertEquals(loginResponse.getStatusCode(), 201, "Response code is not matching.");
    }

    @Test
    public void testLoginNegative() {
        String sBody = "{\"email\":\"hasmukh+am2@whistle.com\",\"password\":\"test12345D\"}";
        step("Send post request for Negative Login request");
        response = apiUtils.sendPostRequest("api/login", null, sBody);
        step("Verify the response code to be 422");
        Assert.assertEquals(response.getStatusCode(), 422, "Response code is not matching.");
    }
}
