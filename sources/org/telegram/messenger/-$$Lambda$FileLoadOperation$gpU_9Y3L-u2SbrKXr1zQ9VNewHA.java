package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$gpU_9Y3L-u2SbrKXr1zQ9VNewHA implements Runnable {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ FileLoadOperationStream f$1;

    public /* synthetic */ -$$Lambda$FileLoadOperation$gpU_9Y3L-u2SbrKXr1zQ9VNewHA(FileLoadOperation fileLoadOperation, FileLoadOperationStream fileLoadOperationStream) {
        this.f$0 = fileLoadOperation;
        this.f$1 = fileLoadOperationStream;
    }

    public final void run() {
        this.f$0.lambda$removeStreamListener$3$FileLoadOperation(this.f$1);
    }
}
