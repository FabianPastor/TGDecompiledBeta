package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$0tV_MJuVJAhZ10ST8ytcL1B6vB4 implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ Context f$1;
    private final /* synthetic */ AlertDialog f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ byte[] f$4;
    private final /* synthetic */ User f$5;

    public /* synthetic */ -$$Lambda$SecretChatHelper$0tV_MJuVJAhZ10ST8ytcL1B6vB4(SecretChatHelper secretChatHelper, Context context, AlertDialog alertDialog, TLObject tLObject, byte[] bArr, User user) {
        this.f$0 = secretChatHelper;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = tLObject;
        this.f$4 = bArr;
        this.f$5 = user;
    }

    public final void run() {
        this.f$0.lambda$null$25$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
