package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda87 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda87(LaunchActivity launchActivity, Theme.ThemeInfo themeInfo) {
        this.f$0 = launchActivity;
        this.f$1 = themeInfo;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3637lambda$didReceivedNotification$91$orgtelegramuiLaunchActivity(this.f$1, tLObject, tL_error);
    }
}
