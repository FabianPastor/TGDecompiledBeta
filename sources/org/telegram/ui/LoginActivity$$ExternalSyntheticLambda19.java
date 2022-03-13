package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda19 implements RequestDelegate {
    public final /* synthetic */ LoginActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda19(LoginActivity loginActivity, String str, String str2, String str3) {
        this.f$0 = loginActivity;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = str3;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$tryResetAccount$21(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
