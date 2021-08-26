package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_help_appUpdate;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLRPC$TL_help_appUpdate f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda41(LaunchActivity launchActivity, TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
        this.f$0 = launchActivity;
        this.f$1 = tLRPC$TL_help_appUpdate;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$checkAppUpdate$56(this.f$1, this.f$2);
    }
}
