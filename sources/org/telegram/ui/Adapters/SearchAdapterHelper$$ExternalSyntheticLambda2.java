package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SearchAdapterHelper$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ SearchAdapterHelper f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;
    public final /* synthetic */ TLObject f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ String f$9;

    public /* synthetic */ SearchAdapterHelper$$ExternalSyntheticLambda2(SearchAdapterHelper searchAdapterHelper, int i, int i2, TLRPC.TL_error tL_error, TLObject tLObject, boolean z, boolean z2, boolean z3, boolean z4, String str) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tL_error;
        this.f$4 = tLObject;
        this.f$5 = z;
        this.f$6 = z2;
        this.f$7 = z3;
        this.f$8 = z4;
        this.f$9 = str;
    }

    public final void run() {
        this.f$0.m1396x7e391ce2(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
