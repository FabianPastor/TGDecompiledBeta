package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_auth_authorization;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda19 implements Runnable {
    public final /* synthetic */ LoginActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC$TL_auth_authorization f$2;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda19(LoginActivity loginActivity, boolean z, TLRPC$TL_auth_authorization tLRPC$TL_auth_authorization) {
        this.f$0 = loginActivity;
        this.f$1 = z;
        this.f$2 = tLRPC$TL_auth_authorization;
    }

    public final void run() {
        this.f$0.lambda$onAuthSuccess$17(this.f$1, this.f$2);
    }
}
