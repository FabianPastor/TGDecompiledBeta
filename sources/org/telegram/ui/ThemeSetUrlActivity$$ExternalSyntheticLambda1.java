package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_account_updateTheme f$2;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda1(ThemeSetUrlActivity themeSetUrlActivity, TLRPC.TL_error tL_error, TLRPC.TL_account_updateTheme tL_account_updateTheme) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = tL_error;
        this.f$2 = tL_account_updateTheme;
    }

    public final void run() {
        this.f$0.m3338lambda$saveTheme$11$orgtelegramuiThemeSetUrlActivity(this.f$1, this.f$2);
    }
}
