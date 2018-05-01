package org.telegram.messenger.exoplayer2.drm;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.exoplayer2.util.Util;

final class ClearKeyUtil {
    private static final Pattern REQUEST_KIDS_PATTERN = Pattern.compile("\"kids\":\\[\"(.*?)\"]");
    private static final String TAG = "ClearKeyUtil";

    private ClearKeyUtil() {
    }

    public static byte[] adjustRequestData(byte[] bArr) {
        if (Util.SDK_INT >= 27) {
            return bArr;
        }
        String fromUtf8Bytes = Util.fromUtf8Bytes(bArr);
        Matcher matcher = REQUEST_KIDS_PATTERN.matcher(fromUtf8Bytes);
        if (matcher.find()) {
            int start = matcher.start(1);
            bArr = matcher.end(1);
            StringBuilder stringBuilder = new StringBuilder(fromUtf8Bytes);
            base64ToBase64Url(stringBuilder, start, bArr);
            return Util.getUtf8Bytes(stringBuilder.toString());
        }
        String str = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Failed to adjust request data: ");
        stringBuilder2.append(fromUtf8Bytes);
        Log.e(str, stringBuilder2.toString());
        return bArr;
    }

    public static byte[] adjustResponseData(byte[] bArr) {
        if (Util.SDK_INT >= 27) {
            return bArr;
        }
        try {
            JSONObject jSONObject = new JSONObject(Util.fromUtf8Bytes(bArr));
            JSONArray jSONArray = jSONObject.getJSONArray("keys");
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                jSONObject2.put("k", base64UrlToBase64(jSONObject2.getString("k")));
                jSONObject2.put("kid", base64UrlToBase64(jSONObject2.getString("kid")));
            }
            return Util.getUtf8Bytes(jSONObject.toString());
        } catch (Throwable e) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to adjust response data: ");
            stringBuilder.append(Util.fromUtf8Bytes(bArr));
            Log.e(str, stringBuilder.toString(), e);
            return bArr;
        }
    }

    private static void base64ToBase64Url(StringBuilder stringBuilder, int i, int i2) {
        while (i < i2) {
            char charAt = stringBuilder.charAt(i);
            if (charAt == '+') {
                stringBuilder.setCharAt(i, '-');
            } else if (charAt == '/') {
                stringBuilder.setCharAt(i, '_');
            }
            i++;
        }
    }

    private static String base64UrlToBase64(String str) {
        return str.replace('-', '+').replace('_', '/');
    }
}
