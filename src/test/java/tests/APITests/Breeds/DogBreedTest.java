package tests.APITests.Breeds;

import common.CommonTestManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.Test;
import reusablecomponent.VerificationMethods;
import utils.APIUtils;
import utils.PropertiesUtil;

public class DogBreedTest extends CommonTestManager {

    RequestSpecification httpRequest;
    Response response;


    DogBreedTest() {
        verificationMethods = new VerificationMethods();
    }


    @Test
    public void testDogsBreedAPI() {
        String sAuthenticationToken = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "auth_token");

        step("Load the request");
        httpRequest = RestAssured.given().header("Authorization", "Bearer " + sAuthenticationToken).header("Accept", "application/vnd.whistle.com.v4+json").baseUri(sStagingURL).basePath("api/breeds/dogs").contentType(ContentType.JSON);

        step("Hit the GET request & get the response");
        response = httpRequest.when().request(Method.GET);

        step("Verify the response code");
        Assert.assertEquals(response.getStatusCode(), 200, "Response code is not matching.");
        JsonPath responseJSONBody = response.jsonPath();

        step("Verify the response has breed options but not null");
        Assert.assertTrue(responseJSONBody.get("breeds") != null, "Breeds values are null");


        step("Verify for all Dog breed id and name is having some values");
        JSONArray breedsArr = APIUtils.jsonStringToJSONArray(responseJSONBody.prettify(), "breeds");
        verificationMethods.verifyBreedsIdAndName(breedsArr);

    }

    @Test
    public void testCatsBreedAPI() {
        String sAuthenticationToken = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "auth_token");

        step("Load the request");
        httpRequest = RestAssured.given().header("Authorization", "Bearer " + sAuthenticationToken).header("Accept", "application/vnd.whistle.com.v4+json").baseUri(sStagingURL).basePath("api/breeds/cats").contentType(ContentType.JSON);

        step("Hit the GET request & get the response");
        response = httpRequest.when().request(Method.GET);

        step("Verify the response code to be 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Response code is not matching.");

        step("Verify the response has breed options but not null");
        JsonPath responseJSONBody = response.jsonPath();
        Assert.assertTrue(responseJSONBody.get("breeds") != null, "Breeds values are null");

        step("Verify for all Cat breed id and name is having some values");
        JSONArray breedsArr = APIUtils.jsonStringToJSONArray(responseJSONBody.prettify(), "breeds");
        verificationMethods.verifyBreedsIdAndName(breedsArr);
    }
}
