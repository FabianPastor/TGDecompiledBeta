package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda13(ThemeSetUrlActivity themeSetUrlActivity, String str, TLRPC.TL_error tL_error) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = str;
        this.f$2 = tL_error;
    }

    public final void run() {
        this.f$0.m4695lambda$checkUrl$6$orgtelegramuiThemeSetUrlActivity(this.f$1, this.f$2);
    }
}
