package utils;

import java.util.Random;

public class StringUtils {


    public static String getRandomString(int iLength){
        String generatedString=System.nanoTime()+"";
        return generatedString;
    }

    public static int RandomInt(int iStart,int iEnd){
        Random random=new Random();
        return random.nextInt(iEnd - iStart + 1) + iStart;

    }
}
