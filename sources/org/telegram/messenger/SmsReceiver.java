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
        if (intent != null) {
            String message = "";
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String hash = preferences.getString("sms_hash", (String) null);
                if ("com.google.android.gms.auth.api.phone.SMS_RETRIEVED".equals(intent.getAction())) {
                    if (AndroidUtilities.isWaitingForSms()) {
                        message = (String) intent.getExtras().get("com.google.android.gms.auth.api.phone.EXTRA_SMS_MESSAGE");
                    } else {
                        return;
                    }
                }
                if (!TextUtils.isEmpty(message)) {
                    Matcher matcher = Pattern.compile("[0-9\\-]+").matcher(message);
                    if (matcher.find()) {
                        String code = matcher.group(0).replace("-", "");
                        if (code.length() >= 3) {
                            if (hash != null) {
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("sms_hash_code", hash + "|" + code).commit();
                            }
                            AndroidUtilities.runOnUIThread(new SmsReceiver$$ExternalSyntheticLambda0(code));
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }
}
