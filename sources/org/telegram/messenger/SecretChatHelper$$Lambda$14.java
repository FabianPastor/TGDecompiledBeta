package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

final /* synthetic */ class SecretChatHelper$$Lambda$14 implements OnCancelListener {
    private final SecretChatHelper arg$1;
    private final int arg$2;

    SecretChatHelper$$Lambda$14(SecretChatHelper secretChatHelper, int i) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$startSecretChat$30$SecretChatHelper(this.arg$2, dialogInterface);
    }
}
