package tests.APITests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.APIUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GetUserTest {

    @Test
    public void getUserTest() {

        boolean bFlag = false;
        APIUtils apiUtils = new APIUtils();
        Response response = apiUtils.getAnotherRequest();
        Assert.assertEquals(response.statusCode(), 200, "Response status is not matching.");

        JsonPath responseJSONBody = response.jsonPath();

        ArrayList<LinkedHashMap<String, String>> list = (ArrayList<LinkedHashMap<String, String>>) responseJSONBody.get("data");
        for (int i = 0; i < list.size(); i++) {
//            list.get(i)
            if (String.valueOf(list.get(i).get("id")).contentEquals("10")) {
                bFlag = true;
                Assert.assertEquals(list.get(i).get("email"), "saurabh@reqres.in", "Email not matching");
                System.out.println("Verified id and email");
                break;
            }
        }
        Assert.assertTrue(bFlag, "ID not found");



    }
}
