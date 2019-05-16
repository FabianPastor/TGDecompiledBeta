package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$qjcWF-Rlu499xbvvlZfzwoJxsQI implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ BaseFragment f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$DataQuery$qjcWF-Rlu499xbvvlZfzwoJxsQI(DataQuery dataQuery, int i, int i2, BaseFragment baseFragment, boolean z) {
        this.f$0 = dataQuery;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = baseFragment;
        this.f$4 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$removeStickersSet$44$DataQuery(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
