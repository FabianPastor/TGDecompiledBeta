package org.telegram.messenger;

import android.content.ComponentName;
import java.util.List;
import java.util.concurrent.CountDownLatch;

final /* synthetic */ class TgChooserTargetService$$Lambda$0 implements Runnable {
    private final TgChooserTargetService arg$1;
    private final int arg$2;
    private final List arg$3;
    private final ComponentName arg$4;
    private final CountDownLatch arg$5;

    TgChooserTargetService$$Lambda$0(TgChooserTargetService tgChooserTargetService, int i, List list, ComponentName componentName, CountDownLatch countDownLatch) {
        this.arg$1 = tgChooserTargetService;
        this.arg$2 = i;
        this.arg$3 = list;
        this.arg$4 = componentName;
        this.arg$5 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onGetChooserTargets$0$TgChooserTargetService(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
