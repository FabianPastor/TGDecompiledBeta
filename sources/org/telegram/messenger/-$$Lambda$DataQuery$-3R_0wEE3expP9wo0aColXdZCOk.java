package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$-3R_0wEE3expP9wo0aColXdZCOk implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ Document f$1;

    public /* synthetic */ -$$Lambda$DataQuery$-3R_0wEE3expP9wo0aColXdZCOk(DataQuery dataQuery, Document document) {
        this.f$0 = dataQuery;
        this.f$1 = document;
    }

    public final void run() {
        this.f$0.lambda$removeRecentGif$3$DataQuery(this.f$1);
    }
}
