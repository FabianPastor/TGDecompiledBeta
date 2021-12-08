package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SearchAdapterHelper$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ SearchAdapterHelper f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ int f$6;

    public /* synthetic */ SearchAdapterHelper$$ExternalSyntheticLambda3(SearchAdapterHelper searchAdapterHelper, int i, TLRPC.TL_error tL_error, TLObject tLObject, String str, boolean z, int i2) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = str;
        this.f$5 = z;
        this.f$6 = i2;
    }

    public final void run() {
        this.f$0.m1394xb4a01a4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
