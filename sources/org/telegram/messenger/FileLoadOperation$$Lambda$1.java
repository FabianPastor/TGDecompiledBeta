package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class FileLoadOperation$$Lambda$1 implements Runnable {
    private final FileLoadOperation arg$1;
    private final int[] arg$2;
    private final int arg$3;
    private final int arg$4;
    private final CountDownLatch arg$5;

    FileLoadOperation$$Lambda$1(FileLoadOperation fileLoadOperation, int[] iArr, int i, int i2, CountDownLatch countDownLatch) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = iArr;
        this.arg$3 = i;
        this.arg$4 = i2;
        this.arg$5 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getDownloadedLengthFromOffset$1$FileLoadOperation(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
