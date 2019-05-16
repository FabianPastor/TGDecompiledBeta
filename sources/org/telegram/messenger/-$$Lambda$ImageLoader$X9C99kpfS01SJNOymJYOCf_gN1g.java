package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$X9CLASSNAMEkpfS01SJNOymJYOCf_gN1g implements Runnable {
    private final /* synthetic */ ImageLoader f$0;
    private final /* synthetic */ HttpFileTask f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$ImageLoader$X9CLASSNAMEkpfS01SJNOymJYOCf_gN1g(ImageLoader imageLoader, HttpFileTask httpFileTask, int i) {
        this.f$0 = imageLoader;
        this.f$1 = httpFileTask;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$runHttpFileLoadTasks$11$ImageLoader(this.f$1, this.f$2);
    }
}
