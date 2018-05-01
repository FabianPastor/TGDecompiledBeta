package org.telegram.messenger;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GcmInstanceIDListenerService extends FirebaseInstanceIdService {
    public void onTokenRefresh() {
        try {
            final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("Refreshed token: " + refreshedToken);
                    }
                    ApplicationLoader.postInitApplication();
                    GcmInstanceIDListenerService.sendRegistrationToServer(refreshedToken);
                }
            });
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void sendRegistrationToServer(final String token) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                SharedConfig.pushString = token;
                for (int a = 0; a < 3; a++) {
                    UserConfig userConfig = UserConfig.getInstance(a);
                    userConfig.registeredForPush = false;
                    userConfig.saveConfig(false);
                    if (userConfig.getClientUserId() != 0) {
                        final int currentAccount = a;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.getInstance(currentAccount).registerForPush(token);
                            }
                        });
                    }
                }
            }
        });
    }
}
