package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsListener extends BroadcastReceiver {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onReceive(Context context, Intent intent) {
        boolean outgoing = false;
        if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            boolean equals = intent.getAction().equals("android.provider.Telephony.NEW_OUTGOING_SMS");
            outgoing = equals;
        }
        if (AndroidUtilities.isWaitingForSms()) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] msgs = new SmsMessage[pdus.length];
                    String wholeString = TtmlNode.ANONYMOUS_REGION_ID;
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(wholeString);
                        stringBuilder.append(msgs[i].getMessageBody());
                        wholeString = stringBuilder.toString();
                    }
                    if (!outgoing) {
                        final Matcher matcher = Pattern.compile("[0-9]+").matcher(wholeString);
                        if (matcher.find() && matcher.group(0).length() >= 3) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, matcher.group(0));
                                }
                            });
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }
}
