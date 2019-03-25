package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShareBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (url != null) {
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType("text/plain");
            shareIntent.putExtra("android.intent.extra.TEXT", url);
            Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", NUM));
            chooserIntent.setFlags(NUM);
            context.startActivity(chooserIntent);
        }
    }
}
