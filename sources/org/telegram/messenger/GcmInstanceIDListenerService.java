package org.telegram.messenger;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GcmInstanceIDListenerService extends FirebaseInstanceIdService {
    public void onTokenRefresh() {
        try {
            final String token = FirebaseInstanceId.getInstance().getToken();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Refreshed token: ");
                        stringBuilder.append(token);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    ApplicationLoader.postInitApplication();
                    GcmInstanceIDListenerService.sendRegistrationToServer(token);
                }
            });
        } catch (Throwable th) {
            FileLog.m3e(th);
        }
    }

    public static void sendRegistrationToServer(final String str) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                SharedConfig.pushString = str;
                for (int i = 0; i < 3; i++) {
                    UserConfig instance = UserConfig.getInstance(i);
                    instance.registeredForPush = false;
                    instance.saveConfig(false);
                    if (instance.getClientUserId() != 0) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.getInstance(i).registerForPush(str);
                            }
                        });
                    }
                }
            }
        });
    }
}
