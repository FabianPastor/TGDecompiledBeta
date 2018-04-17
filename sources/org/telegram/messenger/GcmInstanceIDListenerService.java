package org.telegram.messenger;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIDListenerService extends InstanceIDListenerService {

    /* renamed from: org.telegram.messenger.GcmInstanceIDListenerService$1 */
    class C01741 implements Runnable {
        C01741() {
        }

        public void run() {
            ApplicationLoader.postInitApplication();
            try {
                GcmInstanceIDListenerService.this.startService(new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class));
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public void onTokenRefresh() {
        AndroidUtilities.runOnUIThread(new C01741());
    }
}
