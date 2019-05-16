package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$BEU71I5KzZcukvSdHQ6G9fRsUbQ implements RequestDelegate {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ Context f$1;
    private final /* synthetic */ AlertDialog f$2;
    private final /* synthetic */ byte[] f$3;
    private final /* synthetic */ User f$4;

    public /* synthetic */ -$$Lambda$SecretChatHelper$BEU71I5KzZcukvSdHQ6G9fRsUbQ(SecretChatHelper secretChatHelper, Context context, AlertDialog alertDialog, byte[] bArr, User user) {
        this.f$0 = secretChatHelper;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = bArr;
        this.f$4 = user;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$27$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
