package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$LGTT4xzHAhL56fxY9zS2yMdk6qA implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ TL_messages_getFeaturedStickers f$2;

    public /* synthetic */ -$$Lambda$DataQuery$LGTT4xzHAhL56fxY9zS2yMdk6qA(DataQuery dataQuery, TLObject tLObject, TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        this.f$0 = dataQuery;
        this.f$1 = tLObject;
        this.f$2 = tL_messages_getFeaturedStickers;
    }

    public final void run() {
        this.f$0.lambda$null$18$DataQuery(this.f$1, this.f$2);
    }
}
