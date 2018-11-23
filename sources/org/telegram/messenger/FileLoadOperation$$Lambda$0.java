package org.telegram.messenger;

import java.io.File;
import java.util.concurrent.CountDownLatch;

final /* synthetic */ class FileLoadOperation$$Lambda$0 implements Runnable {
    private final FileLoadOperation arg$1;
    private final File[] arg$2;
    private final CountDownLatch arg$3;

    FileLoadOperation$$Lambda$0(FileLoadOperation fileLoadOperation, File[] fileArr, CountDownLatch countDownLatch) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = fileArr;
        this.arg$3 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getCurrentFile$0$FileLoadOperation(this.arg$2, this.arg$3);
    }
}
