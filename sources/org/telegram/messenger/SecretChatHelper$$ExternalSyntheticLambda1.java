package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ byte[] f$4;
    public final /* synthetic */ TLRPC.User f$5;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda1(SecretChatHelper secretChatHelper, Context context, AlertDialog alertDialog, TLObject tLObject, byte[] bArr, TLRPC.User user) {
        this.f$0 = secretChatHelper;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = tLObject;
        this.f$4 = bArr;
        this.f$5 = user;
    }

    public final void run() {
        this.f$0.m2379xdd0e2aca(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
