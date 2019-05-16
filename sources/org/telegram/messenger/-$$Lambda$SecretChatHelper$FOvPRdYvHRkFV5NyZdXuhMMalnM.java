package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$FOvPRdYvHRkFV5NyZdXuhMMalnM implements OnCancelListener {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$SecretChatHelper$FOvPRdYvHRkFV5NyZdXuhMMalnM(SecretChatHelper secretChatHelper, int i) {
        this.f$0 = secretChatHelper;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$startSecretChat$30$SecretChatHelper(this.f$1, dialogInterface);
    }
}
