package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ TLRPC.Document f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda7(FileLoader fileLoader, TLRPC.Document document, boolean z) {
        this.f$0 = fileLoader;
        this.f$1 = document;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.m640lambda$setLoadingVideo$0$orgtelegrammessengerFileLoader(this.f$1, this.f$2);
    }
}
