package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda14 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ HashMap f$1;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda14(LoginActivity.PhoneView phoneView, HashMap hashMap) {
        this.f$0 = phoneView;
        this.f$1 = hashMap;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$9(this.f$1, tLObject, tLRPC$TL_error);
    }
}
