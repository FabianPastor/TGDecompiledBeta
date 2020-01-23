package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SearchAdapterHelper$TvNOKs_82NxNKUbKEl6ilcDLjOM implements RequestDelegate {
    private final /* synthetic */ SearchAdapterHelper f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$SearchAdapterHelper$TvNOKs_82NxNKUbKEl6ilcDLjOM(SearchAdapterHelper searchAdapterHelper, int i, String str, boolean z, int i2) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = z;
        this.f$4 = i2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$queryServerSearch$1$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
