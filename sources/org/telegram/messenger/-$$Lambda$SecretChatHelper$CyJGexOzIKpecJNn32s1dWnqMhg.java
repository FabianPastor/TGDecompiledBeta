package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$CyJGexOzIKpecJNn32s1dWnqMhg implements RequestDelegate {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ Context f$1;
    private final /* synthetic */ AlertDialog f$2;
    private final /* synthetic */ User f$3;

    public /* synthetic */ -$$Lambda$SecretChatHelper$CyJGexOzIKpecJNn32s1dWnqMhg(SecretChatHelper secretChatHelper, Context context, AlertDialog alertDialog, User user) {
        this.f$0 = secretChatHelper;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = user;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$startSecretChat$29$SecretChatHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
