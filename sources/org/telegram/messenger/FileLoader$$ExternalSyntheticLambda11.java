package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ FileLoadOperation[] f$1;
    public final /* synthetic */ TLRPC$Document f$2;
    public final /* synthetic */ ImageLocation f$3;
    public final /* synthetic */ Object f$4;
    public final /* synthetic */ FileLoadOperationStream f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ CountDownLatch f$8;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda11(FileLoader fileLoader, FileLoadOperation[] fileLoadOperationArr, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, FileLoadOperationStream fileLoadOperationStream, int i, boolean z, CountDownLatch countDownLatch) {
        this.f$0 = fileLoader;
        this.f$1 = fileLoadOperationArr;
        this.f$2 = tLRPC$Document;
        this.f$3 = imageLocation;
        this.f$4 = obj;
        this.f$5 = fileLoadOperationStream;
        this.f$6 = i;
        this.f$7 = z;
        this.f$8 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$loadStreamFile$9(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
