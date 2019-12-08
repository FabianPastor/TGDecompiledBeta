package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoader$Y4NJqKKZ1P8H4f3HcmH_MDyWMYY implements Runnable {
    private final /* synthetic */ FileLoader f$0;
    private final /* synthetic */ FileLoadOperation[] f$1;
    private final /* synthetic */ Document f$2;
    private final /* synthetic */ Object f$3;
    private final /* synthetic */ FileLoadOperationStream f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ CountDownLatch f$7;

    public /* synthetic */ -$$Lambda$FileLoader$Y4NJqKKZ1P8H4f3HcmH_MDyWMYY(FileLoader fileLoader, FileLoadOperation[] fileLoadOperationArr, Document document, Object obj, FileLoadOperationStream fileLoadOperationStream, int i, boolean z, CountDownLatch countDownLatch) {
        this.f$0 = fileLoader;
        this.f$1 = fileLoadOperationArr;
        this.f$2 = document;
        this.f$3 = obj;
        this.f$4 = fileLoadOperationStream;
        this.f$5 = i;
        this.f$6 = z;
        this.f$7 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$loadStreamFile$8$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
