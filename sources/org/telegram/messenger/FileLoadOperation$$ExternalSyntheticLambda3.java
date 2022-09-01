package org.telegram.messenger;

import org.telegram.messenger.FileLoadOperation;

public final /* synthetic */ class FileLoadOperation$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ FileLoadOperation f$0;
    public final /* synthetic */ FileLoadOperation.RequestInfo f$1;

    public /* synthetic */ FileLoadOperation$$ExternalSyntheticLambda3(FileLoadOperation fileLoadOperation, FileLoadOperation.RequestInfo requestInfo) {
        this.f$0 = fileLoadOperation;
        this.f$1 = requestInfo;
    }

    public final void run() {
        this.f$0.lambda$startDownloadRequest$12(this.f$1);
    }
}
