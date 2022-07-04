package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda69 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda69(LaunchActivity launchActivity, int i) {
        this.f$0 = launchActivity;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3628lambda$checkAppUpdate$74$orgtelegramuiLaunchActivity(this.f$1, tLObject, tL_error);
    }
}
