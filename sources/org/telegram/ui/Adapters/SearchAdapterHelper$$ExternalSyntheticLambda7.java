package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SearchAdapterHelper$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ SearchAdapterHelper f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ SearchAdapterHelper$$ExternalSyntheticLambda7(SearchAdapterHelper searchAdapterHelper, String str, boolean z) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = str;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$queryServerSearch$0(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
