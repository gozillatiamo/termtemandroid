package com.worldwidewealth.wealthwallet.until;

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


    public static final String FORMAT = "format";
    public static final String PHONE = "phone";
    public static final String CURSOR = "cursor";
    public static ContentValues formatPhoneNumber(String strPhone){
        ContentValues values = new ContentValues();
        StringBuilder format = new StringBuilder("( _ _ ) _ _ _  _ _  _ _");
        String valuesPhone = "0";
        int position = 2;
        if (strPhone != null && !strPhone.equals("")) {
            String realPhone = strPhone.replace("(", "")
                    .replace("_", "")
                    .replace(")", "")
                    .replace(" ", "");
            for (int i = 0; i < realPhone.length(); i++) {
                char subStr = realPhone.charAt(i);

                format.setCharAt(position, subStr);
                valuesPhone.concat(String.valueOf(subStr));

                if (i < 8) position = getpositionPhoneEnter(i + 1);

            }
        }

        values.put(FORMAT, format.toString());
        values.put(PHONE, valuesPhone);
        values.put(CURSOR, position);
        return values;
    }

    public static int getpositionPhoneEnter(int position){

        switch (position){
            case 0:
                return 2;

            case 1:
                return 4;

            case 2:
                return 8;

            case 3:
                return 10;

            case 4:
                return 12;

            case 5:
                return 15;

            case 6:
                return 17;

            case 7:
                return 20;

            case 8:
                return 22;

            default:
                return -1;
        }

    }

}
