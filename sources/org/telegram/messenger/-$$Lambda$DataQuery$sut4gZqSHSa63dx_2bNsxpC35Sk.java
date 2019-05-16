package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$sut4gZqSHSa63dx_2bNsxpCLASSNAMESk implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$DataQuery$sut4gZqSHSa63dx_2bNsxpCLASSNAMESk(DataQuery dataQuery, int i) {
        this.f$0 = dataQuery;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadPinnedMessageInternal$86$DataQuery(this.f$1, tLObject, tL_error);
    }
}
