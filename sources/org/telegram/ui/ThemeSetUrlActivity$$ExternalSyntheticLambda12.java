package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda12 implements RequestDelegate {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda12(ThemeSetUrlActivity themeSetUrlActivity, String str) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkUrl$7(this.f$1, tLObject, tLRPC$TL_error);
    }
}
