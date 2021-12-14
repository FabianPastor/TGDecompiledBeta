package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SearchAdapterHelper$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ SearchAdapterHelper f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ SearchAdapterHelper$$ExternalSyntheticLambda8(SearchAdapterHelper searchAdapterHelper, int i, String str, boolean z, int i2) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = z;
        this.f$4 = i2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$queryServerSearch$1(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
