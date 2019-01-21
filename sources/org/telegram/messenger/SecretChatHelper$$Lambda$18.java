package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class SecretChatHelper$$Lambda$18 implements Runnable {
    private final SecretChatHelper arg$1;
    private final Context arg$2;
    private final AlertDialog arg$3;
    private final TLObject arg$4;
    private final byte[] arg$5;
    private final User arg$6;

    SecretChatHelper$$Lambda$18(SecretChatHelper secretChatHelper, Context context, AlertDialog alertDialog, TLObject tLObject, byte[] bArr, User user) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = context;
        this.arg$3 = alertDialog;
        this.arg$4 = tLObject;
        this.arg$5 = bArr;
        this.arg$6 = user;
    }

    public void run() {
        this.arg$1.lambda$null$25$SecretChatHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
