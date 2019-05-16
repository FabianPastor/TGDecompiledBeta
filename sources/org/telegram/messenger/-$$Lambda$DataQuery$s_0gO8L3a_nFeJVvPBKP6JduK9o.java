package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_faveSticker;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$s_0gO8L3a_nFeJVvPBKP6JduK9o implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ Object f$1;
    private final /* synthetic */ TL_messages_faveSticker f$2;

    public /* synthetic */ -$$Lambda$DataQuery$s_0gO8L3a_nFeJVvPBKP6JduK9o(DataQuery dataQuery, Object obj, TL_messages_faveSticker tL_messages_faveSticker) {
        this.f$0 = dataQuery;
        this.f$1 = obj;
        this.f$2 = tL_messages_faveSticker;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$addRecentSticker$0$DataQuery(this.f$1, this.f$2, tLObject, tL_error);
    }
}
