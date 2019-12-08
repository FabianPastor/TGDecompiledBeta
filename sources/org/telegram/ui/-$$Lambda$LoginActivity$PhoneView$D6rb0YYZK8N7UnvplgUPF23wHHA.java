package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_sendCode;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.PhoneView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$PhoneView$D6rb0YYZK8N7UnvplgUPvar_wHHA implements RequestDelegate {
    private final /* synthetic */ PhoneView f$0;
    private final /* synthetic */ Bundle f$1;
    private final /* synthetic */ TL_auth_sendCode f$2;

    public /* synthetic */ -$$Lambda$LoginActivity$PhoneView$D6rb0YYZK8N7UnvplgUPvar_wHHA(PhoneView phoneView, Bundle bundle, TL_auth_sendCode tL_auth_sendCode) {
        this.f$0 = phoneView;
        this.f$1 = bundle;
        this.f$2 = tL_auth_sendCode;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$10$LoginActivity$PhoneView(this.f$1, this.f$2, tLObject, tL_error);
    }
}
