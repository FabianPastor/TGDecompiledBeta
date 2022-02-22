package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_auth_authorization;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ LoginActivity f$0;
    public final /* synthetic */ TLRPC$TL_auth_authorization f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda16(LoginActivity loginActivity, TLRPC$TL_auth_authorization tLRPC$TL_auth_authorization, boolean z) {
        this.f$0 = loginActivity;
        this.f$1 = tLRPC$TL_auth_authorization;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$onAuthSuccess$16(this.f$1, this.f$2);
    }
}
