package org.telegram.messenger;

final /* synthetic */ class FileLoadOperation$$Lambda$5 implements Runnable {
    private final FileLoadOperation arg$1;

    FileLoadOperation$$Lambda$5(FileLoadOperation fileLoadOperation) {
        this.arg$1 = fileLoadOperation;
    }

    public void run() {
        this.arg$1.startDownloadRequest();
    }
}
