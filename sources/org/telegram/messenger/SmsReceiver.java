package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony.Sms.Intents;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TargetApi(26)
public class SmsReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            try {
                String message = "";
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String hash = preferences.getString("sms_hash", null);
                if ("com.google.android.gms.auth.api.phone.SMS_RETRIEVED".equals(intent.getAction())) {
                    if (AndroidUtilities.isWaitingForSms()) {
                        message = (String) intent.getExtras().get("com.google.android.gms.auth.api.phone.EXTRA_SMS_MESSAGE");
                    } else {
                        return;
                    }
                } else if (!TextUtils.isEmpty(hash)) {
                    SmsMessage[] msgs = Intents.getMessagesFromIntent(intent);
                    if (msgs != null && msgs.length > 0) {
                        for (SmsMessage messageBody : msgs) {
                            message = message + messageBody.getMessageBody();
                        }
                        if (!message.contains(hash)) {
                            return;
                        }
                    }
                    return;
                } else {
                    return;
                }
                if (!TextUtils.isEmpty(message)) {
                    Matcher matcher = Pattern.compile("[0-9\\-]+").matcher(message);
                    if (matcher.find()) {
                        String code = matcher.group(0).replace("-", "");
                        if (code.length() >= 3) {
                            if (!(preferences == null || hash == null)) {
                                preferences.edit().putString("sms_hash_code", hash + "|" + code).commit();
                            }
                            AndroidUtilities.runOnUIThread(new SmsReceiver$$Lambda$0(code));
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }
}
