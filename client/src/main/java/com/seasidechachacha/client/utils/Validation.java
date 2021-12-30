package com.seasidechachacha.client.utils;

public class Validation {

    public static boolean isCharacterExisted(String content) {
        for (int i = 0; i < content.length(); i++) {
            if (Character.isLetter(content.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNumberExisted(String content) {
        for (int i = 0; i < content.length(); i++) {
            if (Character.isDigit(content.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
