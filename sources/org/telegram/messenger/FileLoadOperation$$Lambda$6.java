package org.telegram.messenger;

final /* synthetic */ class FileLoadOperation$$Lambda$6 implements Runnable {
    private final FileLoadOperation arg$1;
    private final boolean arg$2;

    FileLoadOperation$$Lambda$6(FileLoadOperation fileLoadOperation, boolean z) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$setIsPreloadVideoOperation$5$FileLoadOperation(this.arg$2);
    }
}
