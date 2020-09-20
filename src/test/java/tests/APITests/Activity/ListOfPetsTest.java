package tests.APITests.Activity;

import common.CommonTestManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.PropertiesUtil;

public class ListOfPetsTest extends CommonTestManager {

    RequestSpecification httpRequest;
    Response response;

    @Test
    public void testListOfPetAPI() {
        String sAuthenticationToken = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "auth_token");
        System.out.println("sAuthenticationToken " + sAuthenticationToken);

        step("Hit the GET request & get the response");
        response = apiUtils.sendGetRequest("api/pets", sAuthenticationToken, null);

        step("Verify the response code to be 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Response code is not matching.");

        step("Verify the response has pets but not null & store the value in properties file");
        JsonPath responseJSONBody = response.jsonPath();
        Assert.assertTrue(responseJSONBody.get("pets") != null, "Pets values are null");
        PropertiesUtil.storeValuesInPropertiesFile("Activity", "Activity", "pet_id", responseJSONBody.get("pets[0].id").toString());

    }

    @Test(dependsOnMethods = {"testListOfPetAPI"})
    public void testSetActivityGoalAPI() {
        String sBody = "{\"activity_goal\":{\"minutes\":20}}";
        String sAuthenticationToken = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "auth_token");
        String sPetID = PropertiesUtil.getValuesInPropertiesFile("Activity", "Activity", "pet_id");

        step("Load the request");
        httpRequest = RestAssured.given().header("Authorization", "Bearer " + sAuthenticationToken).header("Accept", "application/vnd.whistle.com.v4+json").baseUri(sStagingURL).basePath("api/pets").body(sBody).contentType(ContentType.JSON);

        step("Hit the POST request & get the response");
        response = httpRequest.when().post("{id}/activity_goals", sPetID);

        step("Verify the response code to be 201");
        Assert.assertEquals(response.getStatusCode(), 201, "Response code is not matching.");

        step("Verify the response has activity_goal but not null");
        JsonPath responseJSONBody = response.jsonPath();
        Assert.assertTrue(responseJSONBody.get("activity_goal") != null, "activity_goal values are null");

        step("Verify the response has upcoming_activity_goal but not null");
        Assert.assertTrue(responseJSONBody.get("upcoming_activity_goal") != null, "upcoming_activity_goal values are null");

    }

}
