package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import org.telegram.PhoneFormat.PhoneFormat;

public class CallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE") && intent.getStringExtra("state").equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String phoneNumber = intent.getStringExtra("incoming_number");
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveCall, PhoneFormat.stripExceptNumbers(phoneNumber));
        }
    }
}
