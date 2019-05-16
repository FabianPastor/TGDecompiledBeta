package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$7ur7HYEmrvSDh4lwc7_mdrw1G5A implements Runnable {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ int[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ CountDownLatch f$4;

    public /* synthetic */ -$$Lambda$FileLoadOperation$7ur7HYEmrvSDh4lwc7_mdrw1G5A(FileLoadOperation fileLoadOperation, int[] iArr, int i, int i2, CountDownLatch countDownLatch) {
        this.f$0 = fileLoadOperation;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getDownloadedLengthFromOffset$1$FileLoadOperation(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
