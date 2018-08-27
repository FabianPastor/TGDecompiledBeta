package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SearchAdapterHelper$$Lambda$9 implements Runnable {
    private final SearchAdapterHelper arg$1;
    private final int arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;
    private final String arg$5;

    SearchAdapterHelper$$Lambda$9(SearchAdapterHelper searchAdapterHelper, int i, TL_error tL_error, TLObject tLObject, String str) {
        this.arg$1 = searchAdapterHelper;
        this.arg$2 = i;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
        this.arg$5 = str;
    }

    public void run() {
        this.arg$1.lambda$null$2$SearchAdapterHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
