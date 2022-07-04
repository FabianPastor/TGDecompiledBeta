package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda3(ThemeSetUrlActivity themeSetUrlActivity, String str) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4696lambda$checkUrl$7$orgtelegramuiThemeSetUrlActivity(this.f$1, tLObject, tL_error);
    }
}
