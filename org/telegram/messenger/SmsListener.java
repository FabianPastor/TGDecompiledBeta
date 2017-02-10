package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsListener extends BroadcastReceiver {
    private SharedPreferences preferences;

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") && AndroidUtilities.isWaitingForSms()) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] msgs = new SmsMessage[pdus.length];
                    String wholeString = "";
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        wholeString = wholeString + msgs[i].getMessageBody();
                    }
                    final Matcher matcher = Pattern.compile("[0-9]+").matcher(wholeString);
                    if (matcher.find() && matcher.group(0).length() >= 3) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, matcher.group(0));
                            }
                        });
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
    }
}
