package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoader$5K2sGZscq7bEKvLM0V2ywbk86iM implements Runnable {
    private final /* synthetic */ FileLoader f$0;
    private final /* synthetic */ Document f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$FileLoader$5K2sGZscq7bEKvLM0V2ywbk86iM(FileLoader fileLoader, Document document, boolean z) {
        this.f$0 = fileLoader;
        this.f$1 = document;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$setLoadingVideo$0$FileLoader(this.f$1, this.f$2);
    }
}
