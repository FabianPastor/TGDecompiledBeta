package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SearchAdapterHelper$$Lambda$2 implements RequestDelegate {
    private final SearchAdapterHelper arg$1;
    private final int arg$2;
    private final boolean arg$3;
    private final boolean arg$4;
    private final boolean arg$5;
    private final String arg$6;

    SearchAdapterHelper$$Lambda$2(SearchAdapterHelper searchAdapterHelper, int i, boolean z, boolean z2, boolean z3, String str) {
        this.arg$1 = searchAdapterHelper;
        this.arg$2 = i;
        this.arg$3 = z;
        this.arg$4 = z2;
        this.arg$5 = z3;
        this.arg$6 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$queryServerSearch$5$SearchAdapterHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, tLObject, tL_error);
    }
}
