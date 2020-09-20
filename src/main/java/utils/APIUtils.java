package utils;

import base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIUtils {

    public static JSONArray jsonStringToJSONArray(String sJSON, String sJSONKey) {
        JSONObject obj = new JSONObject(sJSON);
        JSONArray arr = obj.getJSONArray(sJSONKey);
        return arr;
    }

    public Response sendPostRequest(String sBasePath, Headers headers, String sBody) {

        RequestSpecification requestSpecification = RestAssured.given().baseUri(BaseTest.sStagingURL).basePath(sBasePath).contentType(ContentType.JSON);
        if (headers != null)
            requestSpecification = requestSpecification.headers(headers);

        if (sBody != null)
            requestSpecification = requestSpecification.body(sBody);

        Response response = requestSpecification.request(Method.POST);
        return response;
    }

    public Response sendGetRequest(String sBasePath, String sAuthenticationToken, Headers headers) {
        RequestSpecification requestSpecification = RestAssured.given().baseUri(BaseTest.sStagingURL).basePath(sBasePath).header("Accept", "application/vnd.whistle.com.v4+json").contentType(ContentType.JSON);
        if (sAuthenticationToken != null)
            requestSpecification = requestSpecification.header("Authorization", "Bearer " + sAuthenticationToken);

        if (headers != null)
            requestSpecification = requestSpecification.headers(headers);

        Response response = requestSpecification.request(Method.GET);
        return response;
    }


    public Response getAnotherRequest(){
//        200
//                Reponse 2

        RequestSpecification requestSpecification=RestAssured.given().baseUri("https://reqres.in/").basePath("/api/users").queryParam("page","2");
        Response response=requestSpecification.request(Method.GET);
        return response;

    }
}
