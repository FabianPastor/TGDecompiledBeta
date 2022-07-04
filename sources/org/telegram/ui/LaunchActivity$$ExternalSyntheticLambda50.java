package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda50 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLRPC.TL_help_appUpdate f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda50(LaunchActivity launchActivity, TLRPC.TL_help_appUpdate tL_help_appUpdate, int i) {
        this.f$0 = launchActivity;
        this.f$1 = tL_help_appUpdate;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m3627lambda$checkAppUpdate$73$orgtelegramuiLaunchActivity(this.f$1, this.f$2);
    }
}
