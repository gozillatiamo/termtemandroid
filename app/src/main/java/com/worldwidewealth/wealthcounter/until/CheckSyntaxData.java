package com.worldwidewealth.wealthcounter.until;

import android.content.ContentValues;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MyNet on 4/11/2559.
 */

public class CheckSyntaxData {

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isIdentificationValid(String identification){
        int sum = 0;
        int checkbit = Integer.parseInt(identification.substring(identification.length()-1));
        Log.e("checkbit", checkbit+"");
        for (int i = 13; i > 1; i--){
            int at = identification.length() - i;
            Log.e("at", at+"");
            int num = Integer.parseInt(identification.substring(at, at+1));
            Log.e("num", num+"");
            sum += num*i;
        }
        Log.e("sum", sum+"");
        int resultCheckBit = (11 - (sum % 11)) % 10;
        Log.e("resultCheckBit", resultCheckBit+"");
        if (resultCheckBit == checkbit)
            return true;
        else return false;
    }

/*
    public static ContentValues formatPhoneNumber(String strPhone){
        ContentValues values = null;
        String format = "( _ _ ) _ _ _  _ _  _ _";
        String valuesPhone = "0";

        for (int i = 0; i < strPhone.length(); i++){
            char subStr = strPhone.charAt(i);
            if (i == 0){

            }
        }
    }
*/

}
