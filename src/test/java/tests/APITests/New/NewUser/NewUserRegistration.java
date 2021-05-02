package tests.APITests.New.NewUser;

import common.CommonTestManager;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.APIUtils;
import utils.PropertiesUtil;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewUserRegistration extends CommonTestManager {

    Response response;
    String sDeviceSerialNumber;
    private Headers headers;
    private String sBody;
    private String sID;
    private String sBreedName;

    NewUserRegistration() {
        apiUtils = new APIUtils();
    }

    @BeforeMethod
    public void loadingTestData() {
        sDeviceSerialNumber = PropertiesUtil.getValuesInPropertiesFile("NewUser", "NewUserRegistration", "DeviceSerialNumber");
        response = apiUtils.sendGetRequest("/api/breeds/dogs", null, headers);

        ArrayList<Map<Integer, String>> breeds = response.jsonPath().get("breeds");
        int iRandom = StringUtils.RandomInt(0, breeds.size() - 1);


        sID = String.valueOf(breeds.get(iRandom).get("id"));
        sBreedName = breeds.get(iRandom).get("name");

        Assert.assertEquals(response.getStatusCode(), 200, "Status code is not matching");

        sBody = "{\n" +
                "    \"registration\": {\n" +
                "        \"device_serial_number\": \"" + sDeviceSerialNumber + "\",\n" +
                "        \"pet\": {\n" +
                "            \"name\": \"Bailey\",\n" +
                "            \"gender\": \"f\",\n" +
                "            \"profile_photo\": null,\n" +
                "            \"profile\": {\n" +
                "                \"breed\": {\n" +
                "                    \"id\": " + sID + ",\n" +
                "                    \"name\": \"" + sBreedName + "\"\n" +
                "                },\n" +
                "                \"species\": \"dog\",\n" +
                "                \"time_zone_name\": \"America/Los_Angeles\",\n" +
                "                \"weight\": 22,\n" +
                "                \"weight_type\": \"pounds\",\n" +
                "                \"age_in_years\": 2,\n" +
                "                \"age_in_months\": 4,\n" +
                "                \"is_fixed\": true,\n" +
                "                \"body_condition_score\": \"ideal\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"user\": {\n" +
                "            \"email\": \"Whistle" + StringUtils.getRandomString(5) + "@whistle.com\",\n" +
                "            \"first_name\": \"Hasmukh\",\n" +
                "            \"last_name\": \"Patel\",\n" +
                "            \"password\": \"test1234\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        List<Header> headerlist = new ArrayList<>();
        headerlist.add(new Header("Accept", "application/vnd.whistle.com.v4+json"));
        headers = new Headers(headerlist);
    }

    @Test
    public void newUserRegistration() {

        step("Send post call to api/new_registration");
        response = apiUtils.sendPostRequest("api/new_registration", headers, sBody);

        step("verify Status code ");
        Assert.assertEquals(response.getStatusCode(), 201, "Status code is not matching");

        JsonPath responseJSONBody = response.jsonPath();

        step("verify auth_token & pet values to be not null ");
        Assert.assertFalse(responseJSONBody.get("auth_token") == null, "Auth_token value is null");
        Assert.assertFalse(responseJSONBody.get("pet") == null, "pet value is null");

        step("verify profile.breed.id & profile.breed.name values to be not null ");
        Assert.assertFalse(responseJSONBody.get("pet.profile.breed.id") == null, "Breed id is null");
        Assert.assertFalse(responseJSONBody.get("pet.profile.breed.name") == null, "Breed name is null");

        step("verify profile.breed.id & profile.breed.name values to be some value");
        Assert.assertEquals(responseJSONBody.get("pet.profile.breed.id"), Integer.valueOf(sID), "Breed ID is not matching");
        Assert.assertEquals(responseJSONBody.get("pet.profile.breed.name"), sBreedName, "Breed name is not matching");
    }

//    @Test
//    public void testLoginNegative() {
//        String sBody = "{\"email\":\"hasmukh+am2@whistle.com\",\"password\":\"test12345D\"}";
//        step("Send post request for Negative Login request");
//        response = apiUtils.sendPostRequest("api/login", null, sBody);
//        step("Verify the response code to be 422");
//        Assert.assertEquals(response.getStatusCode(), 422, "Response code is not matching.");
//    }
}
