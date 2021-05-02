package tests.APITests.Breeds;

import common.CommonTestManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import reusablecomponent.VerificationMethods;
import utils.APIUtils;
import utils.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

public class DogBreedTest extends CommonTestManager {

    RequestSpecification httpRequest;
    Response response;
    Headers headers;



    DogBreedTest() {
        verificationMethods = new VerificationMethods();
    }

    @BeforeMethod
    public void loadingTestData() {
        String sAuthenticationToken = PropertiesUtil.getValuesInPropertiesFile("Login", "LoginData", "auth_token");

        List<Header> headerlist = new ArrayList<>();
        headerlist.add(new Header("Authorization", "Bearer " + sAuthenticationToken));
        headerlist.add(new Header("Accept", "application/vnd.whistle.com.v4+json"));
        headers = new Headers();
    }



    @Test
    public void testDogsBreedAPI() {

        step("Load the request");
        httpRequest = RestAssured.given().headers(headers).baseUri(sStagingURL).basePath("api/breeds/dogs").contentType(ContentType.JSON);

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
        httpRequest = RestAssured.given().given().headers(headers).baseUri(sStagingURL).basePath("api/breeds/cats").contentType(ContentType.JSON);

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
