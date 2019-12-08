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
            try {
                CharSequence charSequence;
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String string = sharedPreferences.getString("sms_hash", null);
                String str = "";
                if (!"com.google.android.gms.auth.api.phone.SMS_RETRIEVED".equals(intent.getAction())) {
                    charSequence = str;
                } else if (AndroidUtilities.isWaitingForSms()) {
                    charSequence = (String) intent.getExtras().get("com.google.android.gms.auth.api.phone.EXTRA_SMS_MESSAGE");
                } else {
                    return;
                }
                if (!TextUtils.isEmpty(charSequence)) {
                    Matcher matcher = Pattern.compile("[0-9\\-]+").matcher(charSequence);
                    if (matcher.find()) {
                        String replace = matcher.group(0).replace("-", str);
                        if (replace.length() >= 3) {
                            if (!(sharedPreferences == null || string == null)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(string);
                                stringBuilder.append("|");
                                stringBuilder.append(replace);
                                sharedPreferences.edit().putString("sms_hash_code", stringBuilder.toString()).commit();
                            }
                            AndroidUtilities.runOnUIThread(new -$$Lambda$SmsReceiver$bf6x7dIcCjvctQwFi_AQlRoL3UE(replace));
                        }
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }
}
