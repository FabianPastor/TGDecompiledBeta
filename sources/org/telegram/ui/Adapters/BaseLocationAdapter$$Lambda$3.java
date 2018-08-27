package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class BaseLocationAdapter$$Lambda$3 implements Runnable {
    private final BaseLocationAdapter arg$1;
    private final TLObject arg$2;

    BaseLocationAdapter$$Lambda$3(BaseLocationAdapter baseLocationAdapter, TLObject tLObject) {
        this.arg$1 = baseLocationAdapter;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$0$BaseLocationAdapter(this.arg$2);
    }
}
