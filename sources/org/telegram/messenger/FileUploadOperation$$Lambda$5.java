package org.telegram.messenger;

import org.telegram.tgnet.WriteToSocketDelegate;

final /* synthetic */ class FileUploadOperation$$Lambda$5 implements WriteToSocketDelegate {
    private final FileUploadOperation arg$1;

    FileUploadOperation$$Lambda$5(FileUploadOperation fileUploadOperation) {
        this.arg$1 = fileUploadOperation;
    }

    public void run() {
        this.arg$1.lambda$startUploadRequest$6$FileUploadOperation();
    }
}
