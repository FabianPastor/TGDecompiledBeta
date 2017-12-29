package org.telegram.messenger;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIDListenerService extends InstanceIDListenerService {
    public void onTokenRefresh() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ApplicationLoader.postInitApplication();
                try {
                    GcmInstanceIDListenerService.this.startService(new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class));
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
    }
}
