package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String str;
        if (intent != null) {
            try {
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String string = sharedPreferences.getString("sms_hash", (String) null);
                if (!"com.google.android.gms.auth.api.phone.SMS_RETRIEVED".equals(intent.getAction())) {
                    str = "";
                } else if (AndroidUtilities.isWaitingForSms()) {
                    str = (String) intent.getExtras().get("com.google.android.gms.auth.api.phone.EXTRA_SMS_MESSAGE");
                } else {
                    return;
                }
                if (!TextUtils.isEmpty(str)) {
                    Matcher matcher = Pattern.compile("[0-9\\-]+").matcher(str);
                    if (matcher.find()) {
                        String replace = matcher.group(0).replace("-", "");
                        if (replace.length() >= 3) {
                            if (string != null) {
                                SharedPreferences.Editor edit = sharedPreferences.edit();
                                edit.putString("sms_hash_code", string + "|" + replace).commit();
                            }
                            AndroidUtilities.runOnUIThread(new Runnable(replace) {
                                public final /* synthetic */ String f$0;

                                {
                                    this.f$0 = r1;
                                }

                                public final void run() {
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, this.f$0);
                                }
                            });
                        }
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }
}
