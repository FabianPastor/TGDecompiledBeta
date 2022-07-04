package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class FilePathDatabase$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ FilePathDatabase f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String[] f$4;
    public final /* synthetic */ CountDownLatch f$5;

    public /* synthetic */ FilePathDatabase$$ExternalSyntheticLambda4(FilePathDatabase filePathDatabase, long j, int i, int i2, String[] strArr, CountDownLatch countDownLatch) {
        this.f$0 = filePathDatabase;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = strArr;
        this.f$5 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getPath$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
