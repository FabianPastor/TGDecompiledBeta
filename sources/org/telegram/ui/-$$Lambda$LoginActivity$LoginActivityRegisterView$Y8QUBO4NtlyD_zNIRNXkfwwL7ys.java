package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRegisterView$Y8QUBO4NtlyD_zNIRNXkfwwL7ys implements RequestDelegate {
    private final /* synthetic */ LoginActivityRegisterView f$0;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRegisterView$Y8QUBO4NtlyD_zNIRNXkfwwL7ys(LoginActivityRegisterView loginActivityRegisterView) {
        this.f$0 = loginActivityRegisterView;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$14$LoginActivity$LoginActivityRegisterView(tLObject, tL_error);
    }
}
