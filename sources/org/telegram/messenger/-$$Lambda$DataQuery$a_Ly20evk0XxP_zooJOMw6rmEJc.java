package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$a_Ly20evk0XxP_zooJOMw6rmEJc implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$DataQuery$a_Ly20evk0XxP_zooJOMw6rmEJc(DataQuery dataQuery, long j) {
        this.f$0 = dataQuery;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$101$DataQuery(this.f$1, tLObject, tL_error);
    }
}
