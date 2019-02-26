package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.Document;

final /* synthetic */ class FileLoader$$Lambda$8 implements Runnable {
    private final FileLoader arg$1;
    private final FileLoadOperation[] arg$2;
    private final Document arg$3;
    private final Object arg$4;
    private final FileLoadOperationStream arg$5;
    private final int arg$6;
    private final CountDownLatch arg$7;

    FileLoader$$Lambda$8(FileLoader fileLoader, FileLoadOperation[] fileLoadOperationArr, Document document, Object obj, FileLoadOperationStream fileLoadOperationStream, int i, CountDownLatch countDownLatch) {
        this.arg$1 = fileLoader;
        this.arg$2 = fileLoadOperationArr;
        this.arg$3 = document;
        this.arg$4 = obj;
        this.arg$5 = fileLoadOperationStream;
        this.arg$6 = i;
        this.arg$7 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$loadStreamFile$8$FileLoader(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
