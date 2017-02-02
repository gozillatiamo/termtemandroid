package com.worldwidewealth.termtem.until;

import android.content.ContentValues;

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
        for (int i = 13; i > 1; i--){
            int at = identification.length() - i;
            int num = Integer.parseInt(identification.substring(at, at+1));
            sum += num*i;
        }
        int resultCheckBit = (11 - (sum % 11)) % 10;
        if (resultCheckBit == checkbit)
            return true;
        else return false;
    }

    public static boolean isPhoneValid(String phone){
        int sum = 0;
        if (!(phone.toString().matches("\\d+(?:\\.\\d+)?"))) return false;
        for (int i = 0; i < phone.length(); i++){
            sum += Character.getNumericValue(phone.charAt(i));
        }

        return  (phone.length() == 10 &&
                phone.subSequence(0, 0+1).equals("0") &&
                sum > 0);

    }

}
