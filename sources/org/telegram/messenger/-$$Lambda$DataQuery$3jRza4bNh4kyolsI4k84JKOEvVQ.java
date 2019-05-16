package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$3jRza4bNh4kyolsI4k84JKOEvVQ implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ String f$3;

    public /* synthetic */ -$$Lambda$DataQuery$3jRza4bNh4kyolsI4k84JKOEvVQ(DataQuery dataQuery, int i, String str, String str2) {
        this.f$0 = dataQuery;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = str2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$115$DataQuery(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
