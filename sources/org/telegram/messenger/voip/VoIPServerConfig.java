package org.telegram.messenger.voip;

import java.util.Iterator;
import org.json.JSONObject;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class VoIPServerConfig {
    private static JSONObject config;

    private static native void nativeSetConfig(String[] strArr, String[] strArr2);

    public static void setConfig(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            config = jSONObject;
            str = new String[jSONObject.length()];
            String[] strArr = new String[jSONObject.length()];
            Iterator keys = jSONObject.keys();
            int i = 0;
            while (keys.hasNext()) {
                str[i] = (String) keys.next();
                strArr[i] = jSONObject.getString(str[i]);
                i++;
            }
            nativeSetConfig(str, strArr);
        } catch (String str2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m2e("Error parsing VoIP config", str2);
            }
        }
    }

    public static int getInt(String str, int i) {
        return config.optInt(str, i);
    }

    public static double getDouble(String str, double d) {
        return config.optDouble(str, d);
    }

    public static String getString(String str, String str2) {
        return config.optString(str, str2);
    }

    public static boolean getBoolean(String str, boolean z) {
        return config.optBoolean(str, z);
    }
}
