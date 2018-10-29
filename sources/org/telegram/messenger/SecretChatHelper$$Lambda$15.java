package org.telegram.messenger;

import android.content.Context;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class SecretChatHelper$$Lambda$15 implements Runnable {
    private final Context arg$1;
    private final AlertDialog arg$2;

    SecretChatHelper$$Lambda$15(Context context, AlertDialog alertDialog) {
        this.arg$1 = context;
        this.arg$2 = alertDialog;
    }

    public void run() {
        SecretChatHelper.lambda$null$23$SecretChatHelper(this.arg$1, this.arg$2);
    }
}
