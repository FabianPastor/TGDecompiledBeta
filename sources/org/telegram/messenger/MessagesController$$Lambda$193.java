package org.telegram.messenger;

import android.content.Context;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class MessagesController$$Lambda$193 implements Runnable {
    private final Context arg$1;
    private final AlertDialog arg$2;

    MessagesController$$Lambda$193(Context context, AlertDialog alertDialog) {
        this.arg$1 = context;
        this.arg$2 = alertDialog;
    }

    public void run() {
        MessagesController.lambda$null$141$MessagesController(this.arg$1, this.arg$2);
    }
}
