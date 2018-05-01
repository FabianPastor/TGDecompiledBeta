package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import java.util.regex.Pattern;

public class SmsListener extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") == null) {
            context = intent.getAction().equals("android.provider.Telephony.NEW_OUTGOING_SMS");
            if (context != null) {
            }
        }
        context = null;
        if (AndroidUtilities.isWaitingForSms()) {
            intent = intent.getExtras();
            if (intent != null) {
                try {
                    Object[] objArr = (Object[]) intent.get("pdus");
                    SmsMessage[] smsMessageArr = new SmsMessage[objArr.length];
                    CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                    for (int i = 0; i < smsMessageArr.length; i++) {
                        smsMessageArr[i] = SmsMessage.createFromPdu((byte[]) objArr[i]);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(charSequence);
                        stringBuilder.append(smsMessageArr[i].getMessageBody());
                        charSequence = stringBuilder.toString();
                    }
                    if (context == null) {
                        context = Pattern.compile("[0-9]+").matcher(charSequence);
                        if (context.find() != null && context.group(0).length() >= 3) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, context.group(0));
                                }
                            });
                        }
                    }
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
        }
    }
}
