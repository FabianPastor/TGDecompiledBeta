package org.telegram.messenger;

final /* synthetic */ class FileUploadOperation$$Lambda$1 implements Runnable {
    private final FileUploadOperation arg$1;
    private final boolean arg$2;

    FileUploadOperation$$Lambda$1(FileUploadOperation fileUploadOperation, boolean z) {
        this.arg$1 = fileUploadOperation;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$onNetworkChanged$1$FileUploadOperation(this.arg$2);
    }
}
