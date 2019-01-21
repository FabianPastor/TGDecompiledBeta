package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class LaunchActivity$$Lambda$17 implements RequestDelegate {
    private final LaunchActivity arg$1;
    private final int arg$2;

    LaunchActivity$$Lambda$17(LaunchActivity launchActivity, int i) {
        this.arg$1 = launchActivity;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$checkAppUpdate$31$LaunchActivity(this.arg$2, tLObject, tL_error);
    }
}
