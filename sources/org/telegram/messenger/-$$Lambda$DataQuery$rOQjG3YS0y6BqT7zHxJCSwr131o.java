package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$rOQjG3YS0y6BqT7zHxJCSwr131o implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$DataQuery$rOQjG3YS0y6BqT7zHxJCSwr131o(DataQuery dataQuery, int i, int i2) {
        this.f$0 = dataQuery;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadStickers$35$DataQuery(this.f$1, this.f$2, tLObject, tL_error);
    }
}
