package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda64 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda64(LaunchActivity launchActivity, Theme.ThemeInfo themeInfo) {
        this.f$0 = launchActivity;
        this.f$1 = themeInfo;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3071lambda$didReceivedNotification$73$orgtelegramuiLaunchActivity(this.f$1, tLObject, tL_error);
    }
}
