package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.exoplayer2.C0020C;

public class ShareBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (url != null) {
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType("text/plain");
            shareIntent.putExtra("android.intent.extra.TEXT", url);
            Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", C0431R.string.ShareLink));
            chooserIntent.setFlags(C0020C.ENCODING_PCM_MU_LAW);
            context.startActivity(chooserIntent);
        }
    }
}
