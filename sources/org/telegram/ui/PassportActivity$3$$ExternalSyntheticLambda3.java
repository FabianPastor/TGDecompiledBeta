package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$3$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ PassportActivity.AnonymousClass3 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ Runnable f$2;
    public final /* synthetic */ PassportActivity.ErrorRunnable f$3;
    public final /* synthetic */ TLRPC.TL_account_verifyEmail f$4;

    public /* synthetic */ PassportActivity$3$$ExternalSyntheticLambda3(PassportActivity.AnonymousClass3 r1, TLRPC.TL_error tL_error, Runnable runnable, PassportActivity.ErrorRunnable errorRunnable, TLRPC.TL_account_verifyEmail tL_account_verifyEmail) {
        this.f$0 = r1;
        this.f$1 = tL_error;
        this.f$2 = runnable;
        this.f$3 = errorRunnable;
        this.f$4 = tL_account_verifyEmail;
    }

    public final void run() {
        this.f$0.m3434lambda$onItemClick$5$orgtelegramuiPassportActivity$3(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
