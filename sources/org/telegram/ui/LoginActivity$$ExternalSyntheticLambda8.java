package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ LoginActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda8(LoginActivity loginActivity, TLRPC.TL_error tL_error, String str, String str2, String str3) {
        this.f$0 = loginActivity;
        this.f$1 = tL_error;
        this.f$2 = str;
        this.f$3 = str2;
        this.f$4 = str3;
    }

    public final void run() {
        this.f$0.m2458lambda$tryResetAccount$18$orgtelegramuiLoginActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
