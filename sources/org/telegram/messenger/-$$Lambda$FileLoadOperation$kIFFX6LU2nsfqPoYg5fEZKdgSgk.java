package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$kIFFX6LU2nsfqPoYg5fEZKdgSgk implements Runnable {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ FileLoadOperationStream f$1;

    public /* synthetic */ -$$Lambda$FileLoadOperation$kIFFX6LU2nsfqPoYg5fEZKdgSgk(FileLoadOperation fileLoadOperation, FileLoadOperationStream fileLoadOperationStream) {
        this.f$0 = fileLoadOperation;
        this.f$1 = fileLoadOperationStream;
    }

    public final void run() {
        this.f$0.lambda$removeStreamListener$2$FileLoadOperation(this.f$1);
    }
}
