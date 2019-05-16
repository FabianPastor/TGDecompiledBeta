package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$A03PPqgH8dlVye1igOOaKjX18Mo implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;

    public /* synthetic */ -$$Lambda$DataQuery$A03PPqgH8dlVye1igOOaKjX18Mo(DataQuery dataQuery) {
        this.f$0 = dataQuery;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadDrafts$98$DataQuery(tLObject, tL_error);
    }
}
