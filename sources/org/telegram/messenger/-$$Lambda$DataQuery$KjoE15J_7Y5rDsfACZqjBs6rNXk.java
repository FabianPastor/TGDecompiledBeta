package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$KjoE15J_7Y5rDsfACZqjBs6rNXk implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ TL_messages_getFeaturedStickers f$1;

    public /* synthetic */ -$$Lambda$DataQuery$KjoE15J_7Y5rDsfACZqjBs6rNXk(DataQuery dataQuery, TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        this.f$0 = dataQuery;
        this.f$1 = tL_messages_getFeaturedStickers;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadFeaturedStickers$19$DataQuery(this.f$1, tLObject, tL_error);
    }
}
