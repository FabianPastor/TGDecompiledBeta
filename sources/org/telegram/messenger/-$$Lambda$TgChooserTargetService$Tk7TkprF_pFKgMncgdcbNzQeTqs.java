package org.telegram.messenger;

import android.content.ComponentName;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TgChooserTargetService$Tk7TkprF_pFKgMncgdcbNzQeTqs implements Runnable {
    private final /* synthetic */ TgChooserTargetService f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ List f$2;
    private final /* synthetic */ ComponentName f$3;
    private final /* synthetic */ CountDownLatch f$4;

    public /* synthetic */ -$$Lambda$TgChooserTargetService$Tk7TkprF_pFKgMncgdcbNzQeTqs(TgChooserTargetService tgChooserTargetService, int i, List list, ComponentName componentName, CountDownLatch countDownLatch) {
        this.f$0 = tgChooserTargetService;
        this.f$1 = i;
        this.f$2 = list;
        this.f$3 = componentName;
        this.f$4 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$onGetChooserTargets$0$TgChooserTargetService(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
