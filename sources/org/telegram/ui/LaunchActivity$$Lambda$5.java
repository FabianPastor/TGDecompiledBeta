package org.telegram.ui;

import android.os.Bundle;

final /* synthetic */ class LaunchActivity$$Lambda$5 implements Runnable {
    private final LaunchActivity arg$1;
    private final Bundle arg$2;

    LaunchActivity$$Lambda$5(LaunchActivity launchActivity, Bundle bundle) {
        this.arg$1 = launchActivity;
        this.arg$2 = bundle;
    }

    public void run() {
        this.arg$1.lambda$handleIntent$5$LaunchActivity(this.arg$2);
    }
}
