package org.telegram.messenger;

final /* synthetic */ class FileUploadOperation$$Lambda$3 implements Runnable {
    private final FileUploadOperation arg$1;
    private final long arg$2;
    private final long arg$3;

    FileUploadOperation$$Lambda$3(FileUploadOperation fileUploadOperation, long j, long j2) {
        this.arg$1 = fileUploadOperation;
        this.arg$2 = j;
        this.arg$3 = j2;
    }

    public void run() {
        this.arg$1.lambda$checkNewDataAvailable$3$FileUploadOperation(this.arg$2, this.arg$3);
    }
}
