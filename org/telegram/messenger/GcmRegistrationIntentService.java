package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import org.telegram.messenger.beta.R;

public class GcmRegistrationIntentService extends IntentService {
    public GcmRegistrationIntentService() {
        super("GcmRegistrationIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        try {
            final String token = InstanceID.getInstance(this).getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            FileLog.d("GCM Registration Token: " + token);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ApplicationLoader.postInitApplication();
                    GcmRegistrationIntentService.this.sendRegistrationToServer(token);
                }
            });
        } catch (Throwable e) {
            FileLog.e(e);
            if (intent != null) {
                final int failCount = intent.getIntExtra("failCount", 0);
                if (failCount < 60) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            try {
                                Intent intent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
                                intent.putExtra("failCount", failCount + 1);
                                GcmRegistrationIntentService.this.startService(intent);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    }, failCount < 20 ? 10000 : 1800000);
                }
            }
        }
    }

    private void sendRegistrationToServer(final String token) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                UserConfig.pushString = token;
                UserConfig.registeredForPush = false;
                UserConfig.saveConfig(false);
                if (UserConfig.getClientUserId() != 0) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessagesController.getInstance().registerForPush(token);
                        }
                    });
                }
            }
        });
    }
}
