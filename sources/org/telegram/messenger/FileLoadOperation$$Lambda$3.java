package org.telegram.messenger;

final /* synthetic */ class FileLoadOperation$$Lambda$3 implements Runnable {
    private final FileLoadOperation arg$1;
    private final int arg$2;
    private final FileLoadOperationStream arg$3;
    private final boolean arg$4;

    FileLoadOperation$$Lambda$3(FileLoadOperation fileLoadOperation, int i, FileLoadOperationStream fileLoadOperationStream, boolean z) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = i;
        this.arg$3 = fileLoadOperationStream;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$start$3$FileLoadOperation(this.arg$2, this.arg$3, this.arg$4);
    }
}
