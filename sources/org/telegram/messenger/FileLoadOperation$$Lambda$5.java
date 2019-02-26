package org.telegram.messenger;

final /* synthetic */ class FileLoadOperation$$Lambda$5 implements Runnable {
    private final FileLoadOperation arg$1;
    private final boolean[] arg$2;

    FileLoadOperation$$Lambda$5(FileLoadOperation fileLoadOperation, boolean[] zArr) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = zArr;
    }

    public void run() {
        this.arg$1.lambda$start$4$FileLoadOperation(this.arg$2);
    }
}
