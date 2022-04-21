package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SearchAdapterHelper$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ SearchAdapterHelper f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ SearchAdapterHelper$$ExternalSyntheticLambda7(SearchAdapterHelper searchAdapterHelper, String str, boolean z) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = str;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1354xb4a01a4(this.f$1, this.f$2, tLObject, tL_error);
    }
}
