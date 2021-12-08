package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ TLRPC.TL_account_updateTheme f$1;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda4(ThemeSetUrlActivity themeSetUrlActivity, TLRPC.TL_account_updateTheme tL_account_updateTheme) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = tL_account_updateTheme;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3960lambda$saveTheme$12$orgtelegramuiThemeSetUrlActivity(this.f$1, tLObject, tL_error);
    }
}
