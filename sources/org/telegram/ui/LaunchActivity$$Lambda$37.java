package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_help_appUpdate;

final /* synthetic */ class LaunchActivity$$Lambda$37 implements Runnable {
    private final LaunchActivity arg$1;
    private final TL_help_appUpdate arg$2;
    private final int arg$3;

    LaunchActivity$$Lambda$37(LaunchActivity launchActivity, TL_help_appUpdate tL_help_appUpdate, int i) {
        this.arg$1 = launchActivity;
        this.arg$2 = tL_help_appUpdate;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$26$LaunchActivity(this.arg$2, this.arg$3);
    }
}
