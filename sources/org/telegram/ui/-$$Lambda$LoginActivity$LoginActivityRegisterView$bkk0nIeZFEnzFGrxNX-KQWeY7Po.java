package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRegisterView$bkk0nIeZFEnzFGrxNX-KQWeY7Po implements Runnable {
    private final /* synthetic */ LoginActivityRegisterView f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ TL_error f$2;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRegisterView$bkk0nIeZFEnzFGrxNX-KQWeY7Po(LoginActivityRegisterView loginActivityRegisterView, TLObject tLObject, TL_error tL_error) {
        this.f$0 = loginActivityRegisterView;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
    }

    public final void run() {
        this.f$0.lambda$null$13$LoginActivity$LoginActivityRegisterView(this.f$1, this.f$2);
    }
}
