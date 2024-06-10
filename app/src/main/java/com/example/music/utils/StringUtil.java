package com.example.music.utils;

public class StringUtil {

    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isGoodField(String input) {
        return input != null && !input.isEmpty() && input.length() >= 8;
    }
    public static boolean isValidPhoneNumber(String input) {
        return  input != null && input.length() == 10 && input.startsWith("0");
    }



    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty() || ("").equals(input.trim());
    }


    public static String getStringPhoneNumber(String number) {
        if (number.length() ==9) {
            return "0" + number;
        } else return number;
    }
}
