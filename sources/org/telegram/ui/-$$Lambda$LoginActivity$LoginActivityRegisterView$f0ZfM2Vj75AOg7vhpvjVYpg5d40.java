package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRegisterView$f0ZfM2Vj75AOg7vhpvjVYpg5d40 implements RequestDelegate {
    private final /* synthetic */ LoginActivityRegisterView f$0;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRegisterView$f0ZfM2Vj75AOg7vhpvjVYpg5d40(LoginActivityRegisterView loginActivityRegisterView) {
        this.f$0 = loginActivityRegisterView;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$13$LoginActivity$LoginActivityRegisterView(tLObject, tL_error);
    }
}
