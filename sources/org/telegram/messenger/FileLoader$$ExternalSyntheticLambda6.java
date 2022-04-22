package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ TLRPC$Document f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda6(FileLoader fileLoader, TLRPC$Document tLRPC$Document, boolean z) {
        this.f$0 = fileLoader;
        this.f$1 = tLRPC$Document;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$setLoadingVideo$0(this.f$1, this.f$2);
    }
}
