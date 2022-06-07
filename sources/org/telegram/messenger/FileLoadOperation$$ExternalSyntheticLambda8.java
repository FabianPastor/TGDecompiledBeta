package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class FileLoadOperation$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ FileLoadOperation f$0;
    public final /* synthetic */ long[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ CountDownLatch f$4;

    public /* synthetic */ FileLoadOperation$$ExternalSyntheticLambda8(FileLoadOperation fileLoadOperation, long[] jArr, int i, long j, CountDownLatch countDownLatch) {
        this.f$0 = fileLoadOperation;
        this.f$1 = jArr;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getDownloadedLengthFromOffset$2(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
