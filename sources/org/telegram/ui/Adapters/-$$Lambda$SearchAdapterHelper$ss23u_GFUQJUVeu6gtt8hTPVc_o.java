package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SearchAdapterHelper$ss23u_GFUQJUVeu6gtt8hTPVc_o implements RequestDelegate {
    private final /* synthetic */ SearchAdapterHelper f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$SearchAdapterHelper$ss23u_GFUQJUVeu6gtt8hTPVc_o(SearchAdapterHelper searchAdapterHelper, int i, String str) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$queryServerSearch$1$SearchAdapterHelper(this.f$1, this.f$2, tLObject, tL_error);
    }
}
