package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsListener extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        boolean outgoing = false;
        if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            outgoing = intent.getAction().equals("android.provider.Telephony.NEW_OUTGOING_SMS");
            if (!outgoing) {
                return;
            }
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
                        wholeString = wholeString + msgs[i].getMessageBody();
                    }
                    if (!outgoing) {
                        Matcher matcher = Pattern.compile("[0-9\\-]+").matcher(wholeString);
                        if (matcher.find()) {
                            String str = matcher.group(0).replace("-", TtmlNode.ANONYMOUS_REGION_ID);
                            if (str.length() >= 3) {
                                AndroidUtilities.runOnUIThread(new SmsListener$$Lambda$0(str));
                            }
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
        }
    }
}
