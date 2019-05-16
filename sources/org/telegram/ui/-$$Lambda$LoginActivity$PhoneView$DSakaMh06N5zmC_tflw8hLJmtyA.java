package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_sendCode;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.PhoneView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$PhoneView$DSakaMh06N5zmC_tflw8hLJmtyA implements Runnable {
    private final /* synthetic */ PhoneView f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ Bundle f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ TL_auth_sendCode f$4;

    public /* synthetic */ -$$Lambda$LoginActivity$PhoneView$DSakaMh06N5zmC_tflw8hLJmtyA(PhoneView phoneView, TL_error tL_error, Bundle bundle, TLObject tLObject, TL_auth_sendCode tL_auth_sendCode) {
        this.f$0 = phoneView;
        this.f$1 = tL_error;
        this.f$2 = bundle;
        this.f$3 = tLObject;
        this.f$4 = tL_auth_sendCode;
    }

    public final void run() {
        this.f$0.lambda$null$7$LoginActivity$PhoneView(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
