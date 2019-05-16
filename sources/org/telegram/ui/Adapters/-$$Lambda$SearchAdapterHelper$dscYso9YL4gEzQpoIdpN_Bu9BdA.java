package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SearchAdapterHelper$dscYso9YL4gEzQpoIdpN_Bu9BdA implements RequestDelegate {
    private final /* synthetic */ SearchAdapterHelper f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ String f$5;

    public /* synthetic */ -$$Lambda$SearchAdapterHelper$dscYso9YL4gEzQpoIdpN_Bu9BdA(SearchAdapterHelper searchAdapterHelper, int i, boolean z, boolean z2, boolean z3, String str) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = z2;
        this.f$4 = z3;
        this.f$5 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$queryServerSearch$3$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
