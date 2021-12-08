package org.telegram.ui.ActionBar;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CountDownLatch f$0;

    public /* synthetic */ Theme$$ExternalSyntheticLambda2(CountDownLatch countDownLatch) {
        this.f$0 = countDownLatch;
    }

    public final void run() {
        this.f$0.countDown();
    }
}
