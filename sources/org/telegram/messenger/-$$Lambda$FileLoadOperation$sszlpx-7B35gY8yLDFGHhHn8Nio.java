package org.telegram.messenger;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$sszlpx-7B35gY8yLDFGHhHn8Nio implements Runnable {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ File[] f$1;
    private final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ -$$Lambda$FileLoadOperation$sszlpx-7B35gY8yLDFGHhHn8Nio(FileLoadOperation fileLoadOperation, File[] fileArr, CountDownLatch countDownLatch) {
        this.f$0 = fileLoadOperation;
        this.f$1 = fileArr;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getCurrentFile$0$FileLoadOperation(this.f$1, this.f$2);
    }
}
