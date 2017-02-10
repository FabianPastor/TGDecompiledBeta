package org.telegram.messenger;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;

public class GcmPushListenerService extends GcmListenerService {
    public static final int NOTIFICATION_ID = 1;

    public void onMessageReceived(String from, final Bundle bundle) {
        FileLog.d("GCM received bundle: " + bundle + " from: " + from);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ApplicationLoader.postInitApplication();
                try {
                    if ("DC_UPDATE".equals(bundle.getString("loc_key"))) {
                        JSONObject object = new JSONObject(bundle.getString("custom"));
                        int dc = object.getInt("dc");
                        String[] parts = object.getString("addr").split(":");
                        if (parts.length == 2) {
                            ConnectionsManager.getInstance().applyDatacenterAddress(dc, parts[0], Integer.parseInt(parts[1]));
                        } else {
                            return;
                        }
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                ConnectionsManager.onInternalPushReceived();
                ConnectionsManager.getInstance().resumeNetworkMaybe();
            }
        });
    }
}
