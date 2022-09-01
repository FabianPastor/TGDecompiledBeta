package org.telegram.messenger;

public final /* synthetic */ class FileLoadOperation$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ FileLoadOperation f$0;
    public final /* synthetic */ FileLoadOperationStream f$1;

    public /* synthetic */ FileLoadOperation$$ExternalSyntheticLambda4(FileLoadOperation fileLoadOperation, FileLoadOperationStream fileLoadOperationStream) {
        this.f$0 = fileLoadOperation;
        this.f$1 = fileLoadOperationStream;
    }

    public final void run() {
        this.f$0.lambda$removeStreamListener$4(this.f$1);
    }
}
