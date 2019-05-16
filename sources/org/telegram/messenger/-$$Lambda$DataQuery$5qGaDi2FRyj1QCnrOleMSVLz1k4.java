package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$5qGaDi2FRyj1QCnrOleMSVLz1k4 implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$DataQuery$5qGaDi2FRyj1QCnrOleMSVLz1k4(DataQuery dataQuery, int i, boolean z) {
        this.f$0 = dataQuery;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadRecents$13$DataQuery(this.f$1, this.f$2, tLObject, tL_error);
    }
}
