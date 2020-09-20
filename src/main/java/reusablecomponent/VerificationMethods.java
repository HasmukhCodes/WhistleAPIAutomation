package reusablecomponent;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.testng.Assert;

public class VerificationMethods {

    public void verifyBreedsIdAndName(JSONArray breedsArr) {
        Assert.assertTrue(breedsArr.length() > 0, "Breeds are less than 1");
        for (int i = 0; i < breedsArr.length(); i++) {
            String sID = Integer.toString(breedsArr.getJSONObject(i).getInt("id"));
            String sName = breedsArr.getJSONObject(i).getString("name");
            Assert.assertTrue(StringUtils.isNumeric(sID) && sID.length() > 0, "Breed ID is not proper");
            Assert.assertTrue(sName.length() > 0, "Breed Name is not proper");

        }

    }
}
