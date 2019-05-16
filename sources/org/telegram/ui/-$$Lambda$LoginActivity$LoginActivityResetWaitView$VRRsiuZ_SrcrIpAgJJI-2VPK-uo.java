package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityResetWaitView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityResetWaitView$VRRsiuZ_SrcrIpAgJJI-2VPK-uo implements RequestDelegate {
    private final /* synthetic */ LoginActivityResetWaitView f$0;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityResetWaitView$VRRsiuZ_SrcrIpAgJJI-2VPK-uo(LoginActivityResetWaitView loginActivityResetWaitView) {
        this.f$0 = loginActivityResetWaitView;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$1$LoginActivity$LoginActivityResetWaitView(tLObject, tL_error);
    }
}
