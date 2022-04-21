package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$3$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.AnonymousClass3 f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ PassportActivity.ErrorRunnable f$2;
    public final /* synthetic */ TLRPC.TL_account_verifyEmail f$3;

    public /* synthetic */ PassportActivity$3$$ExternalSyntheticLambda6(PassportActivity.AnonymousClass3 r1, Runnable runnable, PassportActivity.ErrorRunnable errorRunnable, TLRPC.TL_account_verifyEmail tL_account_verifyEmail) {
        this.f$0 = r1;
        this.f$1 = runnable;
        this.f$2 = errorRunnable;
        this.f$3 = tL_account_verifyEmail;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2777lambda$onItemClick$6$orgtelegramuiPassportActivity$3(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
