package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRegisterView$7BLOUX6Psn1I67qrNVHHeqDT7F0 implements Runnable {
    private final /* synthetic */ LoginActivityRegisterView f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRegisterView$7BLOUX6Psn1I67qrNVHHeqDT7F0(LoginActivityRegisterView loginActivityRegisterView, TL_error tL_error, TLObject tLObject) {
        this.f$0 = loginActivityRegisterView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$12$LoginActivity$LoginActivityRegisterView(this.f$1, this.f$2);
    }
}
