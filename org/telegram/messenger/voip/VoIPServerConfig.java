package org.telegram.messenger.voip;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;

public class VoIPServerConfig {
    private static native void nativeSetConfig(String[] strArr, String[] strArr2);

    public static void setConfig(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String[] keys = new String[obj.length()];
            String[] values = new String[obj.length()];
            Iterator<String> itrtr = obj.keys();
            while (itrtr.hasNext()) {
                keys[0] = (String) itrtr.next();
                values[0] = obj.getString(keys[0]);
            }
            nativeSetConfig(keys, values);
        } catch (JSONException x) {
            FileLog.e("Error parsing VoIP config", x);
        }
    }
}
