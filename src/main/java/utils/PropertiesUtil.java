package utils;

import base.BaseTest;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.testng.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesUtil {

    static Logger log = Logger.getLogger("PropertiesUtil");

    public static String getConfigProperties(String sKey) {
        Properties prop;
        String sValue = null;
        try {
            InputStream input = new FileInputStream(BaseTest.CONFIG_FILE_PATH);
            prop = new Properties();
            // load a properties file
            prop.load(input);
            sValue = prop.getProperty(sKey);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sValue;
    }

    public static void storeValuesInPropertiesFile(String sAPIFolder, String sFileName, String sKey, String sValue) {
        Properties prop;
        String sFilePath = "./src/test/resources/TestData/" + sAPIFolder + "/" + sFileName + ".properties";
        try {
            PropertiesConfiguration log4jProperties = new PropertiesConfiguration(sFilePath);
            log4jProperties.setProperty(sKey, sValue);
            log4jProperties.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            Assert.fail("Error occurred while storing TestData in properties file");
        }
    }

    public static String getValuesInPropertiesFile(String sAPIFolder, String sFileName, String sKey) {
        Properties prop;
        String sValue = null;
        String sFilePath = "./src/test/resources/TestData/" + sAPIFolder + "/" + sFileName + ".properties";

        try {
            InputStream input = new FileInputStream(sFilePath);
            prop = new Properties();
            // load a properties file
            prop.load(input);
            sValue = prop.getProperty(sKey);
            System.out.println("Getting " + sKey + " value from " + sFileName + " properties file");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sValue;
    }

}
