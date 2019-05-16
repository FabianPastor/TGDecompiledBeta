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
                CharSequence charSequence;
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String string = sharedPreferences.getString("sms_hash", null);
                String str = "";
                if ("com.google.android.gms.auth.api.phone.SMS_RETRIEVED".equals(intent.getAction())) {
                    if (AndroidUtilities.isWaitingForSms()) {
                        charSequence = (String) intent.getExtras().get("com.google.android.gms.auth.api.phone.EXTRA_SMS_MESSAGE");
                    } else {
                        return;
                    }
                } else if (!TextUtils.isEmpty(string)) {
                    SmsMessage[] messagesFromIntent = Intents.getMessagesFromIntent(intent);
                    if (messagesFromIntent != null) {
                        if (messagesFromIntent.length > 0) {
                            String str2 = str;
                            for (SmsMessage messageBody : messagesFromIntent) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(str2);
                                stringBuilder.append(messageBody.getMessageBody());
                                str2 = stringBuilder.toString();
                            }
                            if (str2.contains(string)) {
                                charSequence = str2;
                            } else {
                                return;
                            }
                        }
                    }
                    return;
                } else {
                    return;
                }
                if (!TextUtils.isEmpty(charSequence)) {
                    Matcher matcher = Pattern.compile("[0-9\\-]+").matcher(charSequence);
                    if (matcher.find()) {
                        String replace = matcher.group(0).replace("-", str);
                        if (replace.length() >= 3) {
                            if (!(sharedPreferences == null || string == null)) {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(string);
                                stringBuilder2.append("|");
                                stringBuilder2.append(replace);
                                sharedPreferences.edit().putString("sms_hash_code", stringBuilder2.toString()).commit();
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
