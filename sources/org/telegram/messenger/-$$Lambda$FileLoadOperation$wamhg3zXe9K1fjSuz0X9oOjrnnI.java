package org.telegram.messenger;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$wamhg3zXe9K1fjSuz0X9oOjrnnI implements Runnable {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ File[] f$1;
    private final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ -$$Lambda$FileLoadOperation$wamhg3zXe9K1fjSuz0X9oOjrnnI(FileLoadOperation fileLoadOperation, File[] fileArr, CountDownLatch countDownLatch) {
        this.f$0 = fileLoadOperation;
        this.f$1 = fileArr;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getCurrentFile$1$FileLoadOperation(this.f$1, this.f$2);
    }
}
