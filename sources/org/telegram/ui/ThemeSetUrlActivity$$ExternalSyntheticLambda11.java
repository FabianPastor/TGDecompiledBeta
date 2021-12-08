package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_theme;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ TLRPC$TL_theme f$1;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda11(ThemeSetUrlActivity themeSetUrlActivity, TLRPC$TL_theme tLRPC$TL_theme) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = tLRPC$TL_theme;
    }

    public final void run() {
        this.f$0.lambda$saveTheme$10(this.f$1);
    }
}
