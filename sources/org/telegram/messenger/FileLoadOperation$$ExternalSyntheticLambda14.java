package org.telegram.messenger;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class FileLoadOperation$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ FileLoadOperation f$0;
    public final /* synthetic */ File[] f$1;
    public final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ FileLoadOperation$$ExternalSyntheticLambda14(FileLoadOperation fileLoadOperation, File[] fileArr, CountDownLatch countDownLatch) {
        this.f$0 = fileLoadOperation;
        this.f$1 = fileArr;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        this.f$0.m619lambda$getCurrentFile$1$orgtelegrammessengerFileLoadOperation(this.f$1, this.f$2);
    }
}
