package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SearchAdapterHelper$$Lambda$0 implements RequestDelegate {
    private final SearchAdapterHelper arg$1;
    private final int arg$2;
    private final String arg$3;

    SearchAdapterHelper$$Lambda$0(SearchAdapterHelper searchAdapterHelper, int i, String str) {
        this.arg$1 = searchAdapterHelper;
        this.arg$2 = i;
        this.arg$3 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$queryServerSearch$1$SearchAdapterHelper(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
