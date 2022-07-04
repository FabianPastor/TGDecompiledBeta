package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_account_updateTheme;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$TL_account_updateTheme f$2;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda10(ThemeSetUrlActivity themeSetUrlActivity, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_updateTheme tLRPC$TL_account_updateTheme) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$TL_account_updateTheme;
    }

    public final void run() {
        this.f$0.lambda$saveTheme$11(this.f$1, this.f$2);
    }
}
