package org.telegram.messenger;

final /* synthetic */ class FileLoadOperation$$Lambda$2 implements Runnable {
    private final FileLoadOperation arg$1;
    private final FileStreamLoadOperation arg$2;

    FileLoadOperation$$Lambda$2(FileLoadOperation fileLoadOperation, FileStreamLoadOperation fileStreamLoadOperation) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = fileStreamLoadOperation;
    }

    public void run() {
        this.arg$1.lambda$removeStreamListener$2$FileLoadOperation(this.arg$2);
    }
}
