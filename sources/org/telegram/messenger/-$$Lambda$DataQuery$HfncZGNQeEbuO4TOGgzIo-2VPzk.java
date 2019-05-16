package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$HfncZGNQeEbuO4TOGgzIo-2VPzk implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Document f$2;

    public /* synthetic */ -$$Lambda$DataQuery$HfncZGNQeEbuO4TOGgzIo-2VPzk(DataQuery dataQuery, int i, Document document) {
        this.f$0 = dataQuery;
        this.f$1 = i;
        this.f$2 = document;
    }

    public final void run() {
        this.f$0.lambda$addRecentSticker$1$DataQuery(this.f$1, this.f$2);
    }
}
