package org.telegram.messenger;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GcmInstanceIDListenerService extends FirebaseInstanceIdService {
    public void onTokenRefresh() {
        try {
            AndroidUtilities.runOnUIThread(new GcmInstanceIDListenerService$$Lambda$0(FirebaseInstanceId.getInstance().getToken()));
        } catch (Throwable e) {
            FileLog.m14e(e);
        }
    }

    static final /* synthetic */ void lambda$onTokenRefresh$0$GcmInstanceIDListenerService(String refreshedToken) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m11d("Refreshed token: " + refreshedToken);
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(refreshedToken);
    }

    public static void sendRegistrationToServer(String token) {
        Utilities.stageQueue.postRunnable(new GcmInstanceIDListenerService$$Lambda$1(token));
    }

    static final /* synthetic */ void lambda$sendRegistrationToServer$2$GcmInstanceIDListenerService(String token) {
        SharedConfig.pushString = token;
        for (int a = 0; a < 3; a++) {
            UserConfig userConfig = UserConfig.getInstance(a);
            userConfig.registeredForPush = false;
            userConfig.saveConfig(false);
            if (userConfig.getClientUserId() != 0) {
                AndroidUtilities.runOnUIThread(new GcmInstanceIDListenerService$$Lambda$2(a, token));
            }
        }
    }
}
