package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SearchAdapterHelper$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ SearchAdapterHelper f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ String f$7;

    public /* synthetic */ SearchAdapterHelper$$ExternalSyntheticLambda7(SearchAdapterHelper searchAdapterHelper, int i, int i2, boolean z, boolean z2, boolean z3, boolean z4, String str) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = z3;
        this.f$6 = z4;
        this.f$7 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$queryServerSearch$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
    }
}
