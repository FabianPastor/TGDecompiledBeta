package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class BaseLocationAdapter$$Lambda$2 implements Runnable {
    private final BaseLocationAdapter arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    BaseLocationAdapter$$Lambda$2(BaseLocationAdapter baseLocationAdapter, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = baseLocationAdapter;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$2$BaseLocationAdapter(this.arg$2, this.arg$3);
    }
}
