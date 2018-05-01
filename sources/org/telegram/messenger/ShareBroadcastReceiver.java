package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShareBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        intent = intent.getDataString();
        if (intent != null) {
            Intent intent2 = new Intent("android.intent.action.SEND");
            intent2.setType("text/plain");
            intent2.putExtra("android.intent.extra.TEXT", intent);
            intent = Intent.createChooser(intent2, LocaleController.getString("ShareLink", C0446R.string.ShareLink));
            intent.setFlags(268435456);
            context.startActivity(intent);
        }
    }
}
