package org.telegram.messenger;

import android.content.Context;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class SecretChatHelper$$Lambda$17 implements Runnable {
    private final SecretChatHelper arg$1;
    private final Context arg$2;
    private final AlertDialog arg$3;

    SecretChatHelper$$Lambda$17(SecretChatHelper secretChatHelper, Context context, AlertDialog alertDialog) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = context;
        this.arg$3 = alertDialog;
    }

    public void run() {
        this.arg$1.lambda$null$28$SecretChatHelper(this.arg$2, this.arg$3);
    }
}
