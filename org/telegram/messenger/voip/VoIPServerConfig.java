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
            int i = 0;
            while (itrtr.hasNext()) {
                keys[i] = (String) itrtr.next();
                values[i] = obj.getString(keys[i]);
                i++;
            }
            nativeSetConfig(keys, values);
        } catch (JSONException x) {
            FileLog.e("Error parsing VoIP config", x);
        }
    }
}
