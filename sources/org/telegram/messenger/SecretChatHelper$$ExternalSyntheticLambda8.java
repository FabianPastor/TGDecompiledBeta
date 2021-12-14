package org.telegram.messenger;

import android.content.Context;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ AlertDialog f$2;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda8(SecretChatHelper secretChatHelper, Context context, AlertDialog alertDialog) {
        this.f$0 = secretChatHelper;
        this.f$1 = context;
        this.f$2 = alertDialog;
    }

    public final void run() {
        this.f$0.lambda$startSecretChat$27(this.f$1, this.f$2);
    }
}
