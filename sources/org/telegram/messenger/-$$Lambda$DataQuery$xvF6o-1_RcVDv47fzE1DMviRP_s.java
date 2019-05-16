package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$xvF6o-1_RcVDv47fzE1DMviRP_s implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ Document f$1;

    public /* synthetic */ -$$Lambda$DataQuery$xvF6o-1_RcVDv47fzE1DMviRP_s(DataQuery dataQuery, Document document) {
        this.f$0 = dataQuery;
        this.f$1 = document;
    }

    public final void run() {
        this.f$0.lambda$addRecentGif$4$DataQuery(this.f$1);
    }
}
