package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda9(ThemeSetUrlActivity themeSetUrlActivity, String str, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = str;
        this.f$2 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$checkUrl$6(this.f$1, this.f$2);
    }
}
