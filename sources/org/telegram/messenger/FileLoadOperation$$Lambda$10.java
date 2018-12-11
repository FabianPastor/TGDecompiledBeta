package org.telegram.messenger;

final /* synthetic */ class FileLoadOperation$$Lambda$10 implements Runnable {
    private final FileLoadOperation arg$1;
    private final int arg$2;

    FileLoadOperation$$Lambda$10(FileLoadOperation fileLoadOperation, int i) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$onFail$9$FileLoadOperation(this.arg$2);
    }
}
