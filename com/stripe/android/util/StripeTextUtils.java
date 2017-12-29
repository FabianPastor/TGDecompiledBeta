package com.stripe.android.util;

public class StripeTextUtils {
    public static boolean hasAnyPrefix(String number, String... prefixes) {
        if (number == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (number.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWholePositiveNumber(String value) {
        if (value == null) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String nullIfBlank(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static String asCardBrand(String possibleCardType) {
        if (isBlank(possibleCardType)) {
            return null;
        }
        if ("American Express".equalsIgnoreCase(possibleCardType)) {
            return "American Express";
        }
        if ("MasterCard".equalsIgnoreCase(possibleCardType)) {
            return "MasterCard";
        }
        if ("Diners Club".equalsIgnoreCase(possibleCardType)) {
            return "Diners Club";
        }
        if ("Discover".equalsIgnoreCase(possibleCardType)) {
            return "Discover";
        }
        if ("JCB".equalsIgnoreCase(possibleCardType)) {
            return "JCB";
        }
        if ("Visa".equalsIgnoreCase(possibleCardType)) {
            return "Visa";
        }
        return "Unknown";
    }

    public static String asFundingType(String possibleFundingType) {
        if (isBlank(possibleFundingType)) {
            return null;
        }
        if ("credit".equalsIgnoreCase(possibleFundingType)) {
            return "credit";
        }
        if ("debit".equalsIgnoreCase(possibleFundingType)) {
            return "debit";
        }
        if ("prepaid".equalsIgnoreCase(possibleFundingType)) {
            return "prepaid";
        }
        return "unknown";
    }

    public static String asTokenType(String possibleTokenType) {
        if ("card".equals(possibleTokenType)) {
            return "card";
        }
        return null;
    }
}
