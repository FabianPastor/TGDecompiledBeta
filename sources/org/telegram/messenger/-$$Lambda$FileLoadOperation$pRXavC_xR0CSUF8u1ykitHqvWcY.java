package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$pRXavC_xR0CSUF8u1ykitHqvWcY implements Runnable {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ FileStreamLoadOperation f$1;

    public /* synthetic */ -$$Lambda$FileLoadOperation$pRXavC_xR0CSUF8u1ykitHqvWcY(FileLoadOperation fileLoadOperation, FileStreamLoadOperation fileStreamLoadOperation) {
        this.f$0 = fileLoadOperation;
        this.f$1 = fileStreamLoadOperation;
    }

    public final void run() {
        this.f$0.lambda$removeStreamListener$2$FileLoadOperation(this.f$1);
    }
}
