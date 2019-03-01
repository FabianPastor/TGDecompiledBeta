package org.telegram.messenger;

final /* synthetic */ class FileLoadOperation$$Lambda$11 implements Runnable {
    private final FileLoadOperation arg$1;
    private final RequestInfo arg$2;

    FileLoadOperation$$Lambda$11(FileLoadOperation fileLoadOperation, RequestInfo requestInfo) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = requestInfo;
    }

    public void run() {
        this.arg$1.lambda$startDownloadRequest$10$FileLoadOperation(this.arg$2);
    }
}
