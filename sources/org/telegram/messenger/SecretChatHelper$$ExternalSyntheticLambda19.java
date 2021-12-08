package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda19 implements RequestDelegate {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ TLRPC.User f$3;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda19(SecretChatHelper secretChatHelper, Context context, AlertDialog alertDialog, TLRPC.User user) {
        this.f$0 = secretChatHelper;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = user;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1155x98b25de3(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
