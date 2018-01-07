package org.telegram.messenger.voip;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class VoIPServerConfig {
    private static JSONObject config;

    private static native void nativeSetConfig(String[] strArr, String[] strArr2);

    public static void setConfig(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            config = obj;
            String[] keys = new String[obj.length()];
            String[] values = new String[obj.length()];
            Iterator<String> itrtr = obj.keys();
            int i = 0;
            while (itrtr.hasNext()) {
                keys[i] = (String) itrtr.next();
                values[i] = obj.getString(keys[i]);
                i++;
            }
            nativeSetConfig(keys, values);
        } catch (JSONException x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error parsing VoIP config", x);
            }
        }
    }

    public static int getInt(String key, int fallback) {
        return config.optInt(key, fallback);
    }

    public static double getDouble(String key, double fallback) {
        return config.optDouble(key, fallback);
    }

    public static String getString(String key, String fallback) {
        return config.optString(key, fallback);
    }

    public static boolean getBoolean(String key, boolean fallback) {
        return config.optBoolean(key, fallback);
    }
}
