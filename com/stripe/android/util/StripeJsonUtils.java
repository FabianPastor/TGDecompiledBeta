package com.stripe.android.util;

import org.json.JSONException;
import org.json.JSONObject;

public class StripeJsonUtils {
    public static String getString(JSONObject jsonObject, String fieldName) throws JSONException {
        return nullIfNullOrEmpty(jsonObject.getString(fieldName));
    }

    public static String optString(JSONObject jsonObject, String fieldName) {
        return nullIfNullOrEmpty(jsonObject.optString(fieldName));
    }

    static String nullIfNullOrEmpty(String possibleNull) {
        return ("null".equals(possibleNull) || TtmlNode.ANONYMOUS_REGION_ID.equals(possibleNull)) ? null : possibleNull;
    }
}
