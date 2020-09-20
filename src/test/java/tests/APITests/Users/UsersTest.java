package tests.APITests.Users;

import common.CommonTestManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.PropertiesUtil;

public class UsersTest extends CommonTestManager {

    RequestSpecification httpRequest;
    Response response;

    @Test
    public void testShowCurrentUser() {

        String sAuthenticationToken = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "auth_token");
        String sName = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "user.name");
        String sFirstName = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "user.first_name");
        String sLastName = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "user.last_name");
        String sEmail = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "user.email");

        step("Load the request and hit request");
        response = apiUtils.sendGetRequest("api/users/me", sAuthenticationToken, null);

        step("Verify the response code to be 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Response code is not matching.");

        step("Verify the response is having username matching");
        JsonPath responseJSONBody = response.jsonPath();
        Assert.assertEquals(responseJSONBody.get("user.name"), sName, "Full name is not matching.");

        step("Verify the response is having first name matching");
        Assert.assertEquals(responseJSONBody.get("user.first_name"), sFirstName, "First name is not matching.");

        step("Verify the response is having lastname matching");
        Assert.assertEquals(responseJSONBody.get("user.last_name"), sLastName, "Last name is not matching.");

        step("Verify the response is having email matching");
        Assert.assertEquals(responseJSONBody.get("user.email"), sEmail, "Email is not matching.");
    }

}
