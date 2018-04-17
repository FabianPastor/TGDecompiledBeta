package org.telegram.messenger.exoplayer2.drm;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.exoplayer2.util.Util;

final class ClearKeyUtil {
    private static final Pattern REQUEST_KIDS_PATTERN = Pattern.compile("\"kids\":\\[\"(.*?)\"]");
    private static final String TAG = "ClearKeyUtil";

    private ClearKeyUtil() {
    }

    public static byte[] adjustRequestData(byte[] request) {
        if (Util.SDK_INT >= 27) {
            return request;
        }
        String requestString = Util.fromUtf8Bytes(request);
        Matcher requestKidsMatcher = REQUEST_KIDS_PATTERN.matcher(requestString);
        if (requestKidsMatcher.find()) {
            int kidsStartIndex = requestKidsMatcher.start(1);
            int kidsEndIndex = requestKidsMatcher.end(1);
            StringBuilder adjustedRequestBuilder = new StringBuilder(requestString);
            base64ToBase64Url(adjustedRequestBuilder, kidsStartIndex, kidsEndIndex);
            return Util.getUtf8Bytes(adjustedRequestBuilder.toString());
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to adjust request data: ");
        stringBuilder.append(requestString);
        Log.e(str, stringBuilder.toString());
        return request;
    }

    public static byte[] adjustResponseData(byte[] response) {
        if (Util.SDK_INT >= 27) {
            return response;
        }
        try {
            JSONObject responseJson = new JSONObject(Util.fromUtf8Bytes(response));
            JSONArray keysArray = responseJson.getJSONArray("keys");
            for (int i = 0; i < keysArray.length(); i++) {
                JSONObject key = keysArray.getJSONObject(i);
                key.put("k", base64UrlToBase64(key.getString("k")));
                key.put("kid", base64UrlToBase64(key.getString("kid")));
            }
            return Util.getUtf8Bytes(responseJson.toString());
        } catch (JSONException e) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to adjust response data: ");
            stringBuilder.append(Util.fromUtf8Bytes(response));
            Log.e(str, stringBuilder.toString(), e);
            return response;
        }
    }

    private static void base64ToBase64Url(StringBuilder base64, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            char charAt = base64.charAt(i);
            if (charAt == '+') {
                base64.setCharAt(i, '-');
            } else if (charAt == '/') {
                base64.setCharAt(i, '_');
            }
        }
    }

    private static String base64UrlToBase64(String base64) {
        return base64.replace('-', '+').replace('_', '/');
    }
}
