package org.telegram.messenger;

import android.content.Context;
import org.telegram.p005ui.ActionBar.AlertDialog;

final /* synthetic */ class MessagesController$$Lambda$198 implements Runnable {
    private final Context arg$1;
    private final AlertDialog arg$2;

    MessagesController$$Lambda$198(Context context, AlertDialog alertDialog) {
        this.arg$1 = context;
        this.arg$2 = alertDialog;
    }

    public void run() {
        MessagesController.lambda$null$148$MessagesController(this.arg$1, this.arg$2);
    }
}
