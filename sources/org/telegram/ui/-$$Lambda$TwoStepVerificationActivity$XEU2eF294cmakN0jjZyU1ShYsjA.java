package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TwoStepVerificationActivity$XEU2evar_cmakN0jjZyU1ShYsjA implements RequestDelegate {
    private final /* synthetic */ TwoStepVerificationActivity f$0;

    public /* synthetic */ -$$Lambda$TwoStepVerificationActivity$XEU2evar_cmakN0jjZyU1ShYsjA(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = twoStepVerificationActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$setNewPassword$18$TwoStepVerificationActivity(tLObject, tL_error);
    }
}
