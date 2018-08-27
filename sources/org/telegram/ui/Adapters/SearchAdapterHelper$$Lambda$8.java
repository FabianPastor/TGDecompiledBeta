package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SearchAdapterHelper$$Lambda$8 implements Runnable {
    private final SearchAdapterHelper arg$1;
    private final int arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;
    private final boolean arg$5;
    private final boolean arg$6;
    private final boolean arg$7;
    private final String arg$8;

    SearchAdapterHelper$$Lambda$8(SearchAdapterHelper searchAdapterHelper, int i, TL_error tL_error, TLObject tLObject, boolean z, boolean z2, boolean z3, String str) {
        this.arg$1 = searchAdapterHelper;
        this.arg$2 = i;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
        this.arg$5 = z;
        this.arg$6 = z2;
        this.arg$7 = z3;
        this.arg$8 = str;
    }

    public void run() {
        this.arg$1.lambda$null$4$SearchAdapterHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
