package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class FilePathDatabase$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ FilePathDatabase f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean[] f$2;
    public final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ FilePathDatabase$$ExternalSyntheticLambda5(FilePathDatabase filePathDatabase, String str, boolean[] zArr, CountDownLatch countDownLatch) {
        this.f$0 = filePathDatabase;
        this.f$1 = str;
        this.f$2 = zArr;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$hasAnotherRefOnFile$5(this.f$1, this.f$2, this.f$3);
    }
}
