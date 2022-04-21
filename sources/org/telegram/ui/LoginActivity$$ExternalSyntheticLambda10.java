package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ LoginActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda10(LoginActivity loginActivity, String str, String str2, String str3) {
        this.f$0 = loginActivity;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = str3;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2459lambda$tryResetAccount$19$orgtelegramuiLoginActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
