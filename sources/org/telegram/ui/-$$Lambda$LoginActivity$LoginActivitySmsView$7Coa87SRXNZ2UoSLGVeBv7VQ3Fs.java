package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivitySmsView$7Coa87SRXNZ2UoSLGVeBv7VQ3Fs implements RequestDelegate {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ Bundle f$1;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivitySmsView$7Coa87SRXNZ2UoSLGVeBv7VQ3Fs(LoginActivitySmsView loginActivitySmsView, Bundle bundle) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = bundle;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$resendCode$2$LoginActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
    }
}
